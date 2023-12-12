package pwr.edu.ilab.models

data class DbTestInfo(
    val name: String,
    val elements: Map<String, ElementInfo>
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }

    fun mapify(): MutableMap<String, String> {
        return elements.map { element ->
            element.key to "0"
        }.toMap().toMutableMap()
    }
}
