package ru.spbau.mit

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.spbau.mit.ast.AstVisitor
import ru.spbau.mit.interpreter.Interpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Pass file to interpret")
        return
    }

    try {
        val source = args[0]
        InterpretFile(System.out).interpret(source)

    } catch (error: ParsingException) {
        println("There are some parsing errors. Aborted")
    } catch (error: InterpretationException) {
        println("File cannot be interpreted, reason:")
        println(error.message)
    }
}