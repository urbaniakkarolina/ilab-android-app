package pwr.edu.ilab.views.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.utils.CardInfo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HorizontalCards(cardsInfo: List<CardInfo>, imgToId: Map<String, Int>, context: Context) {
    LazyRow(modifier = Modifier.padding(top = 20.dp)) {
        itemsIndexed(cardsInfo) { index, cardInfo ->
            Card(
                onClick = {
                    Toast.makeText(
                        context, cardInfo.title + " selected..", Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(160.dp)
                    .height(260.dp),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = imgToId[cardInfo.imageName]!!),

                        contentDescription = cardInfo.title,

                        modifier = Modifier
                            .fillMaxHeight(0.75f)
                            .fillMaxWidth(0.8f)
                            .padding(5.dp),

                        alignment = Alignment.Center
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = cardInfo.title,
                        modifier = Modifier.padding(4.dp),
                        color = Color.Black, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
