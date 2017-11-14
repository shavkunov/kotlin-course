import org.junit.Test
import ru.spbau.mit.InterpretFile
import ru.spbau.mit.ast.Ast
import ru.spbau.mit.ast.nodes.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class ParserTest {
    val ast1 = Ast(File(
        Block(listOf(
            Variable(Identifier("a"), Literal("10")),
            Variable(Identifier("b"), Literal("20")),
            IfStatement(
                BinaryExpression(Identifier("a"), Identifier("b"), ">"),
                Block(listOf(
                    FunctionCall(
                        Identifier("println"),
                        listOf(Literal("1"))
                    ))
                ),
                Block(listOf(
                    FunctionCall(
                        Identifier("println"),
                        listOf(Literal("0")))
                ))
            )
        ))
    ))

    val ast2 = Ast(File(
        Block(listOf(
            Function(
                Identifier("foo"),
                listOf(Identifier("n")),
                Block(listOf(
                    Function(
                        Identifier("bar"),
                        listOf(Identifier("m")),
                        Block(listOf(
                            ReturnStatement(BinaryExpression(Identifier("m"), Identifier("n"), "+"))))
                    ),
                ReturnStatement(FunctionCall(Identifier("bar"), listOf(Literal("1"))))
                ))
            ),

            FunctionCall(
                Identifier("println"),
                listOf(
                    FunctionCall(Identifier("foo"), listOf(Literal("41"))))
            )
        ))
    ))

    @Test
    fun testAst1() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)
        val ast = InterpretFile(printStream).createAst("src/test/resources/test1")

        assertEquals(ast1, ast)
    }

    @Test
    fun testAst2() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)
        val ast = InterpretFile(printStream).createAst("src/test/resources/test3")

        assertEquals(ast2, ast)
    }
}