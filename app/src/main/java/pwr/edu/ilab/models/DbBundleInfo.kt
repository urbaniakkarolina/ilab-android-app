package pwr.edu.ilab.models


data class DbBundleInfo(
    val name: String,
    val imageName: String,
    val tests: List<DbTestInfo>,
) : Searchable {
    override fun doesMatchSearchQuery(query: String): Boolean {
        val testNames = tests.map { test -> test.name }
        val matchingCombinations = arrayListOf(name)
        matchingCombinations.addAll(testNames)

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
