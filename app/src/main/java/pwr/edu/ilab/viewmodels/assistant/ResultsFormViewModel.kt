package pwr.edu.ilab.viewmodels.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.DbTestInfo
import pwr.edu.ilab.utils.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ResultsFormViewModel @Inject constructor(
    private val dbRepo: DbRepository, private val authRepo: AuthRepository
) : ViewModel() {
    val testsInfoState: MutableStateFlow<Resource<List<DbTestInfo>>> =
        MutableStateFlow(Resource.Loading())
    val validationState: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    val sendingState: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Success(""))
    val assistantEmailState: MutableStateFlow<Resource<String>> =
        MutableStateFlow(Resource.Loading())
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private fun getAllTestsFromDatabase() = viewModelScope.launch {
        when (val dbTests = dbRepo.getAllTests()) {
            is Resource.Loading -> testsInfoState.update { Resource.Loading() }
            is Resource.Success -> testsInfoState.update { Resource.Success(dbTests.data!!) }
            is Resource.Error -> testsInfoState.update { Resource.Error(dbTests.message.orEmpty()) }
        }
    }

    private fun getAssistantEmail() = viewModelScope.launch {
        authRepo.currentUser().collect { user ->

            when (user) {
                is Resource.Loading -> assistantEmailState.update { Resource.Loading() }
                is Resource.Error -> assistantEmailState.update { Resource.Error("Couldn't get assistant email") }
                is Resource.Success -> assistantEmailState.update { Resource.Success(user.data!!) }
            }
        }
    }

    suspend fun validateAndSubmitResults(
        pesel: String,
        date: String,
        testResultsName: String,
        testResultsNames: List<String>,
        testResults: Map<String, String>
    ) {
        if (!validatePesel(pesel)) {
            validationState.update { Resource.Error("PESEL is not valid") }
            return
        }
        if (!validateDate(date)) {
            validationState.update { Resource.Error("Date is not valid. It must have a format of dd.MM.yyyy (ex. 23.06.2023).") }
            return
        }
        if (testResultsName.isEmpty()) {
            validationState.update { Resource.Error("For some reason test results name is empty") }
            return
        }
        if (!validateTestResults(testResults)) {
            validationState.update { Resource.Error("Provided test results are not valid") }
            return
        }

        validationState.update { Resource.Success("All data is valid, submitting..") }
        val testResultsWithNumbers = testResults.map { (key, value) ->
            key to value.toDouble() as Number
        }.toMap().toMutableMap()

        submitResults(
            pesel,
            date,
            testResultsName,
            testResultsNames,
            testResultsWithNumbers,
        )
    }

    private fun submitResults(
        pesel: String,
        date: String,
        testResultsName: String,
        testResultsNames: List<String>,
        testResults: MutableMap<String, Number>,
    ) = viewModelScope.launch {
        sendingState.update { Resource.Loading() }
        dbRepo.submitResults(
            pesel,
            date,
            testResultsName,
            testResultsNames,
            testResults,
            assistantEmailState.value.data!!
        )
            .collect { result ->
                when (result) {
                    is Resource.Success -> sendingState.update { Resource.Success("Everything sent correctly") }
                    is Resource.Error -> sendingState.update { Resource.Error(result.message!!) }
                    is Resource.Loading -> sendingState.update { Resource.Loading() }
                }
            }
    }

    private fun validatePesel(pesel: String): Boolean {
        return pesel.length == 11
    }

    private fun validateDate(dateStr: String): Boolean {
        return try {
            formatter.isLenient = false
            val date = formatter.parse(dateStr)
            println(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun validateTestResults(testResults: Map<String, String>): Boolean {
        try {
            testResults.values.forEach { value ->
                println("trying to convert $value")
                val tryToConvert = value.toDouble()
                println(tryToConvert)
            }
            return true
        } catch (e: Exception) {
            println("oops")
            return false
        }
    }

    init {
        getAllTestsFromDatabase()
        getAssistantEmail()
    }
}
