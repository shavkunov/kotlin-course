package ru.spbau.mit.ast

import ru.spbau.mit.ast.nodes.*
import ru.spbau.mit.ast.nodes.Function

interface Visitor<out T> {
    fun visit(node: AstNode): T {
        return node.accept(this)
    }

    fun visitFile(file: File): T

    fun visitBlock(block: Block): T

    fun visitFunction(function: Function): T

    fun visitVariable(variable: Variable): T

    fun visitWhileCycle(whileCycle: WhileCycle): T

    fun visitIfStatement(ifStatement: IfStatement): T

    fun visitAssignment(assignment: Assignment): T

    fun visitReturnStatement(returnStatement: ReturnStatement): T

    fun visitFunctionCall(functionCall: FunctionCall): T

    fun visitBinaryExpression(binaryExpression: BinaryExpression): T

    fun visitIdentifier(identifier: Identifier): T

    fun visitLiteral(literal: Literal): T
}
