package backend

// Used as a template for all other Expr types
abstract class Expr {
    abstract fun eval(runtime:Runtime):Data
}

// Nonetype Expr
class NoneExpr(): Expr() {
    override fun eval(runtime:Runtime) = None
}

// Int Expr
class IntLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = IntData(Integer.parseInt(lexeme))
}

// Double Expr
class DoubleLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = DoubleData(lexeme.toDouble())
}

// Float Expr
class FloatLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime):Data 
    = FloatData(lexeme.toFloat())
}

// Boolean Expr
class BooleanLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime): Data = 
    BooleanData(lexeme.equals("true"))
}

// String Expr
class StringLiteral(val lexeme:String):Expr() {
    override fun eval(runtime:Runtime): Data { 
        return StringData(lexeme.substring(1, lexeme.length-1))
    }
}

// Expr that specifically identifies return statements
class ReturnLiteral(val expr:Expr):Expr() {
    override fun eval(runtime: Runtime):Data {
        val exprVal = expr.eval(runtime)
        return ReturnData(exprVal)
    }
}

// Arithmetic
class Arith(val op:String, val left:Expr, val right:Expr) : Expr() {
    override fun eval(runtime:Runtime):Data {
        var x = left.eval(runtime)   // value of left side of argument
        var y = right.eval(runtime)  // value of right side of argument
        if (x !is StringData && y !is StringData) {
            if (x is FloatData && y is IntData) {
                y = FloatData(y.v.toFloat())
            }
            if (x is FloatData && y is DoubleData) {
                y = FloatData(y.v.toFloat())
            }
            if (x is DoubleData && y is IntData) {
                y = DoubleData(y.v.toDouble())
            }
            if (y is FloatData && x is IntData) {
                x = FloatData(x.v.toFloat())
            }
            if (y is FloatData && x is DoubleData) {
                x = FloatData(x.v.toFloat())
            }
            if (y is DoubleData && x is IntData) {
                x = DoubleData(x.v.toDouble())
            }

            // operators for integers
            if (x is IntData && y is IntData) {
                if (op == "+") return IntData(x.v + y.v)
                if (op == "-") return IntData(x.v - y.v)
                if (op == "*") return IntData(x.v * y.v)
                if (op == "/") return IntData(x.v / y.v)
            }

            // operators for doubles
            if (x is DoubleData && y is DoubleData) {
                if (op == "+") return DoubleData(x.v + y.v)
                if (op == "-") return DoubleData(x.v - y.v)
                if (op == "*") return DoubleData(x.v * y.v)
                if (op == "/") return DoubleData(x.v / y.v)
            }

            // operators for floats
            if (x is FloatData && y is FloatData) {
                if (op == "+") return FloatData(x.v + y.v)
                if (op == "-") return FloatData(x.v - y.v)
                if (op == "*") return FloatData(x.v * y.v)
                if (op == "/") return FloatData(x.v / y.v)
            }
            
        }

        // string repetition
        if (x is StringData && y is IntData) {
            if (op == "*") return StringData(x.v.repeat(y.v))
        }
        if (y is StringData && x is IntData) {
            if (op == "*") return StringData(y.v.repeat(x.v))
        }

        // concatenation
        if (op == "++") {
            return StringData(x.toString().plus(y.toString()))
        }
        return None
    }
}


// assignment (i.e. x = 1)
class Assign(val name: String,val expr: Expr): Expr() {
    override fun eval(runtime:Runtime):Data {
            val v:Data = expr.eval(runtime)
            runtime.symbolTable.put(name, v)
            return None
    }
}

// dereferencing (i.e. returning value of variable)
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

// block statement
class Block(val exprs:List<Expr>):Expr() {
    override fun eval(runtime:Runtime):Data {
        // stores the evaluated expression values
        val evalList:MutableList<Data> = mutableListOf()

        for (expression in exprs) {
            val exprVal = expression.eval(runtime)

            // if the expression evaluates to ReturnData
            // then return the value immediately ...
            if (exprVal is ReturnData) {
                return exprVal
            }

            // ... else, add the value to the list
            evalList.add(exprVal)
        }

        // then return the last evaluated value in the block
        return evalList.last()
    }
}

