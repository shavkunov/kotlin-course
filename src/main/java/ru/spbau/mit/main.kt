package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.AstVisitor
import ru.spbau.mit.interpreter.Interpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

fun main(args: Array<String>) {
    val funLexer = FunLexer(CharStreams.fromFileName(args[0]))
    val tokens = CommonTokenStream(funLexer)
    val funParser = FunParser(tokens)
    val fileContext = funParser.file()

    val tree = AstVisitor().createAst(fileContext)
    Interpreter(System.out).interpret(tree)
}