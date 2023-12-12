package pwr.edu.ilab.data

import pwr.edu.ilab.utils.Resource
import kotlinx.coroutines.flow.Flow
import pwr.edu.ilab.models.*

interface DbRepository {
    fun addRegistrationUserInfo(
        name: String,
        surname: String,
        pesel: String,
        email: String,
    ): Flow<Resource<String>>

    suspend fun getUserInfo(email: String): Resource<UserInfo>
    suspend fun getBundles(): Resource<List<BundleInfo>>
    suspend fun getAllTests(): Resource<List<DbTestInfo>>
    suspend fun getNewBundles(): Resource<List<DbBundleInfo>>
    suspend fun getAllResultsEnteredByAssistant(
        assistantEmail: String,
    ): Resource<List<ResultsEnteredByAssistant>>
    suspend fun removeSelectedResult(
        pesel: String,
        dateWithDots: String,
        assistantEmail: String
    ): Resource<String>
    suspend fun addToAssistantsEnteredResults(
        pesel: String,
        dateWithDots: String,
        assistantEmail: String,
    )
    suspend fun submitResults(
        pesel: String,
        dateWithDots: String,
        testResultsName: String,
        testResultsNames: List<String>,
        testResults: MutableMap<String, Number>,
        assistantEmail: String,
    ): Flow<Resource<String>>
    suspend fun getAllTestResults(pesel: String): Resource<List<TestResultsInfo>>
    suspend fun getSingleTestResults(pesel: String, dateWithDots: String): Resource<SingleTestResults>
    suspend fun getAllSingleTestResults(pesel: String): Resource<Map<String, SingleTestResults>>
}