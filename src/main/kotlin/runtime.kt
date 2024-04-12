package backend

class Runtime() {
    val symbolTable:MutableMap<String, Data> = mutableMapOf()

    fun subscope(bindings:Map<String, Data>):Runtime {
        val parentSymbolTable = this.symbolTable
        return Runtime().apply {
            symbolTable.putAll(parentSymbolTable)
            symbolTable.putAll(bindings)
        }
    }

    override fun toString():String =
        symbolTable.map {
                entry -> "${entry.key} = ${entry.value}"
        }.joinToString("; ")


    fun copy(additionalBindings:Map<String, Data>):Runtime {
        val newRuntime = Runtime()
        newRuntime.symbolTable.putAll(this.symbolTable)
        newRuntime.symbolTable.putAll(additionalBindings)
        return newRuntime
    }
}


