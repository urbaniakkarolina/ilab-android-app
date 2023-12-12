package pwr.edu.ilab.models

import java.text.SimpleDateFormat
import java.util.*

data class ResultsEnteredByAssistant(
    val pesel: String,
    var resultDates: List<String>
) {
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun sort() {
        val datesMap = resultDates.map { date ->
            formatter.parse(date) to date
        }
        val dates = datesMap.toMap().toSortedMap()
        resultDates = dates.values.toList().reversed()
    }
}