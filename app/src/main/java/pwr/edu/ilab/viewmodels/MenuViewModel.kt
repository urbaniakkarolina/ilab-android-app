package pwr.edu.ilab.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pwr.edu.ilab.data.AuthRepository
import pwr.edu.ilab.data.DbRepository
import pwr.edu.ilab.models.BundleInfo
import pwr.edu.ilab.utils.CardInfo
import pwr.edu.ilab.models.TestInfo
import pwr.edu.ilab.utils.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val dbRepo: DbRepository,
) : ViewModel() {
    private val _searchTextState = MutableStateFlow("")
    val searchTextState = _searchTextState.asStateFlow()

    private val _isSearchingState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isSearchingState = _isSearchingState.asStateFlow()

    private val _cardInfoState: MutableStateFlow<Resource<List<CardInfo>>> = MutableStateFlow(
        Resource.Loading()
    )
    val cardInfoState: MutableStateFlow<Resource<List<CardInfo>>> =
        MutableStateFlow(Resource.Loading())

    private val _testsInfoState: MutableStateFlow<Resource<List<TestInfo>>> = MutableStateFlow(
        Resource.Loading()
    )
    val testsInfoState: MutableStateFlow<Resource<List<TestInfo>>> =
        MutableStateFlow(Resource.Loading())

    private val _updateCardInfo = searchTextState
        .debounce(1000L)
        .combine(_cardInfoState) { text, cardsInfo ->
            if (text.isBlank()) {
                cardInfoState.update { cardsInfo }
            } else if (cardsInfo is Resource.Success) {
                cardInfoState.update {
                    Resource.Success(cardsInfo.data!!.filter {
                        it.doesMatchSearchQuery(text)
                    })
                }
            } else {
                cardInfoState.update { cardsInfo }
            }
        }
        .onEach { _isSearchingState.update { false } }
        .produceIn(viewModelScope)

    private val _updateTestsInfo = searchTextState
        .debounce(1000L)
        .combine(_testsInfoState) { text, testsInfo ->
            if (text.isBlank()) {
                testsInfoState.update { testsInfo }
            } else if (testsInfo is Resource.Success) {
                testsInfoState.update {
                    Resource.Success(testsInfo.data!!.filter {
                        it.doesMatchSearchQuery(text)
                    })
                }
            } else {
                testsInfoState.update { testsInfo }
            }
        }
        .onEach { _isSearchingState.update { false } }
        .produceIn(viewModelScope)

    fun onSearchTextChange(text: String) {
        _isSearchingState.update { true }
        _searchTextState.value = text
    }

    private fun getAllBundlesAndTestsFromDatabase() = viewModelScope.launch {
        val tests = ArrayList<TestInfo>();
        val allCardsInfo = when (val bundles = dbRepo.getBundles()) {
            is Resource.Loading -> {
                _isSearchingState.update { true }
                Resource.Loading()
            }
            is Resource.Success -> {
                _isSearchingState.update { false }
                Resource.Success(bundles.data.orEmpty().map { bundle ->
                    tests.addAll(bundle.tests)
                    convertBundleInfoToCardInfo(bundle)
                })
            }
            is Resource.Error -> {
                _isSearchingState.update { false }
                Resource.Error(bundles.message.orEmpty())
            }
        }
        _cardInfoState.update { allCardsInfo }
        _testsInfoState.update { Resource.Success(tests) }
    }

    private fun convertBundleInfoToCardInfo(bundleInfo: BundleInfo): CardInfo {
        return CardInfo("Pakiet ${bundleInfo.name}", bundleInfo.imageName)
    }

    fun deduplicate(testsInfo: List<TestInfo>): List<TestInfo> {
        var alreadyPresent: List<String> = mutableListOf()
        var returnedTestsInfo: List<TestInfo> = mutableListOf()

        for (testInfo in testsInfo) {
            if (testInfo.name !in alreadyPresent) {
                returnedTestsInfo = returnedTestsInfo.plus(testInfo)
                alreadyPresent = alreadyPresent.plus(testInfo.name)
            }
        }

        return returnedTestsInfo
    }

    init {
        getAllBundlesAndTestsFromDatabase()
    }
}
