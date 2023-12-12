package pwr.edu.ilab.viewmodels.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.DbTestInfo
import pwr.edu.ilab.models.ElementDataPoint
import pwr.edu.ilab.models.ElementResult
import pwr.edu.ilab.models.SingleTestResults
import pwr.edu.ilab.models.TestResultsInfo
import pwr.edu.ilab.models.UserInfo
import pwr.edu.ilab.utils.Resource
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val dbRepo: DbRepository,
) : ViewModel() {
    val signInState: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    val testResults: MutableStateFlow<Resource<List<TestResultsInfo>>> =
        MutableStateFlow(Resource.Loading())

    private fun loadResultsForCurrentUser() = viewModelScope.launch {
        authRepo.currentUser().collect { userResult ->
            when (userResult) {
                is Resource.Loading -> signInState.update { Resource.Loading() }
                is Resource.Error -> signInState.update { Resource.Error("Couldn't verify logged in user") }
                is Resource.Success -> {
                    val email = userResult.data!!
                    signInState.update { Resource.Success("Currently logged in as $email") }
                    when (val userInfo = dbRepo.getUserInfo(email)) {
                        is Resource.Loading -> {}
                        is Resource.Error -> {}
                        is Resource.Success -> {
                            val pesel = userInfo.data!!.pesel
                            testResults.update {
                                when (val allTestResults = dbRepo.getAllTestResults(pesel)) {
                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Success -> Resource.Success(allTestResults.data!!)
                                    is Resource.Error -> Resource.Error("Coulnd't load test results from database")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun collectHistoricalData(allResults: List<SingleTestResults>, elementName: String) : List<ElementDataPoint> {
        val historicalData  = mutableListOf<ElementDataPoint>()
        allResults.forEach { result ->
            val dataPoint = ElementDataPoint.fromSingleTestResults(result, elementName)
            if (dataPoint.isPresent) {
                historicalData.add(dataPoint.get())
            }
        }
        return historicalData
    }

    init {
        loadResultsForCurrentUser()
    }
}