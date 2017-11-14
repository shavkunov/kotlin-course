package ru.spbau.mit.ast.nodes

import ru.spbau.mit.ast.Visitor

interface Expression : Statement

data class FunctionCall(
        val funIdentifier: Identifier,
        val arguments: List<Expression>
) : Expression {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitFunctionCall(this)
    }
}

data class BinaryExpression(
        val leftOp: Expression,
        val rightOp: Expression,
        val operator: String
) : Expression {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitBinaryExpression(this)
    }
}

data class Identifier(
        val name: String
) : Expression {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitIdentifier(this)
    }
}

data class Literal(
        val text: String
) : Expression {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitLiteral(this)
    }
}