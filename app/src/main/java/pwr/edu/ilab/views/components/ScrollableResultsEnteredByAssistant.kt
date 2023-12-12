package pwr.edu.ilab.views.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.models.ResultsEnteredByAssistant
import pwr.edu.ilab.models.SingleTestResults
import pwr.edu.ilab.ui.theme.DateColor
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.views.assistant.ResultsEnteredByAssistant

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollableResultsEnteredByAssistant(
    enteredResults: List<ResultsEnteredByAssistant>,
    removeResult: (String, String) -> Unit,
    context: Context
) {
    val enteredResults = enteredResults.map {result ->
        result.sort()
        result
    }
    LazyColumn(
        modifier = Modifier
            .padding(top = 20.dp)
            .background(Color.White)
    ) {
        items(enteredResults) { result ->
            AssistantEnteredResultCard(result, removeResult, context)
        }
    }
}