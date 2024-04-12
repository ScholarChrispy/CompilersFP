package backend

abstract class Expr {
    abstract fun eval(runtime:Runtime):Data
}

class NoneExpr(): Expr() {
    override fun eval(runtime:Runtime) = None
}

class IntLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = IntData(Integer.parseInt(lexeme))
}

class DoubleLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = DoubleData(lexeme.toDouble())
}

class FloatLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = FloatData(lexeme.toFloat())
}

class BooleanLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime): Data = 
    BooleanData(lexeme.equals("true"))
}

class StringLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime): Data { 
        return StringData(lexeme.substring(1, lexeme.length-1))
    }
}

class Arith(val op:String, val left:Expr, val right:Expr) : Expr() {
    override fun eval(runtime:Runtime):Data {
        val x = left.eval(runtime)
        val y = right.eval(runtime)
        if (x is IntData && y is IntData) {
            if (op == "+") return IntData(x.v + y.v)
            if (op == "-") return IntData(x.v - y.v)
            if (op == "*") return IntData(x.v * y.v)
            if (op == "/") return IntData(x.v / y.v)
        }
        if (x is StringData && y is IntData) {
            if (op == "*") return StringData(x.v.repeat(y.v))
        }
        if (y is StringData && x is IntData) {
            if (op == "*") return StringData(y.v.repeat(x.v))
        }
        if (op == "++") {
            return StringData(x.toString().plus(y.toString()))
        }
        return None
    }
    
}

class Assign(val name: String,val expr: Expr): Expr() {
    override fun eval(runtime:Runtime):Data {
        val v:Data = expr.eval(runtime)
        runtime.symbolTable.put(name, v)
        return None
    }
}

class Deref(val name:String): Expr() {
    override fun eval(runtime:Runtime):Data {
        val v = runtime.symbolTable[name]
        if(v != null) {
            return v
        } else {
            return None
        }
    }
}

class Block(val exprs:List<Expr>):Expr() {
    override fun eval(runtime:Runtime):Data{
        return exprs.map { it.eval(runtime) }.last()
    }
}

class Cmp(val op:String,val left:Expr,val right:Expr) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val x:Data = left.eval(runtime)
        val y:Data = right.eval(runtime)
        if(x is IntData && y is IntData) {
            val result = when(op) {
                "<" -> x.v < y.v
                "<=" -> x.v <= y.v
                ">" -> x.v > y.v
                ">=" -> x.v >= y.v
                "==" -> x.v == y.v
                "!=" -> x.v != y.v
                else -> { throw Exception("Invalid operator")}
                
            }
            return BooleanData(result)
        } else {
            throw Exception("Cannot perform comparison")
        }
    }
}

class Ifelse(val cond: Expr,val trueExpr: Expr,val falseExpr: Expr) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val result = cond.eval(runtime) as BooleanData
        return if(result.v) {
            trueExpr.eval(runtime)
        } else {
            falseExpr.eval(runtime)
        }
    }
}

class Print(val expr: Expr): Expr() {
    override fun eval(runtime:Runtime): Data {
        val data = expr.eval(runtime)
        println(data)
        return None
    }
}

class Loop(val creation: Expr, val cond: Expr, val body: Expr, val iterator: Expr): Expr() {
    override fun eval(runtime:Runtime): Data {
        creation.eval(runtime)
        while((cond.eval(runtime) as BooleanData).v) {
            body.eval(runtime)
            iterator.eval(runtime)
        }
        return None
    }
}

class Crement(val left: Expr, val op: String): Expr() {
    override fun eval(runtime:Runtime): Data {
        val x = left.eval(runtime)
        if (x is IntData) {
            return IntData(
                when(op) {
                    "+++" -> x.v + 1
                    "---" -> x.v - 1
                    else -> { throw Exception("Invalid operator")}
                }
            )
        }
        return None
    }
}

class FuncDef(val name: String, val parameters: List<String>, val body: Expr) : Expr() {
    override fun eval(runtime:Runtime):Data {
        val funcdata = FuncData(name, parameters, body)
        runtime.symbolTable[name] = funcdata
        return None
    }
}


class FuncCall(val funcname: String, val arguments: List<Expr>) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val f = runtime.symbolTable[funcname]
        if(f == null) {
            throw Exception("$funcname does not exist.")
        }
        if(f !is FuncData) {
            throw Exception("$funcname is not a function.")
        }
        if(arguments.size != f.parameters.size) {
            throw Exception("$funcname expects ${f.parameters.size} arguments, but ${arguments.size} given.")
        }
        
        // evaluate each argument to a data
        val argumentData = arguments.map {
            it.eval(runtime)
        }

        // create a subscope and evaluate the body using the subscope
        return f.body.eval(runtime.copy(
            f.parameters.zip(argumentData).toMap()
        ))
    }
}

/*class Negative(val expression: Expr) : Expr() {
     override fun eval(runtime:Runtime): Data {
        try {
            val neg = -1*expression.eval(runtime)
            return neg
        } catch (e: NumberFormatException) {
            println("Error: ${e.message}")
        }
        return None
    }
}*/

//class sum(val arguments: List<Expr>) : Expr() {
        
//}