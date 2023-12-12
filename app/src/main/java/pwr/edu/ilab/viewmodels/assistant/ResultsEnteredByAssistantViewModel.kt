package pwr.edu.ilab.viewmodels.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.ResultsEnteredByAssistant
import pwr.edu.ilab.utils.Resource
import javax.inject.Inject

@HiltViewModel
class ResultsEnteredByAssistantViewModel @Inject constructor(
    private val dbRepo: DbRepository, private val authRepo: AuthRepository
) : ViewModel() {
    val enteredResults: MutableStateFlow<Resource<List<ResultsEnteredByAssistant>>> =
        MutableStateFlow(Resource.Loading())

    fun getEnteredResults() = viewModelScope.launch {
        authRepo.currentUser().collect { user ->
            when (user) {
                is Resource.Loading -> {}
                is Resource.Error -> enteredResults.update { Resource.Error("Nie moglismy odnalezc Twojego email") }
                is Resource.Success -> enteredResults.update {
                    when (val results = dbRepo.getAllResultsEnteredByAssistant(user.data!!)) {
                        is Resource.Loading -> Resource.Loading()
                        is Resource.Error -> Resource.Error(results.message!!)
                        is Resource.Success -> Resource.Success(results.data!!)
                    }
                }
            }

        }
    }

    fun removeSelectedResult(pesel: String, date: String) = viewModelScope.launch {
        authRepo.currentUser().collect { user ->
            when (user) {
                is Resource.Loading -> {}
                is Resource.Error -> {}
                is Resource.Success -> {
                    dbRepo.removeSelectedResult(pesel, date, user.data!!)
                    getEnteredResults()
                }
            }
        }
    }
}
