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
        x = 2.0;
        y = x + 2;
        
        print(y);
        """

    val program3 = """
        sum = 0;
        for(i in 10..20) {
          sum = sum + i;
        }
        
        print("Loop 1 Sum: " ++ sum);
        
        sum = 0;
        for(i = 10; i <= 20; i+++) {
          sum = sum + i;
        }
        
        print("Loop 2 Sum: " ++ sum);
        """

    val program4 = """
        function greeting(name, message) {
          x = "Hi,";
          x = x ++ " my name is " ++ name ++ ".";
          print(x);
          print(message);
          
          return x ++ " " ++ message;
        }
        
        print(greeting("Albert", "How are you?"));
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

    val program6 = """
        function test(x,y){
            return x+y;
            print("This shouldn't print");
        }
        
        print(test(1,2));
    """

    val program7 = """
        int[] x = [2, 2, 3];
        print("x = " ++ x);
        
        print("Min (manual definition): " ++ min(2,2,3));
        print("Min (variables): " ++ min(x));
        
        print("Max (manual definition): " ++ max(2,2,3));
        print("Max (variables): " ++ max(x));
        
        print("Sum (manual definition): " ++ sum(2,2,3));
        print("Sum (variables): " ++ sum(x));
    """

    val program8 = """
        double[] x = [1.0, 2.0];
        sum = 0.0;
        for(i = 0; i <= len(x); i+++) {
          sum = sum + i;
        }
        
        print(sum);
    """

    val program9 = """
        x = false;
        y = true;
        if(x) {
            print("x");
        }
        else if (y) {
            print("y");
        }
    """

    println("Program 1: String Concatenation")
    execute(program1)

    println("\nProgram 2: Automatic Type Conversion")
    execute(program2)

    println("\nProgram 3: Loops")
    execute(program3)

    println("\nProgram 4: Functions")
    execute(program4)

    println("\nProgram 5: Recursion")
    execute(program5)

    println("\nProgram 6: Return Statements")
    execute(program6)

    println("\nProgram 7: Arrays & Aggregate Functions")
    execute(program7)

    println("\nProgram 8: Len() Aggregate Function")
    execute(program8)

    println("\nProgram 9: Boolean Values")
    execute(program9)
}