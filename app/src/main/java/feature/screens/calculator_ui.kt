package feature.screens
import java.util.*

import android.service.autofill.OnClickAction
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simple_calculator.ui.theme.Purple80
import com.example.simple_calculator.ui.theme.PurpleGrey40

@Composable
fun CalculatorUI() {
    var userInput = remember {
        mutableStateOf("")
    }
    var answer = remember {
        mutableStateOf("0")
    }
    val numbers = listOf(
        "C", "9", "7", "8", "%",
        "4", "5", "6", "/",
        "1", "2", "3", "x",
        "-", "+", "0"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Display Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Allocate 2 parts of vertical space
                .background(color = Color.White)
                .padding(end = 16.dp),
        ) {
           Column (modifier=Modifier.align(Alignment.Center)
           ){
               Box(
                   modifier = Modifier.fillMaxWidth(),
                   contentAlignment = Alignment.CenterEnd
               ) {
                   Text(
                       text =   userInput.value,
                       style = TextStyle(fontSize = 36.sp, color = Color.Gray)
                   )
               }

               Box(
                   modifier = Modifier.fillMaxWidth(),
                   contentAlignment = Alignment.CenterEnd
               ) {
                   Text(
                       text = answer.value,
                       style = TextStyle(fontSize = 36.sp, color = Color.Black)
                   )
               }
           }
        }

        // Buttons Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(numbers) { icon ->
                CalculatorButton(
                    icon = icon,
                    click = {
                        Log.d ("userInput",userInput.value)
                            when(icon){

                                    "C" -> {
                                        userInput.value = ""
                                        answer.value = "0"
                                    }
                                    "%" -> {
                                        userInput.value += "%"
                                    }
                                    "/" -> {
                                        userInput.value += "/"
                                    }
                                    "x" -> {
                                        userInput.value += "*"
                                    }
                                    "-" -> {
                                        userInput.value += "-"
                                    }
                                    "+" -> {
                                        userInput.value += "+"
                                    }
                                    else -> {
                                        userInput.value += icon

                                    }}
                    },
                    color = when (icon) {
                        "C" -> Color.Red // Specific button color
                        else -> null
                    }
                )
            }
        }

        // "=" Button
        Box(

            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(80.dp)
                .background(color = PurpleGrey40).clickable {
                                                            answer.value= evaluateExpression(userInput.value).toString()
                }
               ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "=",
                style = TextStyle(fontSize = 30.sp, color = Color.White)
            )
        }
    }
}

@Composable
fun CalculatorButton(icon: String, color: Color? = null,click:()->Unit) {

       Box(
           modifier = Modifier
               .padding(4.dp)
               .fillMaxWidth() // Align buttons to grid width
               .aspectRatio(1f) // Square shape for buttons
               .background(color = color ?: Color.LightGray)
               .border(1.dp, Color.Black)
               .clickable { click() },
           contentAlignment = Alignment.Center
       ) {
           Text(
               text = icon,
               style = TextStyle(
                   fontSize = 20.sp,
                   color = if (color == null) Color.Black else Color.White
               )
           )
   }
}

fun evaluateExpression(expression: String): Double {
    val numbers = Stack<Double>()
    val operators = Stack<Char>()

    var i = 0
    while (i < expression.length) {
        val c = expression[i]

        when {
            c.isWhitespace() -> i++  // Ignore whitespaces
            c.isDigit() || c == '.' -> {
                // Handle numbers, including decimal points
                val start = i
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    i++
                }
                numbers.push(expression.substring(start, i).toDouble())
            }
            c == '(' -> {
                operators.push(c)
                i++
            }
            c == ')' -> {
                while (operators.peek() != '(') {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()))
                }
                operators.pop()
                i++
            }
            isOperator(c) -> {
                while (operators.isNotEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()))
                }
                operators.push(c)
                i++
            }
            else -> throw IllegalArgumentException("Unexpected character: $c")
        }
    }

    while (operators.isNotEmpty()) {
        numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()))
    }

    return numbers.pop()
}

fun isOperator(c: Char) = c == '+' || c == '-' || c == '*' || c == '/' || c == '%'

fun precedence(op: Char): Int {
    return when (op) {
        '+', '-' -> 1
        '*', '/', '%' -> 2
        else -> throw IllegalArgumentException("Unknown operator: $op")
    }
}

fun applyOperator(op: Char, b: Double, a: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> a / b
        '%' -> a % b
        else -> throw IllegalArgumentException("Unknown operator: $op")
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun PreviewCalculatorUI() {
//    CalculatorUI()
//}
