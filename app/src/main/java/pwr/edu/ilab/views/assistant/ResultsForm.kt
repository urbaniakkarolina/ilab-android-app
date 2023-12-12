package pwr.edu.ilab.views.assistant

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.MyDarkBlue
import pwr.edu.ilab.ui.theme.MyRose
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.assistant.ResultsFormViewModel
import pwr.edu.ilab.views.components.LoadingSpinner
import pwr.edu.ilab.views.components.ResultCard

@Composable
fun ResultsForm(
    padding: PaddingValues,
    resultsFormViewModel: ResultsFormViewModel = hiltViewModel()
) {
    var pesel by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val tests by resultsFormViewModel.testsInfoState.collectAsState()
    val validation by resultsFormViewModel.validationState.collectAsState(initial = Resource.Loading())
    val addedTests: SnapshotStateList<Int> = remember {
        val initialAddedTests = SnapshotStateList<Int>()
        initialAddedTests.add(0)
        initialAddedTests
    }
    val addedTestsResults: SnapshotStateMap<Int, MutableMap<String, String>> = remember {
        val initialAddedTests = SnapshotStateMap<Int, MutableMap<String, String>>()
        initialAddedTests
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    LaunchedEffect(key1 = addedTests.toList(), key2 = tests.data) {
        if (tests.data == null) {
            return@LaunchedEffect
        }

        val tests = tests.data!!

        for ((key, value) in addedTestsResults) {
            println("BEFORE: $key -> $value")
        }

        for (idx in addedTestsResults.keys) {
            if (idx !in addedTests) {
                addedTestsResults.remove(idx)
            }
        }

        addedTests.forEachIndexed { idx, testIdx ->
            if (testIdx !in addedTestsResults.keys) {
                if (testIdx < tests.size) {
                    val test = tests[testIdx]
                    addedTestsResults[testIdx] = test.mapify()
                } else {
                    val idxsLeft = tests.indices.toSet().subtract(addedTestsResults.keys)
                    if (idxsLeft.size == 0) {
                        Toast.makeText(
                            context,
                            "Nie ma już więcej badań, które można dodać..",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val test = tests[idxsLeft.first()]
                        addedTestsResults[testIdx] = test.mapify()
                        addedTests[idx] = idxsLeft.first()
                    }
                }
            }
        }

        for ((key, value) in addedTestsResults) {
            println("AFTER: $key -> $value")
        }
    }


    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

    LaunchedEffect(key1 = validation) {
        when (validation) {
            is Resource.Error -> Toast.makeText(context, validation.message, Toast.LENGTH_LONG)
                .show()
            is Resource.Success -> {
                Toast.makeText(context, validation.data, Toast.LENGTH_LONG)
                    .show()
                while (addedTests.size > 0) {
                    addedTestsResults.remove(addedTests.removeLast())
                }
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        addedTests.add(0)
                    }, 100
                )
            }
            else -> {}
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(padding)
            .padding(30.dp, 10.dp, 30.dp, 0.dp)
            .fillMaxSize(),
    ) {
        when (tests) {
            is Resource.Success -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wprowadź wyniki",
                        color = MyDarkBlue,
                        fontSize = 24.sp,
                        fontFamily = GreatSailor,
                        modifier = Modifier.padding(30.dp, 8.dp, 30.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    val peselPlaceholderLabel = "PESEL"
                    OutlinedTextField(modifier = modifier,
                        value = pesel,
                        shape = RoundedCornerShape(percent = 50),
                        placeholder = { Text(text = peselPlaceholderLabel) },
                        label = { Text(text = peselPlaceholderLabel) },
                        onValueChange = { newPesel: String ->
                            pesel = newPesel
                        })

                    val datePlaceholderLabel = "Data"
                    OutlinedTextField(modifier = modifier,
                        value = date,
                        shape = RoundedCornerShape(percent = 50),
                        placeholder = { Text(text = datePlaceholderLabel) },
                        label = { Text(text = datePlaceholderLabel) },
                        onValueChange = { newDate: String ->
                            date = newDate
                        })

                    Spacer(modifier = Modifier.padding(vertical = 15.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight(0.68f)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(addedTests) { index, addedTest ->
                            val testValues = addedTestsResults[addedTest]
                            val updateResults: (MutableMap<String, String>) -> Unit =
                                { update ->
                                    println("$addedTest UPDATEEEE")
                                    for ((key, value) in update) {
                                        println("UPDATEEEEE: $key -> $value")
                                    }
                                    println("\nPODSUMOWANIE")
                                    for ((key, value) in addedTestsResults) {
                                        println("PODSUMOWANIE $key -> $value")
                                    }
                                    addedTestsResults.remove(addedTest)
                                    addedTestsResults[addedTest] = update
                                }

                            val setTestChoice: (Int, MutableMap<String, String>) -> Unit =
                                { choice, values ->
                                    addedTests[index] = choice
                                }
                            val removeResultsForPrevIdx: () -> Unit = {
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                ResultCard(
                                    tests.data!!,
                                    tests.data!![addedTest],
                                    index,
                                    testValues,
                                    setTestChoice,
                                    updateResults,
                                    removeResultsForPrevIdx
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 15.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp, 0.dp, 30.dp, 0.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (addedTests.size < tests.data!!.size) {
                                    addedTests.add(addedTests.last() + 1)
                                    for (elem in addedTests) {
                                        println(elem)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
                            shape = RoundedCornerShape(percent = 50),
                            border = BorderStroke(0.dp, Color.Transparent),
                            modifier = Modifier.width(60.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(
                                text = "+", color = MyDarkBlue, fontSize = 26.sp,
                                fontFamily = GreatSailor,
                            )

                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Button(
                            onClick = {
                                val testsAtIndexKeys = tests.data!![addedTests.last()].mapify().keys
                                if (addedTests.size > 1) {
                                    addedTestsResults.remove(addedTests.removeLast())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
                            shape = RoundedCornerShape(percent = 50),
                            border = BorderStroke(0.dp, Color.Transparent),
                            modifier = Modifier.width(60.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(
                                text = "-", color = MyDarkBlue, fontSize = 26.sp,
                                fontFamily = GreatSailor,
                            )

                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .weight(1f, false)
                        .padding(bottom = 15.dp)
                ) {
                    Button(
                        onClick = {
                            var testResultsNames: List<String> =
                                mutableListOf(tests.data!![addedTests[0]].name)
                            var testResultsName: String =
                                tests.data!![addedTests[0]].name.capitalize(
                                    Locale.current
                                )
                            for (testIdx in addedTests.stream().skip(1L)) {
                                testResultsName += ", ${tests.data!![testIdx].name.capitalize(Locale.current)}"
                                testResultsNames = testResultsNames.plus(tests.data!![testIdx].name)
                            }
                            val addedResults: MutableMap<String, String> = mutableMapOf()
                            for (testValues in addedTestsResults.values) {
                                addedResults.putAll(testValues)
                            }
                            for ((key, value) in addedResults) {
                                println("SUBMIT $key -> $value")
                            }
                            scope.launch {
                                resultsFormViewModel.validateAndSubmitResults(
                                    pesel,
                                    date,
                                    testResultsName,
                                    testResultsNames,
                                    addedResults
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
                        shape = RoundedCornerShape(percent = 50),
                        border = BorderStroke(0.dp, Color.Transparent),
                        modifier = modifier,
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Zapisz", color = MyDarkBlue, fontSize = 17.sp,
                            fontFamily = GreatSailor,
                        )
                    }
                }

            }
            is Resource.Loading -> LoadingSpinner()
            is Resource.Error -> {}
        }
    }
}
