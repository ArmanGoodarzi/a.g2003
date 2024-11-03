package ir.ag.convertinstack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ir.ag.convertinstack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fun convertPostfixToInfix(postfix: String): String {
            val stack = mutableListOf<String>()
            for (char in postfix){
                when {
                    char.isLetterOrDigit() -> stack.add(char.toString())
                    else -> {
                        val operand1 = stack.removeAt(stack.lastIndex)
                        val operand2 = stack.removeAt(stack.lastIndex)
                        stack.add("($operand2$char$operand1)")
                    }
                }
            }
            return if (stack.isNotEmpty()) stack[0] else "Invalid Expression"
        }
        fun convertInfixToPostfix(infix: String): String {
            val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '^' to 3)
            val stack = mutableListOf<Char>()
            val result = StringBuilder()

            for (char in infix){
                when {
                    char.isLetterOrDigit() -> result.append(char)
                    char == '(' -> stack.add(char)
                    char == ')' -> {
                        while (stack.isNotEmpty() && stack.last() != '(') {
                            result.append(stack.removeAt(stack.lastIndex))
                        }
                        stack.removeAt(stack.lastIndex)
                    }
                    char in precedence.keys -> {
                        while (stack.isNotEmpty() && stack.last() != '(' &&
                            precedence[char]!! <= precedence[stack.last()]!!
                        ){
                            result.append(stack.removeAt(stack.lastIndex))
                        }
                        stack.add(char)
                    }
                }
            }
            while (stack.isNotEmpty()){
                result.append(stack.removeAt(stack.lastIndex))
            }
            return result.toString()
        }
        fun isValidPostfix(postfix:String):Boolean{
            var operandCount = 0
            for (char in postfix){
                when{
                    char.isLetterOrDigit() -> operandCount++
                    else -> operandCount--
                }
                if (operandCount < 0) return false
            }
            return operandCount == 1
        }
        fun isValidInfix(infix:String):Boolean {
            var hasOperator = false
            for (char in infix) {
                when {
                    char.isLetterOrDigit() -> hasOperator = false
                    char in listOf('+', '-', '*', '/', '^') -> {
                        if (hasOperator) return false
                        hasOperator = true
                    }
                    char !in listOf('(', ')') -> return false
                }
            }
            return true
        }
        binding.btnPostfixToInfix.setOnClickListener {
            val postfix = binding.txtFieldPostfix.editText?.text.toString()
            if (postfix.isNotEmpty()) {
                if (isValidPostfix(postfix)) {
                    val infix = convertPostfixToInfix(postfix)
                    binding.tvResultPostfix.text = "Infix: $infix"
                }else {
                    binding.tvResultPostfix.text = "Please enter a postfix expression"
                }
            } else {
                binding.tvResultPostfix.text = "Invalid Postfix expression"
            }
        }
        binding.btnInfixToPostfix.setOnClickListener {
            val infix = binding.txtFieldInfix.editText?.text.toString()
            if (infix.isNotEmpty()) {
                if (isValidInfix(infix)) {
                    val postfix = convertInfixToPostfix(infix)
                    binding.tvResultInfix.text = "Postfix: $postfix"
                } else {
                    binding.tvResultInfix.text = "Please enter an infix expression"
                }
            }else{
                binding.tvResultInfix.text = "Invalid Infix expression"
            }
        }
        binding.btnClear.setOnClickListener {
            binding.txtFieldPostfix.editText?.text?.clear()
            binding.txtFieldInfix.editText?.text?.clear()
            binding.tvResultPostfix.text = "Result: "
            binding.tvResultInfix.text = "Result: "
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}