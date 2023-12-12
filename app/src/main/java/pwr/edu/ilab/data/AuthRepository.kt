package pwr.edu.ilab.data

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.facebook.AccessToken
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import pwr.edu.ilab.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUserWithGoogleIntent(client: SignInClient, intent: Intent): Flow<Resource<AuthResult>>
    fun loginUserWithFacebook(token: AccessToken): Flow<Resource<AuthResult>>
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun logoutUser()
    fun currentUser(): Flow<Resource<String>>
    fun resetPassword(email: String): Flow<Resource<String>>
}
