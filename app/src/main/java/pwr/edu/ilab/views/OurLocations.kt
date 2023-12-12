package pwr.edu.ilab.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pwr.edu.ilab.R
import pwr.edu.ilab.models.LocationInfo


@Composable
fun OurLocations(padding: PaddingValues) {
    val places: List<LocationInfo> = listOf(
        LocationInfo(
            MarkerState(position = LatLng(51.11, 17.04)),
            "Laboratorium diagnostyczne iLab",
            "Sienkiewicza 110, 50-361 Wrocław"

        ),
        LocationInfo(
            MarkerState(position = LatLng(51.10, 17.04)),
            "Laboratorium diagnostyczne iLab",
            "plac Świętego Macieja 8, 53-110 Wrocław"
        ),
        LocationInfo(
            MarkerState(position = LatLng(51.11, 17.05)),
            "Laboratorium diagnostyczne iLab",
            "plac Grunwaldzki 18-20, 50-384 Wrocław"
        ),
        LocationInfo(
            MarkerState(position = LatLng(51.10, 17.02)),
            "Laboratorium diagnostyczne iLab",
            "Zakładowa 11h, 50-231 Wrocław"
        ),
        LocationInfo(
            MarkerState(position = LatLng(51.12, 17.03)),
            "Laboratorium diagnostyczne iLab",
            "Generała Romualda Traugutta 142, 50-420 Wrocław"
        ),
    )

    val wroclaw = LatLng(51.1, 17.03)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(wroclaw, 13f)
    }
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        GoogleMap(cameraPositionState = cameraPositionState) {
            places.forEach { place ->
                Marker(state = place.state, title = place.title, snippet = place.description)
            }
        }
    }
}