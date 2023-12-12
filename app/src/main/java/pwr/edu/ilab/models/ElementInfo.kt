package pwr.edu.ilab.models

data class ElementInfo(
    val name: String,
    val rangeStart: Number,
    val rangeEnd: Number,
    val unit: String,
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name, unit, "$name$unit"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
