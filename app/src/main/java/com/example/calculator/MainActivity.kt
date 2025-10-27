package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    private var firstNumber = 0.0
    private var secondNumber = 0.0
    private var operator = ""
    private var isNew = true
    private var currentInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.resultTextView)

        val button0: Button = findViewById(R.id.button0)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)

        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        val buttonSubtract: Button = findViewById(R.id.buttonSubtract)
        val buttonMultiply: Button = findViewById(R.id.buttonMultiply)
        val buttonDivide: Button = findViewById(R.id.buttonDivide)
        val buttonEquals: Button = findViewById(R.id.buttonEquals)
        val buttonClear: Button = findViewById(R.id.buttonClear)

        button0.setOnClickListener { numberPressed("0") }
        button1.setOnClickListener { numberPressed("1") }
        button2.setOnClickListener { numberPressed("2") }
        button3.setOnClickListener { numberPressed("3") }
        button4.setOnClickListener { numberPressed("4") }
        button5.setOnClickListener { numberPressed("5") }
        button6.setOnClickListener { numberPressed("6") }
        button7.setOnClickListener { numberPressed("7") }
        button8.setOnClickListener { numberPressed("8") }
        button9.setOnClickListener { numberPressed("9") }

        buttonAdd.setOnClickListener { setOperation("+") }
        buttonSubtract.setOnClickListener { setOperation("-") }
        buttonMultiply.setOnClickListener { setOperation("*") }
        buttonDivide.setOnClickListener { setOperation("/") }

        buttonEquals.setOnClickListener { showResult() }
        buttonClear.setOnClickListener { clearAll() }
    }

    private fun numberPressed(number: String) {
        if (isNew) {
            resultTextView.text = number
            isNew = false
        } else {
            resultTextView.text = resultTextView.text.toString() + number
        }
        currentInput = resultTextView.text.toString()
    }

    private fun setOperation(op: String) {
        if (currentInput.isNotEmpty()) {
            firstNumber = currentInput.toDouble()
            operator = op
            isNew = true
        }
    }

    private fun showResult() {
        if (operator.isEmpty() || currentInput.isEmpty()) {
            return
        }

        secondNumber = currentInput.toDouble()
        var result = 0.0

        if (operator == "+") {
            result = firstNumber + secondNumber
        } else if (operator == "-") {
            result = firstNumber - secondNumber
        } else if (operator == "*") {
            result = firstNumber * secondNumber
        } else if (operator == "/") {
            if (secondNumber != 0.0) {
                result = firstNumber / secondNumber
            } else {
                resultTextView.text = "Ошибка"
                clearAll()
                return
            }
        }

        if (result % 1 == 0.0) {
            resultTextView.text = result.toInt().toString()
        } else {
            resultTextView.text = result.toString()
        }

        currentInput = resultTextView.text.toString()
        isNew = true
        operator = ""
    }

    private fun clearAll() {
        resultTextView.text = "0"
        currentInput = ""
        firstNumber = 0.0
        secondNumber = 0.0
        operator = ""
        isNew = true
    }
}
