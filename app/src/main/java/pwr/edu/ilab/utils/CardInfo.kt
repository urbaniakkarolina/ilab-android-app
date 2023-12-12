package pwr.edu.ilab.utils

import pwr.edu.ilab.models.Searchable

data class CardInfo(
    val title: String = "",
    val imageName: String = "",
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            title
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
