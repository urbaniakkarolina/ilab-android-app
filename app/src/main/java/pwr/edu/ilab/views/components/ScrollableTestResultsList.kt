package pwr.edu.ilab.views.components

import android.content.Context
import android.graphics.drawable.Icon
import android.view.RoundedCorner
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.models.TestInfo
import pwr.edu.ilab.models.TestResultsInfo
import pwr.edu.ilab.ui.theme.BorderColor
import pwr.edu.ilab.ui.theme.DateColor
import pwr.edu.ilab.ui.theme.LoginCardBackground

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollableTestResultsList(
    navigateToSingleResult: (String) -> Unit,
    values: List<TestResultsInfo>,
    context: Context
) {
    val values = values.sortedDescending()

    LazyColumn(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 0.dp)
    ) {

        itemsIndexed(values) { idx, testResultsInfo ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(
                        start = 3.dp,
                        end = 3.dp,
                        top = 3.dp,
                        bottom = if (idx == values.lastIndex) 28.dp else 3.dp
                    )
                    .requiredHeight(80.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clickable {
                        navigateToSingleResult(testResultsInfo.date)
                    },
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "person account"
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = testResultsInfo.name,
                                modifier = Modifier
                                    .padding(start = 16.dp),
                                color = Color.Black, textAlign = TextAlign.Left
                            )
                            Text(
                                text = testResultsInfo.date,
                                modifier = Modifier
                                    .padding(start = 16.dp),
                                color = DateColor, textAlign = TextAlign.Left
                            )
                        }
                    }

                }
            }
        }
    }
}
