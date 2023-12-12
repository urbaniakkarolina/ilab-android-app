package pwr.edu.ilab.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pwr.edu.ilab.viewmodels.NavigationGraphViewModel
import pwr.edu.ilab.views.*
import pwr.edu.ilab.views.assistant.ResultsEnteredByAssistant
import pwr.edu.ilab.views.patient.NavigationDrawer as PatientNavigationDrawer
import pwr.edu.ilab.views.assistant.NavigationDrawer as AssistantNavigationDrawer
import pwr.edu.ilab.views.assistant.ResultsForm
import pwr.edu.ilab.views.Menu
import pwr.edu.ilab.views.patient.Results
import pwr.edu.ilab.views.patient.SingleResult

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    navigationGraphViewModel: NavigationGraphViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = "initial") {
        val navigateToLoginRegister: () -> Unit = { navController.navigate("login_register") }
        val goBackToPrevView: () -> Unit = { navController.popBackStack() }
        val navigateToAssistantForm: () -> Unit = {
            navController.navigate("results_form") {
                popUpTo(0)
            }
        }
        val navigateToAssistantsEnteredResults: () -> Unit =
            { navController.navigate("results_entered_by_assistant") }
        val navigateToMenuAssistant: () -> Unit = { navController.navigate("menu_assistant") }
        val navigateToLocationsAssistant: () -> Unit =
            { navController.navigate("locations_assistant") }
        val navigateToMenu: () -> Unit = { navController.navigate("menu") }
        val navigateToLocations: () -> Unit = { navController.navigate("locations") }
        val navigateToResults: () -> Unit = {
            navController.navigate("results") {
                popUpTo(0)
            }
        }
        val navigateToSingleResult: (date: String) -> Unit =
            { date: String ->
                navController.navigate("single_result/$date")
            }
        val logoutAndGoToInitial: () -> Unit = {
            navigationGraphViewModel.logout()
            navController.navigate("initial") {
                popUpTo(0)
            }
        }

        composable(route = "initial") {
            Initial(navigateToLoginRegister)
        }
        composable(route = "login_register") {
            LoginRegister(navigateToResults, navigateToAssistantForm)
        }
        composable(route = "menu") {
            PatientNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToResults,
                navigateToMenu,
                navigateToLocations,
                logoutAndGoToInitial
            ) { padding ->
                Menu(padding)
            }
        }
        composable(route = "menu_assistant") {
            AssistantNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToAssistantForm,
                navigateToAssistantsEnteredResults,
                navigateToMenuAssistant,
                navigateToLocationsAssistant,
                logoutAndGoToInitial
            ) { padding ->
                Menu(padding)
            }
        }
        composable(route = "results_form") {
            AssistantNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToAssistantForm,
                navigateToAssistantsEnteredResults,
                navigateToMenuAssistant,
                navigateToLocationsAssistant,
                logoutAndGoToInitial
            ) { padding ->
                ResultsForm(padding)
            }
        }
        composable(route = "results_entered_by_assistant") {
            AssistantNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToResultsForm = navigateToAssistantForm,
                navigateToResultsIEntered = navigateToAssistantsEnteredResults,
                navigateToMenu = navigateToMenuAssistant,
                navigateToLocations = navigateToLocationsAssistant,
                logoutAndGoToInitial = logoutAndGoToInitial
            ) { padding ->
                ResultsEnteredByAssistant(padding)
            }
        }
        composable(route = "results") {
            PatientNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToResults,
                navigateToMenu,
                navigateToLocations,
                logoutAndGoToInitial
            ) { padding ->
                Results(navigateToSingleResult, padding)
            }
        }
        composable(route = "locations") {
            PatientNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = false,
                navigateToResults = navigateToResults,
                navigateToMenu = navigateToMenu,
                navigateToLocations = navigateToLocations,
                logoutAndGoToInitial = logoutAndGoToInitial
            ) { padding ->
                OurLocations(padding)
            }
        }
        composable(route = "locations_assistant") {
            AssistantNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = false,
                navigateToResultsForm = navigateToAssistantForm,
                navigateToResultsIEntered = navigateToAssistantsEnteredResults,
                navigateToMenu = navigateToMenuAssistant,
                navigateToLocations = navigateToLocationsAssistant,
                logoutAndGoToInitial = logoutAndGoToInitial
            ) { padding ->
                OurLocations(padding)

            }
        }
        composable(
            route = "single_result/{date}", arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                })
        ) { entry ->
            PatientNavigationDrawer(
                modifier = Modifier,
                topBarActive = false,
                bottomBarActive = true,
                sideBarActive = true,
                navigateToResults = navigateToResults,
                navigateToMenu = navigateToMenu,
                navigateToLocations = navigateToLocations,
                logoutAndGoToInitial = logoutAndGoToInitial
            ) { padding ->
                SingleResult(date = entry.arguments?.getString("date"), padding)
            }
        }
    }
}
