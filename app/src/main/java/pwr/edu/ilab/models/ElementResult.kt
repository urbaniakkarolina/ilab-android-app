package pwr.edu.ilab.models


data class ElementResult(
    val name: String,
    val rangeStart: Number,
    val rangeEnd: Number,
    val unit: String,
    val result: Number,
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name, unit, "$name$unit"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }

    companion object {
        fun fromElementInfo(elementInfo: ElementInfo, result: Number): ElementResult {
            return ElementResult(
                elementInfo.name,
                elementInfo.rangeStart,
                elementInfo.rangeEnd,
                elementInfo.unit,
                result
            )
        }
    }
}
