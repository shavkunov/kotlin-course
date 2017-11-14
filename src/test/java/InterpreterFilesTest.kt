import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import ru.spbau.mit.InterpretFile
import ru.spbau.mit.ParsingException
import ru.spbau.mit.ast.Ast
import ru.spbau.mit.ast.AstVisitor
import ru.spbau.mit.interpreter.Interpreter
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class FilesTest {
    @Test
    fun testFile1() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)
        val result = InterpretFile(printStream).interpret("src/test/resources/test1")

        assertEquals(null, result.value)
        assertEquals(false, result.shouldReturn)
        val output = String(byteOutputStream.toByteArray())
        assertEquals("0\n", output)
    }

    @Test
    fun testFile2() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)
        val result = InterpretFile(printStream).interpret("src/test/resources/test2")

        assertEquals(null, result.value)
        assertEquals(false, result.shouldReturn)
        val output = String(byteOutputStream.toByteArray())
        val expected = "0 1\n" +
                       "1 1\n" +
                       "2 2\n" +
                       "3 3\n" +
                       "4 5\n" +
                       "5 8\n"
        assertEquals(expected, output)
    }

    @Test
    fun testFile3() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)
        val result = InterpretFile(printStream).interpret("src/test/resources/test3")

        assertEquals(null, result.value)
        assertEquals(false, result.shouldReturn)
        val output = String(byteOutputStream.toByteArray())
        assertEquals("42\n", output)
    }

    @Test(expected = ParsingException::class)
    fun testIncorrectFile1() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        InterpretFile(printStream).interpret("src/test/resources/incorrectFile1")
    }
}