// comparison operator (i.e. >, <, ==, etc.)
class Cmp(val op:String,val left:Expr,val right:Expr) : Expr() {
    override fun eval(runtime:Runtime): Data {
        var x:Data = left.eval(runtime)   // value of left side of comparison
        var y:Data = right.eval(runtime)  // value of right side of comparison

        // set y value for float & int
        if (x is FloatData && y is IntData) {
            y = FloatData(y.v.toFloat())
        }
        // set y value for float & double
        if (x is FloatData && y is DoubleData) {
            y = FloatData(y.v.toFloat())
        }
        // set y value for double & int
        if (x is DoubleData && y is IntData) {
            y = DoubleData(y.v.toDouble())
        }

        // set x value for float & int
        if (y is FloatData && x is IntData) {
            x = FloatData(x.v.toFloat())
        }
        // set x value for float & double
        if (y is FloatData && x is DoubleData) {
            x = FloatData(x.v.toFloat())
        }
        // set x value for double & int
        if (y is DoubleData && x is IntData) {
            x = DoubleData(x.v.toDouble())
        }

        // comparison operators for int data
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

        // comparison operators for double data
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

        // comparison operators for float data
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
        else {
            throw Exception("Cannot perform comparison")
        }
    }
}

// if-else statement
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

// print statement
class Print(val expr: Expr): Expr() {
    override fun eval(runtime:Runtime): Data {
        val data = expr.eval(runtime)
        println(data)
        return None
    }
}

// loops
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

// increment value
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

// function definition
class FuncDef(val name: String, val parameters: List<String>, val body: Expr) : Expr() {
    override fun eval(runtime:Runtime):Data {
        val funcdata = FuncData(name, parameters, body)
        runtime.symbolTable[name] = funcdata
        return None
    }
}

// calling function with ID
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

// this version of Sum handles manually defined arrays (e.g. 1,2,3,4)
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

