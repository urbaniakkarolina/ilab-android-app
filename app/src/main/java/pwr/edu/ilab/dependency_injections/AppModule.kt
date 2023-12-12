package pwr.edu.ilab.dependency_injections

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.signin.internal.SignInClientImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.AuthRepositoryImpl
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.data.DbRepositoryImpl
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesFirestoreRepositoryImpl(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): DbRepository {
        return DbRepositoryImpl(firebaseFirestore, firebaseAuth)
    }
}