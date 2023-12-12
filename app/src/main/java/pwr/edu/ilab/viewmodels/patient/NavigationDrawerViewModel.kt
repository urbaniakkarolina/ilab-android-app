package pwr.edu.ilab.viewmodels.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationDrawerViewModel @Inject constructor() : ViewModel() {
    fun onItemClicked(
        itemId: String,
        navigateToResults: () -> Unit,
        navigateToMenu: () -> Unit,
        navigateToLocations: () -> Unit,
        logoutAndGoToInitial: () -> Unit
    ) = viewModelScope.launch {
        when (itemId) {
            "wyniki" -> navigateToResults()
            "oferta" -> navigateToMenu()
            "lokalizacje" -> navigateToLocations()
            "wylogowanie" -> logoutAndGoToInitial()
        }
    }
}