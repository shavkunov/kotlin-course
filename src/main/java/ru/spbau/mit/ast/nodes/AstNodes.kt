package ru.spbau.mit.ast.nodes

import ru.spbau.mit.ast.Visitor


interface AstNode {
    fun <T> accept(visitor: Visitor<T>): T
}

data class File(val block: Block) : AstNode {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitFile(this)
    }
}

data class Block(val statements: List<Statement>) : AstNode {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitBlock(this)
    }
}