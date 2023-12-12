package pwr.edu.ilab.models

data class UserInfo(
    val name: String,
    val surname: String,
    val email: String,
    val pesel: String,
    val userType: String? = null
)
