package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.*

data class CalculationRecord(
    val equation: String,
    val result: String
)

enum class CalculatorOperation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷"),
    POWER("^")
}

data class CalculatorState(
    val displayText: String = "0",
    val previousOperand: String = "",
    val operation: CalculatorOperation? = null,
    val isNewOperand: Boolean = true,
    val isScientificMode: Boolean = false,
    val showHistory: Boolean = false,
    val history: List<CalculationRecord> = emptyList()
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> processNumber(action.number)
            is CalculatorAction.Decimal -> processDecimal()
            is CalculatorAction.Clear -> processClear()
            is CalculatorAction.Operation -> processOperation(action.operation)
            is CalculatorAction.Calculate -> processCalculate()
            is CalculatorAction.ToggleSign -> processToggleSign()
            is CalculatorAction.Percent -> processPercent()
            is CalculatorAction.Sin -> processUnaryOperation("sin") { sin(it) }
            is CalculatorAction.Cos -> processUnaryOperation("cos") { cos(it) }
            is CalculatorAction.Tan -> processUnaryOperation("tan") { tan(it) }
            is CalculatorAction.Log10 -> processUnaryOperation("log₁₀") { log10(it) }
            is CalculatorAction.Ln -> processUnaryOperation("ln") { ln(it) }
            is CalculatorAction.SquareRoot -> processUnaryOperation("√") { sqrt(it) }
            is CalculatorAction.ToggleScientificMode -> _uiState.update { it.copy(isScientificMode = !it.isScientificMode) }
            is CalculatorAction.ToggleHistory -> _uiState.update { it.copy(showHistory = !it.showHistory) }
            is CalculatorAction.RestoreHistory -> _uiState.update { 
                it.copy(displayText = action.record.result, isNewOperand = true, showHistory = false) 
            }
        }
    }

    private fun processUnaryOperation(name: String, func: (Double) -> Double) {
        _uiState.update { state ->
            try {
                val value = state.displayText.toDouble()
                val result = func(value)
                val formattedResult = formatResult(result)
                val record = CalculationRecord("$name(${state.displayText})", formattedResult)
                state.copy(
                    displayText = formattedResult,
                    isNewOperand = true,
                    history = state.history + record
                )
            } catch (e: Exception) {
                state
            }
        }
    }

    private fun processNumber(number: Int) {
        _uiState.update { state ->
            if (state.isNewOperand) {
                state.copy(
                    displayText = number.toString(),
                    isNewOperand = false
                )
            } else {
                val currentDisplay = if (state.displayText == "0") "" else state.displayText
                if (currentDisplay.length >= 9 && !currentDisplay.contains(".") && !state.isScientificMode) {
                    return@update state
                }
                state.copy(
                    displayText = currentDisplay + number
                )
            }
        }
    }

    private fun processDecimal() {
        _uiState.update { state ->
            if (state.isNewOperand) {
                state.copy(
                    displayText = "0.",
                    isNewOperand = false
                )
            } else if (!state.displayText.contains(".")) {
                state.copy(
                    displayText = state.displayText + "."
                )
            } else {
                state
            }
        }
    }

    private fun processClear() {
        _uiState.update { state ->
            state.copy(
                displayText = "0",
                previousOperand = "",
                operation = null,
                isNewOperand = true
            )
        }
    }

    private fun processOperation(operation: CalculatorOperation) {
        _uiState.update { state ->
            if (state.operation != null && !state.isNewOperand) {
                val result = performCalculation(state.previousOperand, state.displayText, state.operation)
                val formattedResult = formatResult(result)
                val equation = "${state.previousOperand} ${state.operation.symbol} ${state.displayText}"
                val record = CalculationRecord(equation, formattedResult)
                
                state.copy(
                    displayText = formattedResult,
                    previousOperand = result.toString(),
                    operation = operation,
                    isNewOperand = true,
                    history = state.history + record
                )
            } else {
                state.copy(
                    previousOperand = state.displayText,
                    operation = operation,
                    isNewOperand = true
                )
            }
        }
    }

    private fun processCalculate() {
        _uiState.update { state ->
            if (state.operation == null || state.previousOperand.isEmpty()) {
                return@update state
            }
            val result = performCalculation(state.previousOperand, state.displayText, state.operation)
            val formattedResult = formatResult(result)
            val equation = "${state.previousOperand} ${state.operation.symbol} ${state.displayText}"
            val record = CalculationRecord(equation, formattedResult)
            
            state.copy(
                displayText = formattedResult,
                previousOperand = "",
                operation = null,
                isNewOperand = true,
                history = state.history + record
            )
        }
    }
    
    private fun processToggleSign() {
        _uiState.update { state ->
            if (state.displayText == "0" || state.displayText == "0.") {
                return@update state
            }
            val newDisplay = if (state.displayText.startsWith("-")) {
                state.displayText.substring(1)
            } else {
                "-" + state.displayText
            }
            state.copy(displayText = newDisplay)
        }
    }
    
    private fun processPercent() {
        _uiState.update { state ->
            try {
                val value = state.displayText.toDouble()
                val result = value / 100.0
                val formattedResult = formatResult(result)
                val record = CalculationRecord("${state.displayText}%", formattedResult)
                state.copy(
                    displayText = formattedResult,
                    isNewOperand = true,
                    history = state.history + record
                )
            } catch (e: Exception) {
                state
            }
        }
    }

    private fun performCalculation(left: String, right: String, operation: CalculatorOperation): Double {
        return try {
            val leftVal = left.toDouble()
            val rightVal = right.toDouble()
            when (operation) {
                CalculatorOperation.ADD -> leftVal + rightVal
                CalculatorOperation.SUBTRACT -> leftVal - rightVal
                CalculatorOperation.MULTIPLY -> leftVal * rightVal
                CalculatorOperation.DIVIDE -> {
                    if (rightVal == 0.0) Double.NaN else leftVal / rightVal
                }
                CalculatorOperation.POWER -> leftVal.pow(rightVal)
            }
        } catch (e: Exception) {
            Double.NaN
        }
    }

    private fun formatResult(result: Double): String {
        if (result.isNaN()) return "Error"
        
        val stringResult = result.toString()
        if (stringResult.endsWith(".0")) {
            return stringResult.substring(0, stringResult.length - 2)
        }
        
        if (stringResult.length > 10) {
            return try {
                val formatted = String.format("%.8g", result).replace(",", ".")
                if (formatted.contains("e")) formatted else stringResult.take(10)
            } catch (e: Exception) {
                stringResult.take(10)
            }
        }
        
        return stringResult
    }
}

sealed interface CalculatorAction {
    data class Number(val number: Int) : CalculatorAction
    data object Clear : CalculatorAction
    data object Decimal : CalculatorAction
    data object Calculate : CalculatorAction
    data class Operation(val operation: CalculatorOperation) : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction
    data object Sin : CalculatorAction
    data object Cos : CalculatorAction
    data object Tan : CalculatorAction
    data object Log10 : CalculatorAction
    data object Ln : CalculatorAction
    data object SquareRoot : CalculatorAction
    data object ToggleScientificMode : CalculatorAction
    data object ToggleHistory : CalculatorAction
    data class RestoreHistory(val record: CalculationRecord) : CalculatorAction
}

