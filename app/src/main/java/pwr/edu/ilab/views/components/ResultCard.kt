package pwr.edu.ilab.views.components

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pwr.edu.ilab.models.DbTestInfo
import pwr.edu.ilab.ui.theme.LetteringCard
import pwr.edu.ilab.ui.theme.MyRose

@SuppressLint("RememberReturnType")
@Composable
fun ResultCard(
    tests: List<DbTestInfo>,
    dbTestInfo: DbTestInfo,
    index: Int,
    testValues: MutableMap<String, String>?,
    setTestChoice: (Int, MutableMap<String, String>) -> Unit,
    updateResults: (MutableMap<String, String>) -> Unit,
    removeResultsForPrevIdx: () -> Unit
) {
    if (testValues == null) return

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    var currentlySelected by remember { mutableStateOf(dbTestInfo) }
    var start by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = currentlySelected) {
        setTestChoice(tests.indexOf(currentlySelected), testValues)
    }

    val modifier = Modifier
        .padding(horizontal = 4.dp, vertical = 4.dp)
        .fillMaxWidth(0.64f)

    Column(
        modifier = Modifier
            .padding(30.dp, 0.dp, 30.dp, 0.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .border(1.dp, color = MyRose, shape = RoundedCornerShape(25.dp))
                .background(LetteringCard)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, color = MyRose, shape = RoundedCornerShape(20.dp))
                        .background(color = Color.White)
                        .onSizeChanged { newSize -> textFieldSize = newSize }
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentlySelected.name,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Dropdown trigger button",
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 25.dp
                                ),
                                onClick = { expanded = !expanded }
                            )
                            .padding(12.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .scrollable(rememberScrollState(), Orientation.Vertical)
                        .then(
                            with(LocalDensity.current) {
                                Modifier.width(textFieldSize.width.toDp())
                            })
                ) {
                    tests.forEach { test ->
                        DropdownMenuItem(onClick = {
                            currentlySelected = test
                            expanded = false
                        }) {
                            Text(text = test.name.capitalize(Locale.current))
                        }
                    }

                }

            }

            currentlySelected.elements.forEach { elementInfo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var testValueLabel = elementInfo.value.name
                    val testValue = testValues[testValueLabel]
                    val shownValue = testValue ?: "0"
                    val testValuePlaceholder = shownValue
                    OutlinedTextField(
                        modifier = modifier,
                        value = if (shownValue == "0" && start) "" else shownValue,
                        shape = RoundedCornerShape(percent = 30),
                        placeholder = { Text(text = testValuePlaceholder) },
                        label = { Text(text = testValueLabel, fontSize = 14.sp) },
                        onValueChange = { newTestValue ->
                            val newTestValue =
                                newTestValue.replace(" ", "").replace("\t", "").replace("\n", "")
                                    .replace(",", ".")
                            start = false
                            if (newTestValue.isEmpty()) {
                                start = true
                                testValues[elementInfo.value.name] = "0"
                            } else {
                                testValues[elementInfo.value.name] = newTestValue
                            }
                            updateResults(testValues)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )


                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = elementInfo.value.unit,
                            textAlign = TextAlign.End,
                            fontSize = 15.sp,
                        )
                        Text(
                            text = "${elementInfo.value.rangeStart} - ${elementInfo.value.rangeEnd}",
                            textAlign = TextAlign.End,
                            fontSize = 15.sp,
                        )

                    }
                }
            }
        }
    }
}
