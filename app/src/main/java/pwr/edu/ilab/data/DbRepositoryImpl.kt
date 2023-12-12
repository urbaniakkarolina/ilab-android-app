package pwr.edu.ilab.data


import android.sax.Element
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import pwr.edu.ilab.models.*
import pwr.edu.ilab.utils.Resource
import java.lang.reflect.Field
import javax.inject.Inject

class DbRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : DbRepository {
    override suspend fun getUserInfo(email: String): Resource<UserInfo> {
        return try {
            val result = firestore.collection("users").document(email).get().await()
            val name: String = result.getString("name")!!
            val surname = result.getString("surname")!!
            val pesel = result.getString("pesel")!!
            val userType = result.getString("user_type")
            val userInfo = UserInfo(name, surname, email, pesel, userType)
            Resource.Success(userInfo)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun getAllResultsEnteredByAssistant(assistantEmail: String): Resource<List<ResultsEnteredByAssistant>> {
        return try {
            val result =
                firestore
                    .collection("assistant-entered-results")
                    .document(assistantEmail)
                    .get()
                    .await()
                    .data
                    ?: return Resource.Success(listOf())

            val enteredByAssistant = mutableListOf<ResultsEnteredByAssistant>()
            for ((pesel, datesWithDashes) in result) {
                val datesWithDots =
                    (datesWithDashes as List<String>).map { date -> date.replace("-", ".") }
                enteredByAssistant.add(ResultsEnteredByAssistant(pesel, datesWithDots))
            }

            Resource.Success(enteredByAssistant)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun removeSelectedResult(
        pesel: String,
        dateWithDots: String,
        assistantEmail: String
    ): Resource<String> {
        return try {
            val date = dateWithDots.replace(".", "-")
            val userResultsDocument = firestore.collection("results").document(pesel)
            val fieldPath = FieldPath.of(date)
            userResultsDocument.update(fieldPath, FieldValue.delete())

            val assistantResults =
                firestore.collection("assistant-entered-results").document(assistantEmail).get()
                    .await().data!![pesel] as MutableList<String>
            assistantResults.remove(date)
            val updatedResults = mapOf(pesel to assistantResults)
            firestore.collection("assistant-entered-results").document(assistantEmail)
                .set(updatedResults, SetOptions.merge()).await()
            if (assistantResults.isEmpty()) {
                val update = hashMapOf<String, Any>(
                    pesel to FieldValue.delete()
                )
                firestore.collection("assistant-entered-results").document(assistantEmail)
                    .update(update).await()
            }
            Resource.Success("Successful removal of entry")
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun addToAssistantsEnteredResults(
        pesel: String,
        dateWithDots: String,
        assistantEmail: String,
    ) {
        val date = dateWithDots.replace(".", "-")

        var enteredResults = firestore
            .collection("assistant-entered-results")
            .document(assistantEmail)
            .get()
            .await()
            .data
            ?.get(pesel) as MutableList<String>?

        enteredResults = enteredResults ?: mutableListOf()
        if (!enteredResults.contains(date)) {
            enteredResults.add(date)
        }
        val updatedResults = mapOf(
            pesel to enteredResults
        )

        firestore
            .collection("assistant-entered-results")
            .document(assistantEmail)
            .set(updatedResults, SetOptions.merge())
            .await()
    }

    override suspend fun submitResults(
        pesel: String,
        dateWithDots: String,
        testResultsName: String,
        testResultsNames: List<String>,
        testResults: MutableMap<String, Number>,
        assistantEmail: String
    ): Flow<Resource<String>> {
        val date = dateWithDots.replace(".", "-")

        return flow {
            emit(Resource.Loading())
            try {
                println("emailll: $assistantEmail")
                addToAssistantsEnteredResults(pesel, date, assistantEmail)
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
                return@flow
            }

            try {
                val testResultsInserted: MutableMap<String, Any> = testResults.toMutableMap()
                testResultsInserted["name"] = testResultsName
                testResultsInserted["test_names"] = testResultsNames
                val insertedValues: MutableMap<String, Any> = mutableMapOf(
                    date to testResultsInserted
                )
                firestore.collection("results").document(pesel)
                    .set(insertedValues, SetOptions.merge()).await()
                emit(Resource.Success("Adding test results was successful!"))
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
        }
    }

    override suspend fun getAllTestResults(pesel: String): Resource<List<TestResultsInfo>> {
        return try {
            val usersResults: Map<String, Map<String, Any>> =
                firestore.collection("results").document(pesel).get()
                    .await().data as Map<String, Map<String, Any>>
            var allResults: List<TestResultsInfo> = mutableListOf()
            for ((dateWithDashes, results) in usersResults) {
                val date = dateWithDashes.replace("-", ".")
                println("$date: $results")
                allResults = allResults.plus(TestResultsInfo(results["name"] as String, date))
            }
            println(allResults)
            Resource.Success(allResults)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    fun mapifyTestList(testList: List<DbTestInfo>): Map<String, Map<String, ElementInfo>> {
        val testMap = mutableMapOf<String, Map<String, ElementInfo>>()

        for (test in testList) {
            testMap[test.name] = test.elements
        }

        return testMap
    }

    override suspend fun getSingleTestResults(
        pesel: String, dateWithDots: String
    ): Resource<SingleTestResults> {
        val date = dateWithDots.replace(".", "-")
        return try {
            val allTestsMap = mapifyTestList(getAllTests().data!!)

            for ((key, value) in allTestsMap) {
                println("ayyy $key :-> $value")
            }

            val usersResults = firestore
                .collection("results")
                .document(pesel)
                .get()
                .await()
                .data as Map<String, Any>

            val singleTestResults =
                parseSingleTestResultsFromAllResults(usersResults, allTestsMap, date)
            Resource.Success(singleTestResults)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    private fun parseSingleTestResultsFromAllResults(
        allResults: Map<String, Any>,
        allTests: Map<String, Map<String, ElementInfo>>,
        date: String
    ): SingleTestResults {
        val resultsForDate = allResults[date] as Map<String, Any>
        val testNames = resultsForDate["test_names"] as List<String>
        val elementResults = mutableListOf<ElementResult>()
        for (name in testNames) {
            for (element in allTests[name]?.keys!!) {
                elementResults.add(
                    ElementResult.fromElementInfo(
                        allTests[name]?.get(element)!!,
                        resultsForDate[element] as Number
                    )
                )
            }
        }
        val name = resultsForDate["name"] as String
        return SingleTestResults(name, date, elementResults)
    }

    override suspend fun getAllSingleTestResults(pesel: String): Resource<Map<String, SingleTestResults>> {
        return try {
            val allTestsMap = mapifyTestList(getAllTests().data!!)
            val usersResults = firestore
                .collection("results")
                .document(pesel)
                .get()
                .await()
                .data as Map<String, Any>

            val allSingleTestResults = mutableMapOf<String, SingleTestResults>()
            usersResults.forEach { (date, values) ->
                allSingleTestResults[date] = parseSingleTestResultsFromAllResults(
                    usersResults,
                    allTestsMap,
                    date
                )
            }
            Resource.Success(allSingleTestResults)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override fun addRegistrationUserInfo(
        name: String,
        surname: String,
        pesel: String,
        email: String,
    ): Flow<Resource<String>> {
        val userInfo = hashMapOf(
            "name" to name,
            "surname" to surname,
            "pesel" to pesel,
            "email" to email,
        )
        val assistantResults = hashMapOf<String, Any>(
            pesel to listOf<String>()
        )

        return flow {
            emit(Resource.Loading())
            try {
                firestore.collection("users").document(email).set(userInfo).await()
                firestore.collection("results").document(pesel).set(hashMapOf<String, Any>())
                    .await()
                firestore.collection("assistant-entered-results").document("lab@ilab.com")
                    .update(assistantResults).await()
                println("HUH???")
                emit(Resource.Success("Adding user info was successful!"))
            } catch (e: Exception) {
                println("HUH???")
                println(e)
                emit(Resource.Error(e.toString()))
            }
        }
    }

    override suspend fun getAllTests(): Resource<List<DbTestInfo>> {
        val result = firestore.collection("tests").get()
        val tests: List<DbTestInfo> = result.await().map { res ->
            val name = res.getString("name")
            val elements: Map<String, Map<String, Any>> =
                res.get("elements") as Map<String, Map<String, Any>>

            val elementInfos: HashMap<String, ElementInfo> = HashMap()
            for ((elementName, elementDetails) in elements) {
                val rangeStart = elementDetails["range_start"];
                val rangeEnd = elementDetails["range_end"];
                val unit: String = elementDetails["unit"] as String;
                elementInfos[elementName] = ElementInfo(
                    elementName,
                    rangeStart as Number,
                    rangeEnd as Number,
                    unit,
                )
            }

            println(elementInfos)
            DbTestInfo(name!!, elementInfos)
        }

        return Resource.Success(tests)
    }

    override suspend fun getNewBundles(): Resource<List<DbBundleInfo>> {
        val result = firestore.collection("new-new-bundles").get()
        val bundles: List<DbBundleInfo> = result.await().map { res ->
            val name = res.getString("name")
            val imageName = res.getString("image_name")
            var tests: List<DbTestInfo> = mutableListOf()

            val dbTests: List<DocumentReference> = res.get("tests") as List<DocumentReference>
            for (test in dbTests) {
                val dbTest = test.get().await()
                val name = dbTest.getString("name")
                val elements: Map<String, Map<String, Any>> =
                    dbTest.get("elements") as Map<String, Map<String, Any>>

                val elementInfos: HashMap<String, ElementInfo> = HashMap()
                for ((elementName, elementDetails) in elements) {
                    val rangeStart = elementDetails["range_start"];
                    val rangeEnd = elementDetails["range_end"];
                    val unit: String = elementDetails["unit"] as String;
                    elementInfos[elementName] = ElementInfo(
                        elementName,
                        rangeStart as Number,
                        rangeEnd as Number,
                        unit,
                    )
                }

                tests = tests.plus(DbTestInfo(name!!, elementInfos))
            }

            DbBundleInfo(name!!, imageName!!, tests)
        }
        println(bundles)
        return Resource.Success(bundles)
    }

    override suspend fun getBundles(): Resource<List<BundleInfo>> {
        return try {
            val result = firestore.collection("new-new-bundles").get()
            val bundlesInfo: List<BundleInfo> = result.await().map { res ->
                val name = res.getString("name")!!
                val imageName = res.getString("image_name")!!

                val dbTests = res.get("tests") as List<DocumentReference>
                val testsInfo: List<TestInfo> = dbTests.map { dbTest ->
                    TestInfo(dbTest.id.replace("\"", ""), name, imageName)
                }

                BundleInfo(
                    name,
                    imageName,
                    testsInfo,
                )
            }
            println(bundlesInfo)
            Resource.Success(bundlesInfo)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
}