// this alternate version of the Sum function handles stored arrays (e.g. int[] x = [1,2,3,4])
class DerefSum(val arrayID:String):Expr() {
    override fun eval(runtime: Runtime):Data {
        val array = Deref(arrayID).eval(runtime)

        if (array is ArrayData) {
            val x:Data = array.contents[0]
            if(x is IntData || x is DoubleData || x is FloatData) {
                var resultInt = 0
                var resultDouble = 0.0
                var resultFloat = 0.0f
                var intcheck = true
                var doublecheck = false
                var floatcheck = false
                array.contents.forEach { arg ->
                    var temp:Data = arg
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
        } else {
            throw Exception("$arrayID is not an array.")
        }
    }
}

// this version of Max handles manually defined arrays (e.g. 1,2,3,4)
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

// this alternate version of the Max function handles stored arrays (e.g. int[] x = [1,2,3,4])
class DerefMax(val arrayID:String):Expr() {
    override fun eval(runtime: Runtime):Data {
        val array = Deref(arrayID).eval(runtime)

        if (array is ArrayData) {
            val x: Data = array.contents[0]
            if (x is IntData || x is DoubleData || x is FloatData) {
                var highest = -Float.MAX_VALUE
                var highestIndex = 0
                for (i in 0 until (array.contents.size)) {
                    var temp: Data = array.contents[i]
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
                    } else {
                        throw Exception("Cannot compare $temp.")
                    }
                }
                val dataCheck: Data = array.contents[highestIndex]
                if (dataCheck is IntData) {
                    return IntData(dataCheck.v)
                }
                if (dataCheck is DoubleData) {
                    return DoubleData(dataCheck.v)
                }
                if (dataCheck is FloatData) {
                    return FloatData(dataCheck.v)
                }
            } else {
                throw Exception("$x is not a valid number.")
            }
            return None
        } else {
            throw Exception("$arrayID is not an array.")
        }
    }
}

// this version of Min handles manually defined arrays (e.g. 1,2,3,4)
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

// this alternate version of the Min function handles stored arrays (e.g. int[] x = [1,2,3,4])
class DerefMin(val arrayID:String):Expr() {
    override fun eval(runtime: Runtime):Data {
        val array = Deref(arrayID).eval(runtime)

        if (array is ArrayData) {
            val x:Data = array.contents[0]
            if(x is IntData || x is DoubleData || x is FloatData) {
                var lowest = Float.MAX_VALUE
                var lowestIndex = 0
                for (i in 0 until (array.contents.size)) {
                    var temp:Data = array.contents[i]
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
                val dataCheck:Data = array.contents[lowestIndex]
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
        } else {
            throw Exception("$arrayID is not an array.")
        }
    }
}

// this version of Len handles manually defined arrays (e.g. 1,2,3,4)
class Len(val arguments: List<Expr>) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val x:Data = arguments[0].eval(runtime)
        var count = 0
        for (i in 0 until (arguments.size)) {
            val temp = arguments[i].eval(runtime)
            if (x is IntData) {
                if (temp is IntData){
                    count++
                }
                else {
                    throw Exception("$temp is not an Int")
                }
            }
            if (x is DoubleData) {
                if (temp is DoubleData){
                    count++
                }
                else {
                    throw Exception("$temp is not a Double")
                }
            }
            if (x is FloatData) {
                if (temp is FloatData){
                    count++
                }
                else {
                    throw Exception("$temp is not a Float")
                }
            }
            if (x is StringData) {
                if (temp is StringData){
                    count++
                }
                else {
                    throw Exception("$temp is not a String")
                }
            }
            if (x is BooleanData) {
                if (temp is BooleanData){
                    count++
                }
                else {
                    throw Exception("$temp is not a Boolean")
                }
            }
        }
        
        return IntData(count)
    }
}

// this alternate version of the Len function handles stored arrays (e.g. int[] x = [1,2,3,4])
class DerefLen(val arrayID:String):Expr() {
    override fun eval(runtime: Runtime): Data {
        val array = Deref(arrayID).eval(runtime)

        if (array is ArrayData) {
            var count:Int = 0
            for (i in 0 until (array.contents.size)) {
                count++
            }
            return IntData(count)
        } else {
            throw Exception("$arrayID is not an array.")
        }
    }
}

// define arrays
class ArrayDef(val type: Data, val name: String, val contents: List<Expr>) : Expr() {
    override fun eval(runtime:Runtime):Data {
        var contentData:MutableList<Data> = mutableListOf()
        contents.forEach{content -> 
            val x = content.eval(runtime)
            if (type is IntData) {
                if (!(x is IntData)) {
                    throw Exception("$x is not an Int")
                }
            }
            if (type is DoubleData) {
                if (!(x is DoubleData)) {
                    throw Exception("$x is not a Double")
                }
            }
            if (type is FloatData) {
                if (!(x is FloatData)) {
                    throw Exception("$x is not a Float")
                }
            }
            if (type is StringData) {
                if (!(x is StringData)) {
                    throw Exception("$x is not a String")
                }
            }
            if (type is BooleanData) {
                if (!(x is BooleanData)) {
                    throw Exception("$x is not a Boolean")
                }
            }
            contentData.add(x)
        }
        val arraydata = ArrayData(type, name, contentData)
        runtime.symbolTable[name] = arraydata
        return None
    }
}

// access the value of an array at a given index
class ArrayIndex(val name: String, val index: Expr) : Expr() {
    override fun eval(runtime:Runtime):Data {
        val x = index.eval(runtime)
        var ind:Int
        if (x is IntData) {
            ind = x.v
        }
        else{
            throw Exception("Index is not an Int")
        }
        val array = runtime.symbolTable[name]
        if (array == null) {
            throw Exception("$name does not exist.")
        }
        if (array is ArrayData) {
            if(array.contents.size <= ind || ind < 0) {
                throw Exception("Index out of bounds")
            }
            return array.contents[ind]
        }
        else {
            throw Exception("$name is not an array.")
        }
    }
}