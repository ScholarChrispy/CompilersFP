package backend

abstract class Data

object None:Data() {
    override fun toString() = "None"
}

class IntData(val v:Int) : Data() {
    override fun toString() = "$v"
}

class DoubleData(val v:Double) : Data() {
    override fun toString() = "$v"
}

class FloatData(val v:Float) : Data() {
    override fun toString() = "$v"
}

class StringData(val v:String): Data() {
    override fun toString() = "$v"
}

class BooleanData(val v:Boolean): Data() {
    override fun toString() = "$v"
}

class FuncData(val name: String, val parameters: List<String>, val body: Expr) : Data() {
    override fun toString()
    = parameters.joinToString(", ").let {
        "$name($it)"
    }
}

class ArrayData(val type: Data, val name: String, val contents: List<Expr>) : Data() {
    override fun toString()
    = contents.joinToString(", ").let {
        "$name($it)"
    }
}

class ReturnData(val v:Data):Data() {
    override fun toString() = "$v"
}