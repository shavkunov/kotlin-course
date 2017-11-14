package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.Ast
import ru.spbau.mit.ast.AstVisitor
import ru.spbau.mit.interpreter.Interpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.PrintStream

class InterpretFile(private val printStream: PrintStream) {
    fun createAst(source: String): Ast {
        val funLexer = FunLexer(CharStreams.fromFileName(source))
        val tokens = CommonTokenStream(funLexer)
        val funParser = FunParser(tokens)
        val fileContext = funParser.file()

        if (funParser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }

        return AstVisitor().createAst(fileContext)
    }

    fun interpret(source: String) : Interpreter.InterpreterResult {
        val tree = createAst(source)
        return Interpreter(printStream).interpret(tree)
    }
}