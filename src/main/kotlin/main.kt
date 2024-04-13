package backend

import PL.PLLexer
import PL.PLParser
import org.antlr.v4.runtime.*

fun main() {
    fun execute(source:String) {
        val errorlistener = object: BaseErrorListener() {
            override fun syntaxError(recognizer: Recognizer<*,*>,
                                     offendingSymbol: Any?,
                                     line: Int,
                                     pos: Int,
                                     msg: String,
                                     e: RecognitionException?) {
                throw Exception("${e} at line:${line}, char:${pos}")
            }
        }
        val input = CharStreams.fromString(source)
        val lexer = PLLexer(input).apply {
            removeErrorListeners()
            addErrorListener(errorlistener)
        }
        val tokens = CommonTokenStream(lexer)
        val parser = PLParser(tokens).apply {
            removeErrorListeners()
            addErrorListener(errorlistener)
        }

        try {
            val result = parser.program()
            result.expr.eval(Runtime())
        } catch(e:Exception) {
            println("Error: ${e}")
        }
    }

    val program1 = """
        x = "Hello";
        y = "World";
        
        print(x ++ " " ++ y);
        """

    val program2 = """
        x = "woof ";
        y = "Dog goes " ++ (x * 2);
        
        print(y);
        """

    val program3 = """
        sum = 0;
        for(i in 10..20) {
          sum = sum + i;
        }
        
        print(sum);
        """

    val program4 = """
        function greeting(name, message) {
          x = "Hi,";
          x = x ++ " my name is " ++ name ++ ".";
          print(x);
          print(message);
        }
        
        greeting("Albert", "How are you?");
        """

    val program5 = """
        function factorial(n) {
          if(n < 2) {
            1;
          } else {
            n * factorial(n-1);
          }
        }
        
        print(factorial(10));
        """

    println("Program1: " + execute(program1))
    println("Program2: " + execute(program2))
    println("Program3: " + execute(program3))
    println("Program4: " + execute(program4))
    println("Program5: " + execute(program5))
}