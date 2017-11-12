package ru.spbau.mit.interpreter

import ru.spbau.mit.InterpretationException
import ru.spbau.mit.ast.Ast
import ru.spbau.mit.ast.Visitor
import ru.spbau.mit.ast.nodes.*
import ru.spbau.mit.ast.nodes.Function
import java.io.PrintStream

class Interpreter(private val printStream: PrintStream,
                  private var context: Context = Context()): Visitor<Interpreter.InterpreterResult> {
    fun interpret(tree: Ast): InterpreterResult {
        return visit(tree.root)
    }

    override fun visitFile(file: File): InterpreterResult {
        return visitBlock(file.block)
    }

    override fun visitBlock(block: Block): InterpreterResult {
        val outerContext = context
        context = Context(context)

        for (statement in block.statements) {
            val interpretedStatement = visit(statement)
            if (interpretedStatement.shouldReturn) {
                context = outerContext
                return interpretedStatement
            }
        }
        context = outerContext

        return UNIT_RESULT
    }

    override fun visitFunction(function: Function): InterpreterResult {
        context.addFunction(function)
        return UNIT_RESULT
    }

    override fun visitVariable(variable: Variable): InterpreterResult {
        context.addVariable(variable.name.name)
        return UNIT_RESULT
    }

    override fun visitWhileCycle(whileCycle: WhileCycle): InterpreterResult {
        while (visit(whileCycle.condition).value!! != 0) {
            val body = visit(whileCycle.body)
            if (body.shouldReturn) {
                return body
            }
        }

        return UNIT_RESULT
    }

    override fun visitIfStatement(ifStatement: IfStatement): InterpreterResult {
        if (visit(ifStatement.condition).value!! != 0) {
            visit(ifStatement.body)
        }

        if (ifStatement.elseBody != null) {
            visit(ifStatement.elseBody)
        }

        return UNIT_RESULT
    }

    override fun visitAssignment(assignment: Assignment): InterpreterResult {
        context.setVariableValue(assignment.identifier.name,
                                 visit(assignment.expression).value!!)
        return UNIT_RESULT
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement): InterpreterResult {
        return InterpreterResult(visit(returnStatement.expression).value, true)
    }

    override fun visitFunctionCall(functionCall: FunctionCall): InterpreterResult {
        val function = context.getFunction(functionCall.funName.name)
        val args = functionCall.arguments.map { visit(it).value!! }
        if (function != null) {
            val outerContext = context
            context = Context(context)

            val paramIterator = function.parameters.listIterator()
            for (arg in args) {
                val param = paramIterator.next()
                context.addVariable(param.name, arg)
            }
            val interpretedBody = visit(function.body)
            context = outerContext
            return InterpreterResult(
                    if (interpretedBody.shouldReturn) interpretedBody.value!! else DEFAULT_RESULT,
                    false
            )
        } else {
            if (functionCall.funName == PRINTLN) {
                val argsString = args.joinToString(" ") { it.toString() }
                printStream.println(argsString)
                return InterpreterResult(DEFAULT_RESULT, false)
            }

            throw InterpretationException("")
        }
    }

    override fun visitBinaryExpression(binaryExpression: BinaryExpression): InterpreterResult {
        val leftValue = visit(binaryExpression.leftOp).value!!
        val rightValue = visit(binaryExpression.rightOp).value!!
        val operation = binaryExpression.operator
        val resultValue = when (operation) {
            "+" -> leftValue + rightValue
            "-" -> leftValue - rightValue
            "*" -> leftValue * rightValue
            "/" -> leftValue / rightValue
            "%" -> leftValue % rightValue
            ">" -> if (leftValue > rightValue) 1 else 0
            "<" -> if (leftValue  < rightValue) 1 else 0
            ">=" -> if (leftValue  >= rightValue) 1 else 0
            "<=" -> if (leftValue  <= rightValue) 1 else 0
            "==" -> if (leftValue  == rightValue) 1 else 0
            "!=" -> if (leftValue  != rightValue) 1 else 0
            "||" -> if (rightValue != 0) 1 else 0
            "&&" -> if (rightValue == 0) 0 else 1
            else -> throw InterpretationException("")
        }

        return InterpreterResult(resultValue, false)
    }

    override fun visitIdentifier(identifier: Identifier): InterpreterResult {
        return InterpreterResult(context.getVariableValue(identifier.name), false)
    }

    override fun visitLiteral(literal: Literal): InterpreterResult {
        try {
            val intValue = literal.literal.toInt()
            return InterpreterResult(intValue, false)
        } catch (e: NumberFormatException) {
            throw InterpretationException("Number " + literal.literal + " is too large.")
        }
    }

    data class InterpreterResult(val value: Int?, val shouldReturn: Boolean)

    companion object {
        val UNIT_RESULT = InterpreterResult(null, false)

        private val DEFAULT_RESULT = 0
        private val PRINTLN = Identifier("println")

        private fun boolToInt(bool: Boolean): Int {
            return if (bool) 1 else 0
        }

        private fun intToBool(int: Int): Boolean {
            return int != 0
        }
    }
}