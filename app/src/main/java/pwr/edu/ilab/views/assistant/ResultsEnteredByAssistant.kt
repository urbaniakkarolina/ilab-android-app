package pwr.edu.ilab.views.assistant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.assistant.ResultsEnteredByAssistantViewModel
import pwr.edu.ilab.views.components.LoadingSpinner
import pwr.edu.ilab.views.components.ScrollableResultsEnteredByAssistant

@Composable
fun ResultsEnteredByAssistant(padding: PaddingValues, viewModel: ResultsEnteredByAssistantViewModel = hiltViewModel()) {
    val enteredResultsState = viewModel.enteredResults.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(padding).fillMaxSize()) {
        when (enteredResultsState.value) {
            is Resource.Loading -> LoadingSpinner()
            is Resource.Error -> Text(text = "Nie moglismy znalezc zadnych wprowadzoncyh przez Ciebie wynikow")
            is Resource.Success -> {
                if (!enteredResultsState.value.data!!.isEmpty()) {
                    val removeResult: (String, String) -> Unit = { pesel: String, date: String ->
                        viewModel.removeSelectedResult(pesel, date)
                    }

                    ScrollableResultsEnteredByAssistant(
                        enteredResults = enteredResultsState.value.data!!,
                        removeResult = removeResult,
                        context = context
                    )
                } else {
                    Text(text = "Nie znaleziono żadnych wyników wprowadzonych przez Ciebie")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getEnteredResults()
    }
}
