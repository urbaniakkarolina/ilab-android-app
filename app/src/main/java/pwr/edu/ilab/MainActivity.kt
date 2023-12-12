package pwr.edu.ilab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import pwr.edu.ilab.ui.theme.ILabTheme
import pwr.edu.ilab.views.NavigationGraph
import java.io.File


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ILabTheme {
                NavigationGraph()
            }
        }
    }
}
