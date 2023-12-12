package pwr.edu.ilab.views.patient

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.utils.MenuItem
import pwr.edu.ilab.viewmodels.patient.NavigationDrawerViewModel
import pwr.edu.ilab.views.components.AppBar
import pwr.edu.ilab.views.components.DrawerBody
import pwr.edu.ilab.views.components.DrawerHeader

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NavigationDrawer(
    modifier: Modifier,
    topBarActive: Boolean,
    bottomBarActive: Boolean,
    sideBarActive: Boolean,
    navigateToResults: () -> Unit,
    navigateToMenu: () -> Unit,
    navigateToLocations: () -> Unit,
    logoutAndGoToInitial: () -> Unit,
    navigationDrawerViewModel: NavigationDrawerViewModel = hiltViewModel(),
    content: @Composable() (padding: PaddingValues) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    if (sideBarActive) {
        Scaffold(
            modifier,
            scaffoldState = scaffoldState,
            topBar = {
                if (topBarActive) {
                    AppBar(
                        onNavigationIconClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (bottomBarActive) {
                    BottomBar(navigateToMenu, navigateToResults, navigateToLocations)
                }
            },
            drawerContent = {
                DrawerHeader()
                DrawerBody(
                    items = listOf(
                        MenuItem(
                            id = "wyniki",
                            title = "Moje wyniki",
                            contentDescription = "Przejdź do moich wyników",
                            icon = Icons.Outlined.Favorite
                        ),
                        MenuItem(
                            id = "oferta",
                            title = "Oferta",
                            contentDescription = "Przejdź do oferty",
                            icon = Icons.Outlined.ShoppingCart
                        ),
                        MenuItem(
                            id = "lokalizacje",
                            title = "Nasze lokalizacje",
                            contentDescription = "Przejdź do mapy z naszymi lokalizacjami",
                            icon = Icons.Outlined.LocationOn
                        ),
                        MenuItem(
                            id = "wylogowanie",
                            title = "Wyloguj",
                            contentDescription = "Wyloguj z konta",
                            icon = Icons.Outlined.Close
                        ),
                    ),
                    onItemClick = { item ->
                        navigationDrawerViewModel.onItemClicked(
                            item.id,
                            navigateToResults,
                            navigateToMenu,
                            navigateToLocations,
                            logoutAndGoToInitial
                        )
                    }
                )
            }
        ) { padding ->
            content(padding)
        }

    } else {
        Scaffold(
            modifier,
            scaffoldState = scaffoldState,
            topBar = {
                if (topBarActive) {
                    AppBar(
                        onNavigationIconClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (bottomBarActive) {
                    BottomBar(navigateToMenu, navigateToResults, navigateToLocations)
                }
            },
        ) { padding ->
            content(padding)
        }

    }
}

@Composable
fun BottomBar(
    navigateToMenu: () -> Unit,
    navigateToResults: () -> Unit,
    navigateToLocations: () -> Unit
) {
    BottomAppBar(modifier = Modifier.background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Oferta",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 25.dp),
                    onClick = navigateToMenu
                )
            )
            Icon(
                imageVector = Icons.Outlined.Favorite,
                contentDescription = "Moje wyniki",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 25.dp),
                    onClick = navigateToResults
                )
            )
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Lokalizacje",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 25.dp),
                    onClick = navigateToLocations
                )
            )
        }

    }
}