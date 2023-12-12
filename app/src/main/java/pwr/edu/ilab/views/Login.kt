package pwr.edu.ilab.views

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.launch
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.ILabTheme
import pwr.edu.ilab.ui.theme.MyDarkBlue
import pwr.edu.ilab.ui.theme.MyRose
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.LoginViewModel

@Composable
fun Login(
    navigateToMenu: () -> Unit,
    navigateToAssistantForm: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val callbackManager = CallbackManager.Factory.create()
    val contextFacebook = LocalContext.current as ActivityResultRegistryOwner
    val state = viewModel.signInState.collectAsState(initial = null)
    val resetState = viewModel.resetState.collectAsState(initial = Resource.Loading())

    val oneTapClient = remember { Identity.getSignInClient(context) }
    viewModel.provideOneTapClientAndContext(oneTapClient, context)

    registerFacebookCallback(callbackManager, viewModel)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            println(result)
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.loginUserWithGoogle(
                    intent = result.data ?: return@rememberLauncherForActivityResult
                )
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                viewModel.loginUserWithGoogle(
                    intent = result.data ?: return@rememberLauncherForActivityResult
                )
            }
        }
    )

    Column(
        modifier = Modifier.padding(30.dp, 0.dp, 30.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
        Spacer(modifier = Modifier.padding(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.replace(" ", "").replace("\t", "").replace("\n", "") },
            label = { Text(text = "Adres email") },
            leadingIcon = { Icon(Icons.Filled.Email, "email address") },
            modifier = modifier
        )

        Spacer(modifier = Modifier.padding(6.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it.replace("\t", "").replace("\n", "")
                if (it.isNotEmpty() && it.last() == '\n') {
                    scope.launch { viewModel.loginUser(email, password) }
                }
            },
            label = { Text(text = "Hasło") },
            leadingIcon = { Icon(Icons.Filled.Lock, "password") },
            modifier = modifier,
            visualTransformation = if (passwordVisible) VisualTransformation.None else
                PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        Text(
            text = "Zapomniałeś hasła?",
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.body2.copy(
                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
            ),
            modifier = modifier.clickable {
                if (resetState.value !is Resource.Success) {
                    viewModel.resetPassword(email)
                } else {
                    Toast.makeText(context, "Already sent an email!", Toast.LENGTH_LONG).show()
                }
            },
        )

        Spacer(modifier = Modifier.padding(vertical = 28.dp))

        Button(
            onClick = {
                scope.launch { viewModel.loginUser(email, password) }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
            shape = RoundedCornerShape(percent = 50),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = modifier,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Zaloguj się",
                color = MyDarkBlue,
                fontSize = 17.sp,
                fontFamily = GreatSailor
            )
        }

        Button(
            onClick = {
                viewModel.createIntent {
                    launcher.launch(
                        IntentSenderRequest.Builder(it.intentSender).build()
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
            shape = RoundedCornerShape(percent = 50),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = modifier.padding(top = 10.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Google",
                color = MyDarkBlue,
                fontSize = 17.sp,
                fontFamily = GreatSailor
            )
        }

        Button(
            onClick = {
                val permissions = listOf("email", "public_profile")
                LoginManager.getInstance()
                    .logIn(contextFacebook, callbackManager, permissions)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
            shape = RoundedCornerShape(percent = 50),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = modifier.padding(top = 10.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Facebook",
                color = MyDarkBlue,
                fontSize = 17.sp,
                fontFamily = GreatSailor
            )
        }
    }


    LaunchedEffect(key1 = resetState.value) {
        scope.launch {
            when (resetState.value) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    val error = resetState.value.message
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }

                is Resource.Success -> {
                    Toast.makeText(context, resetState.value.data, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        scope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, success, Toast.LENGTH_SHORT).show()

                if (state.value?.isStaff == true) {
                    navigateToAssistantForm()
                } else if (state.value?.isStaff == false) {
                    navigateToMenu()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.value?.isError) {
        scope.launch {
            if (state.value?.isError?.isNotEmpty() == true) {
                val error = state.value?.isError
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }
}


fun registerFacebookCallback(callbackManager: CallbackManager, viewModel: LoginViewModel) {
    LoginManager.getInstance().registerCallback(callbackManager, object :
        FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            Log.d(TAG, "facebook:onSuccess:$result")
            viewModel.loginUserWithFacebook(result.accessToken)
        }

        override fun onCancel() {
            Log.d(TAG, "facebook:onCancel")
        }

        override fun onError(error: FacebookException) {
            Log.d(TAG, "facebook:onError", error)
        }
    })
}