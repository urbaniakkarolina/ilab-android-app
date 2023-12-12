package pwr.edu.ilab.viewmodels

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pwr.edu.ilab.R
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.UserInfo
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.utils.State
import javax.inject.Inject

enum class SignInType {
    EMAIL, GOOGLE, FACEBOOK
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository, private val dbRepo: DbRepository
) : ViewModel() {
    private val _state = Channel<State>()
    val signInState = _state.receiveAsFlow()
    val resetState: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    private var oneTapClient: SignInClient? = null
    private var context: Context? = null

    fun provideOneTapClientAndContext(client: SignInClient, context: Context) {
        oneTapClient = client
        this.context = context
    }

    fun createIntent(onSuccess: (IntentSenderRequest) -> Unit) {
        val request = context?.let {
            GetSignInIntentRequest.builder().setServerClientId(it.getString(R.string.web_client_id))
                .build()
        }
        if (request != null) {
            oneTapClient?.getSignInIntent(request)?.addOnSuccessListener { result: PendingIntent ->
                try {
                    onSuccess(
                        IntentSenderRequest.Builder(
                            result.intentSender
                        ).build()
                    )

                } catch (e: IntentSender.SendIntentException) {
                    Log.e(ContentValues.TAG, "Google Sign-in failed")
                }
            }?.addOnFailureListener { e: Exception? ->
                Log.e(
                    ContentValues.TAG, "Google Sign-in failed", e
                )
            }
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        authRepo.loginUser(email, password)
            .collect { result -> handleLoginResult(result, SignInType.EMAIL) }
    }

    fun loginUserWithGoogle(intent: Intent) = viewModelScope.launch {
        authRepo.loginUserWithGoogleIntent(oneTapClient!!, intent)
            .collect { result -> handleLoginResult(result, SignInType.GOOGLE) }
    }

    fun loginUserWithFacebook(token: AccessToken) = viewModelScope.launch {
        authRepo.loginUserWithFacebook(token).collect { result ->
            handleLoginResult(result, SignInType.FACEBOOK)
        }
    }

    private suspend fun handleLoginResult(result: Resource<AuthResult>, signInType: SignInType) {
        when (signInType) {
            SignInType.EMAIL -> {
                handleLoginWithEmail(result)
            }

            SignInType.GOOGLE -> {
                handleLoginWithGoogle(result)
            }

            SignInType.FACEBOOK -> {
                handleLoginWithFacebook(result)
            }
        }
    }

    private suspend fun handleLoginWithEmail(result: Resource<AuthResult>) {
        when (result) {
            is Resource.Loading -> _state.send(State(isLoading = true))
            is Resource.Success -> {
                when (val isStaff = isLoggedInUserStaff()) {
                    is Resource.Success -> {
                        if (isStaff.data == true) {
                            _state.send(
                                State(
                                    isSuccess = "Sign in success",
                                    isStaff = true
                                )
                            )
                        } else {
                            _state.send(
                                State(
                                    isSuccess = "Sign in success",
                                    isStaff = false
                                )
                            )
                        }
                    }

                    is Resource.Loading -> _state.send(State(isLoading = true))
                    is Resource.Error -> _state.send(State(isError = "Nie można załadować danych użytkownika"))
                }
            }

            is Resource.Error -> _state.send(State(isError = "Sign in error"))
        }
    }

    private suspend fun handleLoginWithGoogle(result: Resource<AuthResult>) {
        when (result) {
            is Resource.Loading -> _state.send(State(isLoading = true))
            is Resource.Error -> _state.send(State(isError = "Couldnt sign in with google"))
            is Resource.Success -> {
                val user = result.data!!.user
                when (user?.email?.let { email -> dbRepo.getUserInfo(email) }) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        val name = user.displayName ?: user.uid
                        val email = user.email ?: user.uid
                        dbRepo.addRegistrationUserInfo(
                            name,
                            user.uid,
                            user.uid,
                            email
                        ).collect { dbResult ->
                            when (dbResult) {
                                is Resource.Loading -> _state.send(State(isLoading = true))
                                is Resource.Error -> _state.send(State(isError = "Couldn't add data to database"))
                                is Resource.Success -> _state.send(State(isSuccess = "Sign in success google, ${email}, first login, added to db"))
                            }
                        }
                    }

                    is Resource.Success -> {
                        _state.send(
                            State(
                                isSuccess = "Sign in success google, ${user.email}, subsequent login",
                                isStaff = false
                            )
                        )

                    }

                    else -> _state.send(State(isError = "Couldn't get users email"))
                }
            }
        }
    }

    private suspend fun handleLoginWithFacebook(result: Resource<AuthResult>) {
        when (result) {
            is Resource.Loading -> _state.send(State(isLoading = true))
            is Resource.Error -> _state.send(State(isError = "Couldnt sign in with facebook"))
            is Resource.Success -> {
                val user = result.data!!.user
                when (user?.email?.let { email -> dbRepo.getUserInfo(email) }) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        val name = user.displayName ?: user.uid
                        val email = user.email ?: user.uid
                        dbRepo.addRegistrationUserInfo(
                            name,
                            user.uid,
                            user.uid,
                            email
                        ).collect { dbResult ->
                            when (dbResult) {
                                is Resource.Loading -> _state.send(State(isLoading = true))
                                is Resource.Error -> _state.send(State(isError = "Couldn't add data to database"))
                                is Resource.Success -> _state.send(State(isSuccess = "Sign in success facebook, ${email}, first login, added to db"))
                            }
                        }
                    }

                    is Resource.Success -> {
                        _state.send(
                            State(
                                isSuccess = "Sign in success facebook, ${user.email}, subsequent login",
                                isStaff = false
                            )
                        )

                    }

                    else -> _state.send(State(isError = "Couldn't get users email"))
                }
            }
        }
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        authRepo.resetPassword(email).collect { result ->
            when (result) {
                is Resource.Loading -> resetState.update { Resource.Loading() }
                is Resource.Error -> resetState.update { Resource.Error(result.message!!) }
                is Resource.Success -> resetState.update { Resource.Success(result.data!!) }
            }
        }
    }

    private suspend fun isLoggedInUserStaff(): Resource<Boolean> {
        var isStaff: Resource<Boolean> = Resource.Loading()
        authRepo.currentUser().collect { result ->
            val userInfo: Resource<UserInfo> = when (result) {
                is Resource.Success -> dbRepo.getUserInfo(result.data!!)
                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error(result.message!!)
            }

            isStaff = when (userInfo) {
                is Resource.Success -> {
                    if (userInfo.data!!.userType == "staff") {
                        Resource.Success(true)
                    } else {
                        Resource.Success(false)
                    }
                }

                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error("Nie można załadować danych użytkownika")
            }
        }

        return isStaff
    }
}
