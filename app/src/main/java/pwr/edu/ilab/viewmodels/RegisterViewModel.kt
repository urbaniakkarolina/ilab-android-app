package pwr.edu.ilab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.utils.State
import pwr.edu.ilab.utils.VerificationResult
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val dbRepo: DbRepository,
) : ViewModel() {
    private val _signInState = Channel<State>()
    val signInState = _signInState.receiveAsFlow()
    private val _registerInfoState = Channel<State>()
    val registerInfoState = _registerInfoState.receiveAsFlow()

    fun registerUser(
        name: String,
        surname: String,
        pesel: String,
        email: String,
        password: String,
        passwordVerification: String,
    ) = viewModelScope.launch {
        _registerInfoState.send(State(isLoading = true))
        val verificationResults =
            verifyUserRegistrationInfo(name, surname, pesel, email, password, passwordVerification)

        if (verificationResults.all { it.isSuccess }) {
            _registerInfoState.send(State(isSuccess = "All user info verified!"))
            registerUser(email, password).join()
            addUserInfoToDb(name, surname, pesel, email)
        } else {
            val errorMsg = joinErrorMsgsFromVerificationResults(verificationResults)
            _registerInfoState.send(State(isError = errorMsg))
        }
    }

    private fun verifyUserRegistrationInfo(
        name: String,
        surname: String,
        pesel: String,
        email: String,
        password: String,
        passwordVerification: String,
    ): List<VerificationResult> {
        return listOf(
            verifyName(name),
            verifyName(surname),
            verifyPassword(password, passwordVerification),
            verifyPesel(pesel),
            verifyEmail(email),
        )
    }

    private fun joinErrorMsgsFromVerificationResults(results: List<VerificationResult>): String {
        return results
            .filter { result -> !result.isSuccess }
            .map { result -> result.msg }
            .reduce { finalMsg, msg -> finalMsg + "\n" + msg }
    }

    private fun verifyName(name: String): VerificationResult {
        val isAtLeastThreeChars = name.length > 2
        if (!isAtLeastThreeChars) return VerificationResult(
            false,
            "Name must be at least 3 characters long"
        )

        return VerificationResult(true, "Name is valid")
    }

    private fun verifyEmail(email: String): VerificationResult {
        return VerificationResult(true, "Email appears to be valid")
    }

    private fun verifyPassword(password: String, passwordVerification: String): VerificationResult {
        val isPasswordLongEnough = password.length > 5
        if (!isPasswordLongEnough) return VerificationResult(
            false, "Password must have at least 5 characters"
        )

        val isPasswordMatching = password == passwordVerification
        if (!isPasswordMatching) return VerificationResult(false, "Passwords must match")

        return VerificationResult(true, "Password appears to be valid")
    }

    private fun verifyPesel(pesel: String): VerificationResult {
        val correctLength = pesel.length == 11
        if (!correctLength) return VerificationResult(false, "PESEL must have 11 characters")

        return VerificationResult(true, "PESEL appears to be valid")
    }

    private fun registerUser(email: String, password: String) = viewModelScope.launch {
        authRepo.registerUser(email, password).collect { result -> handleRegisterResult(result) }
    }

    private fun addUserInfoToDb(
        name: String,
        surname: String,
        pesel: String,
        email: String
    ) = viewModelScope.launch {
        dbRepo.addRegistrationUserInfo(name, surname, pesel, email)
            .collect { result -> handleAddingInfoToDbResult(result) }
    }

    private suspend fun handleRegisterResult(result: Resource<AuthResult>) {
        when (result) {
            is Resource.Loading -> _signInState.send(State(isLoading = true))
            is Resource.Success -> _signInState.send(State(isSuccess = "Sign up success"))
            is Resource.Error -> _signInState.send(State(isError = "Sign up error"))
        }
    }

    private suspend fun handleAddingInfoToDbResult(result: Resource<String>) {
        when (result) {
            is Resource.Loading -> _registerInfoState.send(State(isLoading = true))
            is Resource.Success -> _registerInfoState.send(State(isSuccess = result.toString()))
            is Resource.Error -> _registerInfoState.send(State(isError = result.toString()))
        }
    }

}
