package pwr.edu.ilab.data

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.facebook.AccessToken
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import pwr.edu.ilab.R
import pwr.edu.ilab.utils.Resource
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    private var oneTapClient: SignInClient? = null

    private fun buildSignInRequest(context: Context): BeginSignInRequest {
        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false).setServerClientId(
                    context.getString(
                        R.string.web_client_id
                    )
                ).build()
        ).setAutoSelectEnabled(true).build()
    }

    override fun loginUserWithGoogleIntent(client: SignInClient, intent: Intent): Flow<Resource<AuthResult>> {
        if (oneTapClient == null) {
            oneTapClient = client
        }
        return flow {
            emit(Resource.Loading())
            val credential = client.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = firebaseAuth.signInWithCredential(googleCredentials).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun loginUserWithFacebook(token: AccessToken): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val credential = FacebookAuthProvider.getCredential(token.token)
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun logoutUser() {
        oneTapClient?.signOut()
        firebaseAuth.signOut()
    }

    override fun currentUser(): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            val email = firebaseAuth.currentUser?.email ?: ""
            emit(Resource.Success(email))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun resetPassword(email: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success("Sent the mail successfully!"))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}
