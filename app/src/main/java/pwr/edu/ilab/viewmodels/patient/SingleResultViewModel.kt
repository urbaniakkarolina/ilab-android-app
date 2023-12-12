package pwr.edu.ilab.viewmodels.patient

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.SingleTestResults
import pwr.edu.ilab.utils.Resource
import javax.inject.Inject

@HiltViewModel
class SingleResultViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val dbRepo: DbRepository,
) : ViewModel() {
    val signInState: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    val singleTestResults: MutableStateFlow<Resource<SingleTestResults>> =
        MutableStateFlow(Resource.Loading())
    val allTestResults: MutableStateFlow<Resource<Map<String, SingleTestResults>>> =
        MutableStateFlow(Resource.Loading())

    suspend fun getTestResults(date: String) {
        getAllTestResults(date)
    }

    private suspend fun getSingleTestResults(date: String) {
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
                            val dbSingleTestResults = dbRepo.getSingleTestResults(pesel, date)

                            singleTestResults.update {
                                when (dbSingleTestResults) {
                                    is Resource.Loading -> Resource.Loading()
                                    is Resource.Error -> Resource.Error(dbSingleTestResults.message!!)
                                    is Resource.Success -> Resource.Success(dbSingleTestResults.data!!)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getAllTestResults(dateWithDots: String) {
        val date = dateWithDots.replace(".", "-")

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
                            when (val dbAllTestResults = dbRepo.getAllSingleTestResults(pesel)) {
                                is Resource.Loading -> {
                                    allTestResults.update {
                                        Resource.Loading()
                                    }
                                    singleTestResults.update {
                                        Resource.Loading()
                                    }
                                }

                                is Resource.Error -> {
                                    allTestResults.update {
                                        Resource.Error("Bad error")
                                    }
                                    singleTestResults.update {
                                        Resource.Error("Bad error")
                                    }
                                }

                                is Resource.Success -> {
                                    allTestResults.update {
                                        Resource.Success(dbAllTestResults.data!!)
                                    }
                                    singleTestResults.update {
                                        Resource.Success(dbAllTestResults.data!![date]!!)
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

    }

}
