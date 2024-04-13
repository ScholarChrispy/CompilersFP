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
        if (x is FloatData && y is FloatData) {
            if (op == "+") return FloatData(x.v + y.v)
            if (op == "-") return FloatData(x.v - y.v)
            if (op == "*") return FloatData(x.v * y.v)
            if (op == "/") return FloatData(x.v / y.v)
        }
        if (x is DoubleData && y is DoubleData) {
            if (op == "+") return DoubleData(x.v + y.v)
            if (op == "-") return DoubleData(x.v - y.v)
            if (op == "*") return DoubleData(x.v * y.v)
            if (op == "/") return DoubleData(x.v / y.v)
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
        }
        else if (x is FloatData && y is FloatData) {
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
        }
        else if (x is DoubleData && y is DoubleData) {
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
        }
        else {
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


class Sum(val arguments: List<Expr>) : Expr() {
    override fun eval(runtime:Runtime): Data {
        val x:Data = arguments[0].eval(runtime)
        if(x is IntData || x is DoubleData || x is FloatData) {
            var resultInt = 0
            var resultDouble = 0.0
            var resultFloat = 0.0f
            var intcheck = true
            var doublecheck = false
            var floatcheck = false
            arguments.forEach { arg ->
                var temp:Data = arg.eval(runtime)
                if (doublecheck == true && temp is IntData) {
                    temp = DoubleData(temp.v.toDouble())
                }
                if (floatcheck == true && (temp is IntData)) {
                    temp = FloatData(temp.v.toFloat())
                }
                if (floatcheck == true && (temp is DoubleData)) {
                    temp = FloatData(temp.v.toFloat())
                }
                
                if (temp is IntData) {
                    resultInt = resultInt + temp.v
                }
                else if (temp is DoubleData) {
                    if (resultDouble == 0.0){
                        resultDouble = resultInt.toDouble()
                    }
                    resultDouble = resultDouble + temp.v
                    intcheck = false
                    doublecheck = true
                }
                else if (temp is FloatData) {
                    if (resultFloat == 0.0f && intcheck == true){
                        resultFloat = resultInt.toFloat()
                    }
                    if (resultFloat == 0.0f && doublecheck == true){
                        resultFloat = resultDouble.toFloat()
                    }
                    resultFloat = resultFloat + temp.v
                    intcheck = false
                    doublecheck = false
                    floatcheck = true
                }
                else {
                    throw Exception("$temp is not a valid number.")
                }
            }
            if (intcheck == true) {
                return IntData(resultInt)
            }
            else if (doublecheck == true) {
                return DoubleData(resultDouble)
            }
            else if (floatcheck == true) {
                return FloatData(resultFloat)
            }
        }
        else {
            throw Exception("$x is not a valid number.")
        }
        return None
    }
}

class Max(val arguments: List<Expr>) : Expr() {
     override fun eval(runtime:Runtime): Data {
         val x:Data = arguments[0].eval(runtime)
        if(x is IntData || x is DoubleData || x is FloatData) {
            var highest = -Float.MAX_VALUE
            var highestIndex = 0
             for (i in 0 until (arguments.size)) {
                 var temp:Data = arguments[i].eval(runtime)
                 if (temp is IntData) {
                     temp = FloatData(temp.v.toFloat()) 
                 }
                 if (temp is DoubleData) {
                     temp = FloatData(temp.v.toFloat()) 
                 }
                 if (temp is FloatData) {
                     if (i == 0) {
                         highest = temp.v
                     }
                     else {
                         if (highest < temp.v){
                             highest = temp.v
                             highestIndex = i
                         }
                     }
                 }
                 else {
                     throw Exception("Cannot compare $temp.")
                 }
             }
             val dataCheck:Data = arguments[highestIndex].eval(runtime)
             if (dataCheck is IntData){
                 return IntData(dataCheck.v)
             }
             if (dataCheck is DoubleData){
                 return DoubleData(dataCheck.v)
             }
             if (dataCheck is FloatData){
                 return FloatData(dataCheck.v)
             }
            
        }
        else {
            throw Exception("$x is not a valid number.")
        }
        return None
     }
}

class Min(val arguments: List<Expr>) : Expr() {
     override fun eval(runtime:Runtime): Data {
         val x:Data = arguments[0].eval(runtime)
        if(x is IntData || x is DoubleData || x is FloatData) {
            var lowest = Float.MAX_VALUE
            var lowestIndex = 0
             for (i in 0 until (arguments.size)) {
                 var temp:Data = arguments[i].eval(runtime)
                 if (temp is IntData) {
                     temp = FloatData(temp.v.toFloat()) 
                 }
                 if (temp is DoubleData) {
                     temp = FloatData(temp.v.toFloat()) 
                 }
                 if (temp is FloatData) {
                     if (i == 0) {
                         lowest = temp.v
                     }
                     else {
                         if (lowest > temp.v){
                             lowest = temp.v
                             lowestIndex = i
                         }
                     }
                 }
                 else {
                     throw Exception("Cannot compare $temp.")
                 }
             }
             val dataCheck:Data = arguments[lowestIndex].eval(runtime)
             if (dataCheck is IntData){
                 return IntData(dataCheck.v)
             }
             if (dataCheck is DoubleData){
                 return DoubleData(dataCheck.v)
             }
             if (dataCheck is FloatData){
                 return FloatData(dataCheck.v)
             }
            
        }
        else {
            throw Exception("$x is not a valid number.")
        }
        return None
     }
}