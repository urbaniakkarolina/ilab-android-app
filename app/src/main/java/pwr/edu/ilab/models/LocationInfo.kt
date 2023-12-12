package pwr.edu.ilab.models

import com.google.maps.android.compose.MarkerState

data class LocationInfo(
    val state: MarkerState,
    val title: String,
    val description: String
)