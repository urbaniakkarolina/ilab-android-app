package pwr.edu.ilab.views.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.models.ResultsEnteredByAssistant
import pwr.edu.ilab.ui.theme.DateColor
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.ui.theme.TurtleColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AssistantEnteredResultCard(
    result: ResultsEnteredByAssistant,
    removeResult: (String, String) -> Unit,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                ambientColor = LoginCardBackground,
                elevation = 6.dp,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = result.pesel, color = TurtleColor)
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                contentDescription = "Open/close indicator"
            )
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .background(Color.White)
            ) {
                result.resultDates.forEachIndexed { index, date ->
                    Card(
                        modifier = Modifier
                            .height(80.dp)
                            .padding(horizontal = 6.dp)
                            .fillMaxWidth()
                            .border(BorderStroke(0.dp, Color.White)),

                        elevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row() {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "person account"
                                    )
                                    Text(
                                        text = date,
                                        modifier = Modifier
                                            .padding(start = 16.dp),
                                        color = Color.Black, textAlign = TextAlign.Left
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Usun wyniki ${result.pesel}, $date",
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(
                                            bounded = false,
                                            radius = 25.dp
                                        ),
                                        onClick = { removeResult(result.pesel, date) }
                                    )
                                )
                            }

                        }
                    }
                    if (index != result.resultDates.lastIndex) {
                        Divider(modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp))
                    }
                }
            }
        }

    }


}
