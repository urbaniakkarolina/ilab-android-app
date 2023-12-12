package pwr.edu.ilab.models

data class SingleTestResults(
    val name: String,
    val date: String,
    val results: List<ElementResult>,
)
