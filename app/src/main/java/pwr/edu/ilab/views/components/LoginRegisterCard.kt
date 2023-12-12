package pwr.edu.ilab.views.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.R
import pwr.edu.ilab.views.Login
import pwr.edu.ilab.views.Register

@Composable
fun LoginRegisterCard(navigateToResults: () -> Unit, navigateToAssistantForm: () -> Unit) {
    val options = listOf(
        stringResource(R.string.login_text),
        stringResource(R.string.register_text)
    )
    var selectedOption by remember { mutableStateOf(options.first()) }

    MultiSelector(
        options = options,
        selectedOption = selectedOption,
        onOptionSelect = { option ->
            selectedOption = option
        },
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 25.dp, bottom = 10.dp)
            .fillMaxWidth()
            .height(46.dp)
    )

    when (selectedOption) {
        stringResource(R.string.login_text) -> Login(navigateToResults, navigateToAssistantForm)
        stringResource(R.string.register_text) -> Register(navigateToResults)
    }
}