package pwr.edu.ilab.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReferenceBar(
    values: List<Double>,
    start: Number,
    end: Number,
    result: Number,
    modifier: Modifier = Modifier
) {
    val isResultInRange = values[2] <= values[1] && values[2] >= values[0]
    val resultColor = if (isResultInRange) InRangeColor else NotInRangeColor
    println(values)
    println(result)

    RangeSlider(
        values,
        result = result.toFloat(),
        value = values[0].toFloat()..values[1].toFloat(),
        steps = 0,
        onValueChange = { },
        valueRange = 0.6f * values.min().toFloat()..1.2f * values.max().toFloat(),
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            disabledThumbColor = Color.Transparent,
            activeTrackColor = MaterialTheme.colors.primary,
            inactiveTrackColor = MaterialTheme.colors.onSecondary,
        ),
        modifier = Modifier
            .padding(0.dp)
            .shadow(0.dp)
            .background(Color.Transparent, RectangleShape),
        enabled = false,
    )
}