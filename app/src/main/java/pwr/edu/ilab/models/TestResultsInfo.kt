package pwr.edu.ilab.models

import java.text.SimpleDateFormat
import java.util.Locale

data class TestResultsInfo(
    val name: String,
    val date: String,
) : Searchable, Comparable<TestResultsInfo> {
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }

    override fun compareTo(other: TestResultsInfo): Int {

        return formatter.parse(date).compareTo(formatter.parse(other.date))
    }
}
