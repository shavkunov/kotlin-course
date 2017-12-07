package ru.spbau.mit.ast

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import ru.spbau.mit.ParsingException
import ru.spbau.mit.ast.nodes.*
import ru.spbau.mit.ast.nodes.Function
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

data class Ast(val root: AstNode)

class AstVisitor : FunBaseVisitor<AstNode>() {
    fun createAst(context: ParserRuleContext): Ast {
        val rootNode = visit(context)

        return Ast(rootNode)
    }

    override fun visitFile(context: FunParser.FileContext): AstNode {
        return File(visitBlock(context.block()) as Block)
    }

    override fun visitBlock(context: FunParser.BlockContext): AstNode {
        val statements = context.statement().map { visit(it) as Statement }

        return Block(statements.toList())
    }

    override fun visitBlockWithBraces(context: FunParser.BlockWithBracesContext): AstNode {
        return visit(context.block())
    }

    override fun visitStatement(context: FunParser.StatementContext): AstNode {

        return visitChildren(context) as? Statement ?: throw ParsingException()
    }

    override fun visitFunction(context: FunParser.FunctionContext): AstNode {
        val funName = Identifier(context.IDENTIFIER().text)

        val paramsList = context.parameterNames()
                                .IDENTIFIER()?.map { Identifier(it.text) }
                                .orEmpty()

        val body = visit(context.blockWithBraces()) as Block

        return Function(funName, paramsList, body)
    }

    override fun visitVariable(context: FunParser.VariableContext): AstNode {
        val varName = Identifier(context.IDENTIFIER().text)
        val expression = context.expression()
        val visitedExpression = expression?.let { visit(expression) as Expression }

        return Variable(varName, visitedExpression)
    }

    override fun visitWhileCycle(context: FunParser.WhileCycleContext): AstNode {
        val condition = visit(context.expression()) as Expression
        val body = visit(context.blockWithBraces()) as Block

        return WhileCycle(condition, body)
    }

    override fun visitIfStatement(context: FunParser.IfStatementContext): AstNode {
        val condition = visit(context.expression()) as Expression
        val ifBlocks = context.blockWithBraces().map { visit(it) }
        val body = ifBlocks[0] as Block
        val elseBody = ifBlocks.getOrNull(1) as Block

        return IfStatement(condition, body, elseBody)
    }

    override fun visitAssignment(context: FunParser.AssignmentContext): AstNode {
        val identifier = Identifier(context.IDENTIFIER().text)
        val expression = visit(context.expression()) as Expression

        return Assignment(identifier, expression)
    }

    override fun visitReturnStatement(context: FunParser.ReturnStatementContext): AstNode {
        val expression = visit(context.expression()) as Expression

        return ReturnStatement(expression)
    }

    override fun visitFunctionCall(context: FunParser.FunctionCallContext): AstNode {
        val identifier = Identifier(context.IDENTIFIER().text)
        val arguments = context.arguments()
                               .expression()?.map { visit(it) as Expression }
                               .orEmpty()

        return FunctionCall(identifier, arguments)
    }

    override fun visitInnerExpression(context: FunParser.InnerExpressionContext): AstNode {
        return visit(context.expression())
    }

    override fun visitIdentifierExpression(context: FunParser.IdentifierExpressionContext): AstNode {
        return Identifier(context.IDENTIFIER().text)
    }

    override fun visitLiteralExpression(context: FunParser.LiteralExpressionContext): AstNode {
        return Identifier(context.LITERAL().text)
    }

    override fun visitBinaryExpression(context: FunParser.BinaryExpressionContext): AstNode {
        val leftOp = context.leftOp
        val rightOp = context.rightOp
        val operation = context.operation

        val visitedLeft = visit(leftOp) as Expression
        val visitedRight = visit(rightOp) as Expression
        return BinaryExpression(visitedLeft, visitedRight, operation.text)
    }
}