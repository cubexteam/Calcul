package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel

val AppleDarkGray = Color(0xFF333333)
val AppleLightGray = Color(0xFFA5A5A5)
val AppleOrange = Color(0xFFFF9F0A)
val AppleBlack = Color(0xFF000000)
val AppleDarkBlue = Color(0xFF001F3F)

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    val buttonSpacing = 16.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(buttonSpacing),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Top bar for toggles
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.onAction(CalculatorAction.ToggleHistory) }) {
                Icon(Icons.Default.History, contentDescription = "History", tint = if (state.showHistory) AppleOrange else Color.Gray)
            }
            IconButton(onClick = { viewModel.onAction(CalculatorAction.ToggleScientificMode) }) {
                Icon(Icons.Default.Science, contentDescription = "Scientific Mode", tint = if (state.isScientificMode) AppleOrange else Color.Gray)
            }
        }

        // Display area
        if (state.showHistory) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppleDarkBlue)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Bottom,
                reverseLayout = true
            ) {
                items(state.history.reversed()) { record ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onAction(CalculatorAction.RestoreHistory(record)) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(text = record.equation, color = Color.Gray, fontSize = 20.sp)
                        Text(text = record.result, color = Color.White, fontSize = 28.sp)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = state.displayText,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 8.dp)
                        .testTag("display_text"),
                    fontWeight = FontWeight.Light,
                    fontSize = if (state.displayText.length > 6) 60.sp else 80.sp,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
        
        // Rows
        Column(
            verticalArrangement = Arrangement.spacedBy(buttonSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            val sciButtonHeight = 48.dp

            if (state.isScientificMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton(symbol = "sin", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Sin) })
                    CalculatorButton(symbol = "cos", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Cos) })
                    CalculatorButton(symbol = "tan", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Tan) })
                    CalculatorButton(symbol = "xⁿ", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.POWER)) })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    CalculatorButton(symbol = "log", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Log10) })
                    CalculatorButton(symbol = "ln", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.Ln) })
                    CalculatorButton(symbol = "√", color = AppleDarkGray, modifier = Modifier.weight(1f).height(sciButtonHeight), fontSize = 20.sp, onClick = { viewModel.onAction(CalculatorAction.SquareRoot) })
                    Box(modifier = Modifier.weight(1f)) // Empty box for alignment
                }
            }
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "AC", // We can improve to clear vs clear all (C/AC) later
                    color = AppleLightGray,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Clear) }
                )
                CalculatorButton(
                    symbol = "+/-",
                    color = AppleLightGray,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.ToggleSign) }
                )
                CalculatorButton(
                    symbol = "%",
                    color = AppleLightGray,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Percent) }
                )
                CalculatorButton(
                    symbol = "÷",
                    color = if (state.operation == CalculatorOperation.DIVIDE) Color.White else AppleOrange,
                    textColor = if (state.operation == CalculatorOperation.DIVIDE) AppleOrange else Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.DIVIDE)) }
                )
            }
            
            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "7",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(7)) }
                )
                CalculatorButton(
                    symbol = "8",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(8)) }
                )
                CalculatorButton(
                    symbol = "9",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(9)) }
                )
                CalculatorButton(
                    symbol = "×",
                    color = if (state.operation == CalculatorOperation.MULTIPLY) Color.White else AppleOrange,
                    textColor = if (state.operation == CalculatorOperation.MULTIPLY) AppleOrange else Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.MULTIPLY)) }
                )
            }
            
            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "4",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(4)) }
                )
                CalculatorButton(
                    symbol = "5",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(5)) }
                )
                CalculatorButton(
                    symbol = "6",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(6)) }
                )
                CalculatorButton(
                    symbol = "-",
                    color = if (state.operation == CalculatorOperation.SUBTRACT) Color.White else AppleOrange,
                    textColor = if (state.operation == CalculatorOperation.SUBTRACT) AppleOrange else Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.SUBTRACT)) }
                )
            }
            
            // Row 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "1",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(1)) }
                )
                CalculatorButton(
                    symbol = "2",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(2)) }
                )
                CalculatorButton(
                    symbol = "3",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Number(3)) }
                )
                CalculatorButton(
                    symbol = "+",
                    color = if (state.operation == CalculatorOperation.ADD) Color.White else AppleOrange,
                    textColor = if (state.operation == CalculatorOperation.ADD) AppleOrange else Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.ADD)) }
                )
            }
            
            // Row 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "0",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(2f).aspectRatio(2f), // Double width
                    isWide = true,
                    onClick = { viewModel.onAction(CalculatorAction.Number(0)) }
                )
                CalculatorButton(
                    symbol = ".",
                    color = AppleDarkGray,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Decimal) }
                )
                CalculatorButton(
                    symbol = "=",
                    color = AppleOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onAction(CalculatorAction.Calculate) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RowScope.CalculatorButton(
    symbol: String,
    color: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 32.sp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = if (isWide) Alignment.CenterStart else Alignment.Center,
        modifier = modifier
            .clip(if (isWide) RoundedCornerShape(percent = 50) else CircleShape) // Pill shape for 0
            .background(color)
            .clickable { onClick() }
            .then(if (!isWide && fontSize == 32.sp) Modifier.aspectRatio(1f) else Modifier)
            .testTag("btn_${symbol}")
    ) {
        Text(
            text = symbol,
            fontSize = fontSize,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = if (isWide) Modifier.padding(start = 28.dp) else Modifier
        )
    }
}
