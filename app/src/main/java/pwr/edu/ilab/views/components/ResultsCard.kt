package pwr.edu.ilab.views.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pwr.edu.ilab.models.DbTestInfo
import pwr.edu.ilab.ui.theme.LetteringCard
import pwr.edu.ilab.ui.theme.MyRose
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize


@androidx.compose.runtime.Composable
fun ResultsCard(testName: String, testUnit: String, tests: List<DbTestInfo>) {
    var expanded by remember { mutableStateOf(false) }
    var currentlySelected by remember { mutableStateOf(tests[0]) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    val testValues: SnapshotStateMap<String, String> = remember { mutableStateMapOf() }
    remember(key1 = currentlySelected) {
        testValues.map {
            println(it.key)
            println(it.value)
        }
        currentlySelected.mapify().map { entry ->
            testValues[entry.key] = ""
        }
    }

    val modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .fillMaxWidth(0.5f)

    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

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
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
                .fillMaxHeight(0.6f),
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
                        .padding(5.dp)
                ) {
                    Text(
                        text = currentlySelected.name,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                    Button(onClick = { expanded = !expanded }) {
                        Icon(icon, "Dropdown trigger button")
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
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
                            Text(text = test.name)
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
                    val testValuePlaceholderLabel = elementInfo.value.name
                    OutlinedTextField(
                        modifier = modifier,
                        value = "${testValues[elementInfo.value.name]}",
                        shape = RoundedCornerShape(percent = 30),
                        placeholder = { Text(text = testValuePlaceholderLabel) },
                        label = { Text(text = testValuePlaceholderLabel, fontSize = 16.sp) },
                        onValueChange = { newTestValue ->
                            println(newTestValue)
                            testValues[elementInfo.value.name] = newTestValue
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )


                    Text(
                        text = elementInfo.value.unit,
                        textAlign = TextAlign.End,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
