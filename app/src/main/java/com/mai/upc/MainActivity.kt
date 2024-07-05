package com.mai.upc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.mai.upc.ui.theme.UPCTheme
import kotlinx.coroutines.launch
import java.lang.Double.isFinite
import java.util.Locale
import kotlin.math.abs

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(this)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(activity: MainActivity) {
    var price1 by remember { mutableStateOf("") }
    var unit1 by remember { mutableStateOf("") }
    var up1 by remember { mutableStateOf("单价：") }
    var price2 by remember { mutableStateOf("") }
    var unit2 by remember { mutableStateOf("") }
    var up2 by remember { mutableStateOf("单价：") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var resultState by remember { mutableIntStateOf(0) }

    val focusManager = LocalFocusManager.current

    UPCTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(getString(activity, R.string.app_name)) }
                )
            }) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    OutlinedTextField(
                        value = price1,
                        onValueChange = { newValue ->
                            // 限制只允许输入数字
                            price1 = newValue.filter {
                                isFinite(
                                    newValue.toDoubleOrNull() ?: Double.NaN
                                )
                            }
                        },
                        label = { Text("价格") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unit1,
                        onValueChange = { newValue ->
                            // 限制只允许输入数字
                            unit1 = newValue.filter { it.isDigit() }
                        },
                        label = { Text("单位") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Text(
                    text = up1,
                    color = when (resultState) {
                        1 -> Color.Red
                        else -> Color.Unspecified
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    OutlinedTextField(
                        value = price2,
                        onValueChange = { newValue ->
                            // 限制只允许输入数字
                            price2 = newValue.filter {
                                isFinite(
                                    newValue.toDoubleOrNull() ?: Double.NaN
                                )
                            }
                        },
                        label = { Text("价格") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = unit2,
                        onValueChange = { newValue ->
                            // 限制只允许输入数字
                            unit2 = newValue.filter { it.isDigit() }
                        },
                        label = { Text("单位") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Text(
                    text = up2,
                    color = when (resultState) {
                        2 -> Color.Red
                        else -> Color.Unspecified
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    onClick = {
                        focusManager.clearFocus()

                        val up1Num = (price1.toDoubleOrNull() ?: 0.0) / (unit1.toIntOrNull() ?: 1)
                        val up2Num = (price2.toDoubleOrNull() ?: 0.0) / (unit2.toIntOrNull() ?: 1)
                        up1 = "单价:$up1Num"
                        up2 = "单价:$up2Num"

                        resultState = if (up1Num < up2Num) 1 else if (up1Num > up2Num) 2 else 0

                        val msg =
                            when (resultState) {
                                1 -> "上方胜出"
                                2 -> "下方胜出"
                                else -> "价格相同"
                            } + "；差价为:${
                                String.format(
                                    Locale.getDefault(),
                                    "%.3f",
                                    abs(up1Num - up2Num)
                                )
                            }"

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = msg,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }) {
                    Text("比较")
                }

                Button(
                    onClick = {
                        price1 = ""
                        price2 = ""
                        up1 = "单价:"
                        up2 = "单价:"
                        unit1 = ""
                        unit2 = ""
                        resultState = 0
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                ) {
                    Text("重置")
                }
            }
        }
    }

}