package pwr.edu.ilab.views.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import pwr.edu.ilab.models.ElementResult
import pwr.edu.ilab.ui.theme.CardTitle
import pwr.edu.ilab.ui.theme.DateColor
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.ui.theme.OutOfRangeColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SingleTestResultCard(
    result: ElementResult,
    historicalResults: Map<String, Number>,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }
    var values: List<Double> = mutableListOf()
    if (result.rangeStart is Long) {
        values = if (result.rangeEnd is Long) {
            if (result.result is Long) {
                listOf(
                    result.rangeStart.toInt().toDouble(),
                    result.rangeEnd.toInt().toDouble(),
                    result.result.toInt().toDouble()
                )
            } else {
                listOf(
                    result.rangeStart.toInt().toDouble(),
                    result.rangeEnd.toInt().toDouble(),
                    result.result as Double
                )
            }
        } else {
            if (result.result is Long) {
                listOf(
                    result.rangeStart.toInt().toDouble(),
                    result.rangeEnd as Double,
                    result.result.toInt().toDouble()
                )
            } else {
                listOf(
                    result.rangeStart.toInt().toDouble(),
                    result.rangeEnd as Double,
                    result.result as Double
                )
            }
        }
    } else {
        values = if (result.rangeEnd is Long) {
            if (result.result is Long) {
                listOf(
                    result.rangeStart as Double,
                    result.rangeEnd.toInt().toDouble(),
                    result.result.toInt().toDouble()
                )
            } else {
                listOf(
                    result.rangeStart as Double,
                    result.rangeEnd.toInt().toDouble(),
                    result.result as Double
                )
            }
        } else {
            if (result.result is Long) {
                listOf(
                    result.rangeStart as Double,
                    result.rangeEnd as Double,
                    result.result.toInt().toDouble()
                )
            } else {
                listOf(
                    result.rangeStart as Double,
                    result.rangeEnd as Double,
                    result.result as Double
                )
            }
        }
    }
    val isResultInRange = values[2] <= values[1] && values[2] >= values[0]
    val cardTitleColor = if (isResultInRange) CardTitle else OutOfRangeColor

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(
                ambientColor = LoginCardBackground,
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
                    .background(cardTitleColor)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = result.name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp),
                        color = Color.Black, textAlign = TextAlign.Left
                    )
                    Text(
                        text = result.unit,
                        modifier = Modifier
                            .padding(start = 16.dp),
                        color = DateColor, textAlign = TextAlign.Left
                    )

                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!expanded) {
                        Text(text = "${result.result}", modifier = Modifier.padding(end = 5.dp))
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Open/close indicator"
                    )
                }


            }

            if (expanded) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Divider(modifier = Modifier.fillMaxWidth())
                    Row(
                        modifier = Modifier
                            .padding(25.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Wynik",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black, textAlign = TextAlign.Left
                        )
                        Text(text = "${result.result}")
                    }

                    Divider(modifier = Modifier.fillMaxWidth())

                    if (historicalResults.size < 2) {
                        Text(
                            text = "Za mało wyników, aby wyświetlić wykres",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        val data = historicalResults.map { (dateString, yValue) ->
                            LocalDate.parse(
                                dateString,
                                DateTimeFormatter.ofPattern("d-MM-yyyy")
                            ) to yValue
                        }.toMap()
                        val xValuesToDates = data.keys.associateBy { it.toEpochDay().toFloat() }
                        val chartEntryModel =
                            entryModelOf(xValuesToDates.keys.zip(data.values, ::entryOf))
                        val dateTimeFormatter: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("d.MM.yy")
                        val horizontalAxisValueFormatter =
                            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                                (xValuesToDates[value]
                                    ?: LocalDate.ofEpochDay(value.toLong())).format(
                                    dateTimeFormatter
                                )
                            }
                        var spacing = 1
                        if (historicalResults.size > 2) {
                            spacing = 2
                        }
                        val axisItemPlacer = AxisItemPlacer.Horizontal.default(
                            spacing = spacing,
                            shiftExtremeTicks = false,
                        )


                        Chart(
                            chart = columnChart(),
                            model = chartEntryModel,
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(
                                valueFormatter = horizontalAxisValueFormatter,
                                labelRotationDegrees = 15f,
                                itemPlacer = axisItemPlacer,
                                tickLength = 2.dp,
                            ),
                            modifier = Modifier.padding(bottom=15.dp)
                        )
                    }

                    Divider(modifier = Modifier.fillMaxWidth())
                    Column(
                        modifier = Modifier
                            .padding(25.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 0.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Zakres referencyjny",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black, textAlign = TextAlign.Left
                        )
                        ReferenceBar(
                            values,
                            result.rangeStart,
                            result.rangeEnd,
                            result.result,
                            Modifier
                                .fillMaxSize()
                        )
                    }
                    if (!isResultInRange) {
                        Divider(modifier = Modifier.fillMaxWidth())
                        Column(
                            modifier = Modifier
                                .padding(25.dp)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 0.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = "Twój wynik wykracza poza normę. Skonsultuj badania z lekarzem.",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black, textAlign = TextAlign.Left
                            )
                        }
                    }

                }

            }

        }
    }
}
