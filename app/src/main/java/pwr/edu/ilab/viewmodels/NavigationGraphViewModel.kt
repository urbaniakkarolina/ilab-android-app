package pwr.edu.ilab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.data.AuthRepository
import javax.inject.Inject

@HiltViewModel
class NavigationGraphViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {
    fun logout() = viewModelScope.launch {
        authRepo.logoutUser()
    }
}
