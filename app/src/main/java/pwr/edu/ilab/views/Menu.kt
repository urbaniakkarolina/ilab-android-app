package pwr.edu.ilab.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import pwr.edu.ilab.R
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.MenuViewModel
import pwr.edu.ilab.views.components.HorizontalCards
import pwr.edu.ilab.views.components.LoadingSpinner
import pwr.edu.ilab.views.components.ScrollableList

@Composable
fun Menu(padding: PaddingValues, menuViewModel: MenuViewModel = hiltViewModel()) {
    val cardsInfo by menuViewModel.cardInfoState.collectAsState()
    val testsInfo by menuViewModel.testsInfoState.collectAsState()
    val searchText by menuViewModel.searchTextState.collectAsState()
    val isSearching by menuViewModel.isSearchingState.collectAsState()
    val context = LocalContext.current

    val imgToId: Map<String, Int> = mapOf(
        "pakiet_dla_dzieci" to R.drawable.pakiet_dla_dzieci,
        "pakiet_fit_and_active" to R.drawable.pakiet_fit_and_active,
        "pakiet_zdrowa_mama" to R.drawable.pakiet_zdrowa_mama,
        "pakiet_hormonalny" to R.drawable.pakiet_hormonalny,
        "pakiet_sercowy" to R.drawable.pakiet_sercowy,
        "pakiet_odpornosciowy" to R.drawable.pakiet_odpornosciowy,
    )

    Column(modifier = Modifier.padding(padding)) {
        TextField(
            value = searchText,
            onValueChange = menuViewModel::onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") }
        )

        if (isSearching) {
            LoadingSpinner()
        } else {
            when (cardsInfo) {
                is Resource.Success -> HorizontalCards(
                    cardsInfo = cardsInfo.data!!,
                    imgToId = imgToId,
                    context = context
                )
                else -> LoadingSpinner()
            }
            when (testsInfo) {
                is Resource.Success -> ScrollableList(
                    values = menuViewModel.deduplicate(testsInfo.data!!),
                    imgToId = imgToId,
                    context = context
                )
                else -> LoadingSpinner()
            }
        }
    }
}