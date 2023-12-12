package pwr.edu.ilab.viewmodels.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.utils.Resource
import javax.inject.Inject

@HiltViewModel
class NavigationDrawerViewModel @Inject constructor(
    val authRepo: AuthRepository
) : ViewModel() {
    fun onItemClicked(
        itemId: String,
        navigateToResultsForm: () -> Unit,
        navigateToResultsIEntered: () -> Unit,
        navigateToMenu: () -> Unit,
        navigateToLocations: () -> Unit,
        logoutAndGoToInitial: () -> Unit
    ) = viewModelScope.launch {
        when (itemId) {
            "wystaw_wyniki" -> navigateToResultsForm()
            "wystawione_wyniki" -> navigateToResultsIEntered()
            "oferta" -> navigateToMenu()
            "lokalizacje" -> navigateToLocations()
            "wylogowanie" -> logoutAndGoToInitial()
        }
    }
}
