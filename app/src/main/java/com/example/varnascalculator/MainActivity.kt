package com.example.varnascalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.varnascalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var canAddOperator: Boolean = false
    private var canAddDecimal: Boolean = false

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

   fun onDigitClick(view: View){
        if (view is Button){
            if (view.text == "."){
                if (canAddDecimal){
                    binding.tvNumber.append(view.text)
                    canAddDecimal = false
                }
            } else {
                binding.tvNumber.append(view.text)
                canAddOperator = true
            }
            binding.tvResult.text = calculateResult()
        }
    }

   fun onOperatorClick(view: View){
        if (view is Button && canAddOperator){
            binding.tvNumber.append(view.text)
            canAddOperator = false
            canAddDecimal = true
        }
    }

    fun onAllClearClick(view: View){
        binding.tvNumber.text = ""
        binding.tvResult.text = ""
        binding.tvResult.textSize = 40F
    }

    fun onBackClick(view: View){
        val textResult = binding.tvResult.text
        val length = binding.tvNumber.length()
        if (textResult.isNotEmpty() && length > 0){
            binding.tvNumber.text = binding.tvNumber.text.subSequence(0, length - 1)
            binding.tvResult.text = binding.tvResult.text.subSequence(0, length - 1)
        }
    }

    fun onEqualClick(view: View){
        if (view is Button){
            binding.apply {
                tvResult.text = calculateResult()
                tvResult.textSize = 60F
                tvNumber.text = ""
            }

        }
    }


    private fun calculateResult(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timeDivision = timeDivisionCalc(digitsOperators)
        if (timeDivision.isEmpty()) return ""

        val result = addSubtractCalc(timeDivision)
        return result.toString()
    }

    private fun addSubtractCalc(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices){
            if (passedList[i] is Char && i != passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+') result +=  nextDigit
                if (operator == '-') result -=  nextDigit
            }
        }

        return result
    }

    private fun calcTimeDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices){
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator){
                    '*' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex) newList.add(passedList[i])
        }

        return newList
    }

    private fun timeDivisionCalc(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while(list.contains('*') || list.contains('/')){
            list = calcTimeDiv(list)
        }
        return list
    }

    // splits the tvNumber into an array of digits, operators and floats
    private fun digitsOperators(): MutableList<Any>{
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in binding.tvNumber.text){
            if (character.isDigit() || character == '.'){
                currentDigit += character
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != ""){
            list.add(currentDigit.toFloat())
        }

        return list
    }
}