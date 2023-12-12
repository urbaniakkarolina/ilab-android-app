package pwr.edu.ilab.views.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.models.SingleTestResults
import pwr.edu.ilab.ui.theme.DateColor
import pwr.edu.ilab.ui.theme.LoginCardBackground

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollableSingleTestResultsList(
    singleTestResults: SingleTestResults,
    allSingleTestResults: Map<String, SingleTestResults>,
    context: Context,
    modifier: Modifier = Modifier
) {
    println(singleTestResults)

    LazyColumn(
        modifier = modifier
            .padding(top = 70.dp)
    ) {
        itemsIndexed(singleTestResults.results) { index, result ->
            val historicalResults = parseHistoricalResultsForElement(allSingleTestResults, result.name)
            println(historicalResults)
            SingleTestResultCard(result = result, historicalResults = historicalResults, context = context)
        }
    }
}

private fun parseHistoricalResultsForElement(allResults: Map<String, SingleTestResults>, elementName: String) : Map<String, Number> {
    val historicalResults = mutableMapOf<String, Number>()
    allResults.forEach{(date, singleTestResults) ->
        singleTestResults.results.forEach { elementResult ->
            if (elementResult.name == elementName) {
                historicalResults[date] = elementResult.result
            }
        }
    }
    return historicalResults.toSortedMap()
}