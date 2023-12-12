package pwr.edu.ilab.views.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.models.TestInfo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScrollableList(values: List<TestInfo>, imgToId: Map<String, Int>, context: Context) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
        itemsIndexed(values) { index, testInfo ->
            Card(
                onClick = {
                    Toast.makeText(
                        context, "${testInfo.name} selected..", Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .height(45.dp)
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth(),

                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (testInfo.imageName.isNotEmpty()) {
                        Image(
                            painter = painterResource(imgToId[testInfo.imageName]!!),
                            contentDescription = testInfo.name,
                        )
                    }
                    Text(
                        text = testInfo.name,
                        modifier = Modifier
                            .padding(4.dp),
                        color = Color.Black, textAlign = TextAlign.Center
                    )
                }
            }
            Divider(modifier = Modifier.padding(horizontal = 6.dp))
        }
    }
}
