package pwr.edu.ilab.utils

data class State(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = "",
    val isStaff: Boolean? = false,
)