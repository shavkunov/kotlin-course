package ru.spbau.mit.interpreter

import ru.spbau.mit.InterpretationException
import ru.spbau.mit.ast.nodes.Function
import java.util.*

class Context(private val parent: Context? = null) {
    private val variables: MutableMap<String, Int> = HashMap()
    private val functions: MutableMap<String, Function> = HashMap()

    fun getVariableValue(name: String): Int? =
        variables.getOrElse(name) { parent?.getVariableValue(name) } ?: throw Exception()


    fun addVariable(name: String, value: Int = 0) {
        if (variables.containsKey(name)) {
            throw InterpretationException("Variable $name is already defined")
        }

        variables.put(name, value)
    }

    fun setVariableValue(name: String, value: Int) {
        if (name !in variables) {
            parent?.setVariableValue(name, value) ?: throw InterpretationException("Variable $name isn't defined")
        } else {
            variables[name] = value
        }
    }

    fun getFunction(name: String): Function? =
            functions[name] ?: parent?.getFunction(name)


    fun addFunction(function: Function) {
        val name = function.functionIdentifier.name
        if (functions.containsKey(name)) {
            throw InterpretationException("Function $name is already defined")
        }

        functions.put(name, function)
    }
}