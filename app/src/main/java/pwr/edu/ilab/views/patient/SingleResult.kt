package pwr.edu.ilab.views.patient

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.R
import pwr.edu.ilab.generatePDF
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.ui.theme.MyDarkBlue
import pwr.edu.ilab.ui.theme.MyRose
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.patient.SingleResultViewModel
import pwr.edu.ilab.views.components.LoadingSpinner
import pwr.edu.ilab.views.components.ScrollableSingleTestResultsList
import pwr.edu.ilab.views.components.ScrollableTestResultsList
import java.io.File

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SingleResult(
    date: String?,
    padding: PaddingValues,
    viewModel: SingleResultViewModel = hiltViewModel()
) {
    val testResultsState = viewModel.singleTestResults.collectAsState()
    val allSingleTestResults = viewModel.allTestResults.collectAsState()
    val context = LocalContext.current

    fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("no activity")
    }

    val activity = context.findActivity()
    fun getDirectory(): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir
    }

    BoxWithConstraints(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {

        when (testResultsState.value) {
            is Resource.Loading -> LoadingSpinner()
            is Resource.Error -> Text(text = "Nie mogliśmy załadować wyników tych badań :c")
            is Resource.Success -> {

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val name = testResultsState.value.data!!.name
                        Text(
                            text = if (name.length < 19) name else name.substring(0, 18) + "..",
                            color = MyDarkBlue,
                            fontSize = 25.sp,
                            fontFamily = GreatSailor,
                            modifier = Modifier.padding(30.dp, 30.dp, 30.dp, 0.dp)
                        )

                        Image(
                            painter = painterResource(id = R.drawable.blood),
                            contentDescription = "Blood drops",
                            modifier = Modifier
                                .fillMaxWidth(0.2f)
                                .padding(0.dp, 25.dp, 0.dp, 0.dp),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$date",
                            color = MyDarkBlue,
                            fontSize = 22.sp,
                            fontFamily = GreatSailor,
                            modifier = Modifier.padding(30.dp, 10.dp, 30.dp, 0.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MyRose),
                            onClick = {
                                generatePDF(
                                    context,
                                    getDirectory(),
                                    AppCompatResources.getDrawable(context, R.drawable.ilabs)!!,
                                    "wyniki-$date",
                                    testResultsState.value.data!!
                                )
                            }) {
                            Text(text = "Eksport do PDF", fontFamily = GreatSailor)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(30.dp, 60.dp, 30.dp, 0.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    ScrollableSingleTestResultsList(
                        singleTestResults = testResultsState.value.data!!,
                        allSingleTestResults = allSingleTestResults.value.data!!,
                        context = context,
                        modifier = Modifier.padding(top = 45.dp)
                    )
                }
                requestForegroundPermission(context)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getTestResults(date!!)
    }

}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

private fun foregroundPermissionApproved(context: Context): Boolean {
    val writePermissionFlag =
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    val readPermissionFlag =
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        )

    return writePermissionFlag && readPermissionFlag
}

private fun requestForegroundPermission(context: Context) {
    val provideRationale = foregroundPermissionApproved(context = context)
    if (provideRationale) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }
}
