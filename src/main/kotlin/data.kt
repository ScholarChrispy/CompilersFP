package backend

abstract class Data

// nonetype data
object None:Data() {
    override fun toString() = "None"
}

// integer data
class IntData(val v:Int) : Data() {
    override fun toString() = "$v"
}

// double data
class DoubleData(val v:Double) : Data() {
    override fun toString() = "$v"
}

// float data
class FloatData(val v:Float) : Data() {
    override fun toString() = "$v"
}

// string data
class StringData(val v:String): Data() {
    override fun toString() = "$v"
}

// boolean data
class BooleanData(val v:Boolean): Data() {
    override fun toString() = "$v"
}

// stores function data
class FuncData(val name: String, val parameters: List<String>, val body: Expr) : Data() {
    override fun toString()
    = parameters.joinToString(", ").let {
        "$name($it)"
    }
}

// stores array data
class ArrayData(val type: Data, val name: String, val contents: List<Data>) : Data() {
    override fun toString() 
        = contents.joinToString(", ").let {
        "[$it]"
    }
}

// a special return data type that signifies a return statement
class ReturnData(val v:Data):Data() {
    override fun toString() = "$v"
}