package pwr.edu.ilab.models

data class TestInfo(
    val name: String,
    val bundle: String,
    val imageName: String,
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name, bundle, "$name $bundle", "$name$bundle"
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
