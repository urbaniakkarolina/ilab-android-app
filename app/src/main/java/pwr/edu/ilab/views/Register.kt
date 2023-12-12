package pwr.edu.ilab.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.MyDarkBlue
import pwr.edu.ilab.ui.theme.MyRose
import pwr.edu.ilab.viewmodels.RegisterViewModel

@Composable
fun Register(navigateHome: () -> Unit, viewModel: RegisterViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var pesel by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVerification by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVerificationVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val signInState = viewModel.signInState.collectAsState(initial = null)
    val registerInfoState = viewModel.registerInfoState.collectAsState(initial = null)

    Column(
        modifier = Modifier.padding(30.dp, 0.dp, 30.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp)
        Spacer(modifier = Modifier.padding(16.dp))

        val namePlaceholderLabel = "Imię"
        OutlinedTextField(modifier = modifier,
            value = name,
            placeholder = { Text(text = namePlaceholderLabel) },
            label = { Text(text = namePlaceholderLabel) },
            onValueChange = { newName: String ->
                name = newName
            })

        val surnamePlaceholderLabel = "Nazwisko"
        OutlinedTextField(modifier = modifier,
            value = surname,
            placeholder = { Text(text = surnamePlaceholderLabel) },
            label = { Text(text = surnamePlaceholderLabel) },
            onValueChange = { newSurname: String ->
                surname = newSurname
            })

        val peselPlaceholderLabel = "PESEL"
        OutlinedTextField(modifier = modifier,
            value = pesel,
            placeholder = { Text(text = peselPlaceholderLabel) },
            label = { Text(text = peselPlaceholderLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { newPesel: String ->
                if (newPesel.length < 12 && newPesel.last().isDigit()) {
                    pesel = newPesel
                }
            })

        val emailPlaceholderLabel = "Adres email"
        OutlinedTextField(modifier = modifier,
            value = email,
            placeholder = { Text(text = emailPlaceholderLabel) },
            label = { Text(text = emailPlaceholderLabel) },
            onValueChange = { newEmail: String ->
                email = newEmail
            })

        val passwordPlaceholderLabel = "Hasło"
        OutlinedTextField(
            modifier = modifier,
            value = password,
            placeholder = { Text(text = passwordPlaceholderLabel) },
            label = { Text(text = passwordPlaceholderLabel) },
            onValueChange = { newPasswd: String ->
                password = newPasswd
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else
                PasswordVisualTransformation()
        )

        val verificationPasswordPlaceholderLabel = "Powtórz hasło"
        OutlinedTextField(
            modifier = modifier,
            value = passwordVerification,
            placeholder = { Text(text = verificationPasswordPlaceholderLabel) },
            label = { Text(text = verificationPasswordPlaceholderLabel) },
            onValueChange = { newPasswd: String ->
                passwordVerification = newPasswd
            },
            visualTransformation = if (passwordVerificationVisible) VisualTransformation.None else
                PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.padding(vertical = 15.dp))

        Button(
            onClick = {
                scope.launch {
                    viewModel.registerUser(
                        name, surname, pesel, email, password, passwordVerification
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
            shape = RoundedCornerShape(percent = 50),
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = modifier,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Zarejestruj się", color = MyDarkBlue, fontSize = 17.sp,
                fontFamily = GreatSailor,
            )

        }
        LaunchedEffect(
            key1 = signInState.value?.isSuccess,
            key2 = registerInfoState.value?.isSuccess
        ) {
            scope.launch {
                if ((signInState.value?.isSuccess?.isNotEmpty() == true) and (registerInfoState.value?.isSuccess?.isNotEmpty() == true)) {
                    val success =
                        signInState.value?.isSuccess + registerInfoState.value?.isSuccess
                    Toast.makeText(context, success, Toast.LENGTH_LONG).show()
                    navigateHome()
                }
            }
        }

        LaunchedEffect(
            key1 = signInState.value?.isError,
            key2 = registerInfoState.value?.isError
        ) {
            scope.launch {
                var error = "";
                error += if (signInState.value?.isError?.isNotEmpty() == true) signInState.value?.isError else ""
                error += if (registerInfoState.value?.isError?.isNotEmpty() == true) registerInfoState.value?.isError else ""
                if (error.isNotEmpty()) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}