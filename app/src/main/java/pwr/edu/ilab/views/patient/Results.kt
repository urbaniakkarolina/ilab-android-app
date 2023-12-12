package pwr.edu.ilab.views.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pwr.edu.ilab.R
import pwr.edu.ilab.models.TestResultsInfo
import pwr.edu.ilab.ui.theme.CardTitle
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.ui.theme.MyDarkBlue
import pwr.edu.ilab.utils.Resource
import pwr.edu.ilab.viewmodels.patient.ResultsViewModel
import pwr.edu.ilab.views.components.LoadingSpinner
import pwr.edu.ilab.views.components.ScrollableTestResultsList

@Composable
fun Results(
    navigateToSingleResult: (date: String) -> Unit,
    padding: PaddingValues,
    resultsViewModel: ResultsViewModel = hiltViewModel()
) {
    val testResults = resultsViewModel.testResults.collectAsState()
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.plama_dark),
            contentDescription = "Card Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(CardTitle),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(vertical = 15.dp))
            }
        }
        Column(
            modifier = Modifier
                .padding(30.dp, 40.dp, 30.dp, 0.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.micro),
                    contentDescription = "Card Background",
                    modifier = Modifier
                        .fillMaxWidth(0.15f)
                        .padding(bottom = 2.dp),
                    contentScale = ContentScale.FillWidth
                )

                Text(
                    text = "Moje wyniki",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontFamily = GreatSailor,
                    modifier = Modifier.padding(30.dp, 10.dp, 30.dp, 0.dp)
                )
            }

            Spacer(modifier = Modifier.padding(62.dp))

            when (testResults.value) {
                is Resource.Loading -> LoadingSpinner()
                is Resource.Error -> Column(
                    modifier = Modifier.fillMaxHeight(1f),
                    verticalArrangement = Arrangement.Center
                ) { Text(text = "Oops.. nie ma jeszcze żadnych wyników") }

                is Resource.Success -> {
                    for ((testName, results) in testResults.value.data!!) {

                    }
                    var elementsOverTime: Map<String, Array<Double>> = mapOf(

                    )

                    if (testResults.value.data!!.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxHeight(1f),
                            verticalArrangement = Arrangement.Center
                        ) { Text(text = "Oops.. nie ma jeszcze żadnych wyników") }
                    } else {

                        Column(
                            modifier = Modifier
                                .fillMaxHeight(1.0f)
                                .padding(start = 15.dp, end = 15.dp, bottom = 0.dp)
                                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                                .background(CardTitle)
                                .padding(start = 0.dp, end = 0.dp, bottom = 0.dp)
                        ) {
                            ScrollableTestResultsList(
                                navigateToSingleResult,
                                values = testResults.value.data!!,
                                context = context,
                            )
                        }
                    }
                }
            }

        }
    }
}
