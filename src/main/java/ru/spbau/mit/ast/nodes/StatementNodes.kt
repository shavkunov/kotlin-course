package ru.spbau.mit.ast.nodes

import ru.spbau.mit.ast.Visitor

interface Statement : AstNode

data class Function(
        val name: Identifier,
        val parameters: List<Identifier>,
        val body: Block
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitFunction(this)
    }
}

data class Variable(
        val name: Identifier,
        val expression: Expression?
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitVariable(this)
    }
}

data class WhileCycle(
        val condition: Expression,
        val body: Block
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitWhileCycle(this)
    }
}

data class IfStatement(
        val condition: Expression,
        val body: Block,
        val elseBody: Block?
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitIfStatement(this)
    }
}

data class Assignment(
        val identifier: Identifier,
        val expression: Expression
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitAssignment(this)
    }
}

data class ReturnStatement(
        val expression: Expression
) : Statement {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitReturnStatement(this)
    }
}