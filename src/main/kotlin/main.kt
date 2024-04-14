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
       function test(x, y) {
        if (x == y) {
            return;
        } else if (x > y) {
            return x;
        } else {
            return y;
        }
       } 
       
       print(test(1.5, 1.5));
       print(test(1, 2));
       print(test(1, 1.5));
    """

    val program7 = """
        int[] x = [2, 2, 3];
        print(min(2,2,3));
        print(min(x));
        print(max(2,2,3));
        print(max(x));
        print(sum(2,2,3));
        print(sum(x));
    """

    val program8 = """
        double[] x = [1.0, 2.0];
        sum = 0.0;
        for(i = 0; i <= len(x); i +++) {
          sum = sum + i;
        }
        
        print(sum);
    """

    println("Program1: " + execute(program1))
    println("Program2: " + execute(program2))
    println("Program3: " + execute(program3))
    println("Program4: " + execute(program4))
    println("Program5: " + execute(program5))
    println("Program6: " + execute(program6))
    println("Program7: " + execute(program7))
    println("Program8: " + execute(program8))
}