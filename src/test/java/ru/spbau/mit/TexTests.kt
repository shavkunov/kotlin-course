package ru.spbau.mit

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TexTests {
    @Test
    fun testDocument() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        val rows = listOf(1, 2, 3, 4)
        val doc =
                document {
                    documentClass("beamer")
                    usePackage("babel", "russian" /* varargs */)
                    frame("arg1=arg2", frameTitle = "frametitle") {

                        itemize {
                            for (row in rows) {
                                item { +"$row" }
                            }
                        }

                        // begin{pyglist}[language=kotlin]...\end{pyglist}
                        customTag("pyglist", "language=kotlin") {
                            +   """
                                |val a = 1
                                |val b = 2
                                |println(a + b)
                                """.trimMargin()
                        }
                    }
                }.toOutputStream(printStream)

        assertEquals(String(byteOutputStream.toByteArray()), """
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |\begin{frame}[arg1=arg2]
            |\frametitle{frametitle}
            |\begin{itemize}
            |\item
            |1
            |\item
            |2
            |\item
            |3
            |\item
            |4
            |\end{itemize}
            |\begin{pyglist}[language=kotlin]
            |val a = 1
            |val b = 2
            |println(a + b)
            |\end{pyglist}
            |\end{frame}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testAlign() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        val doc =
                document {
                    documentClass("article")
                    center { +"center" }
                    right { +"right" }
                    left { +"left" }
                }.toOutputStream(printStream)

        assertEquals(String(byteOutputStream.toByteArray()), """
            |\documentclass{article}
            |\begin{document}
            |\begin{center}
            |center
            |\end{center}
            |\begin{flushright}
            |right
            |\end{flushright}
            |\begin{flushleft}
            |left
            |\end{flushleft}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testMath() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        val doc =
                document {
                    documentClass("article")
                    math("A_n = \\sum_i^n i")
                }.toOutputStream(printStream)

        assertEquals(String(byteOutputStream.toByteArray()), """
            |\documentclass{article}
            |\begin{document}
            |${'$'}A_n = \sum_i^n i${'$'}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testEnumerate() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        val doc =
                document {
                    documentClass("article")
                    math("A_n = \\sum_i^n i")
                    center {
                        + "test"

                        enumerate {
                            item { +"1" }
                            item { +"test" }
                        }
                    }
                }.toOutputStream(printStream)

        assertEquals(String(byteOutputStream.toByteArray()), """
            |\documentclass{article}
            |\begin{document}
            |${'$'}A_n = \sum_i^n i${'$'}
            |\begin{center}
            |test
            |\begin{enumerate}
            |\item
            |1
            |\item
            |test
            |\end{enumerate}
            |\end{center}
            |\end{document}
            |""".trimMargin())
    }

    @Test(expected = TexException::class)
    fun testNoDocumentClass() {
        document {
            usePackage("babel", "russian", "english")
            usePackage("amsmath")
        }.toString()
    }

    @Test(expected = Exception::class)
    fun testTwoDocClasses() {
        document {
            documentClass("article", "12pt")
            usePackage("babel", "russian", "english")
            usePackage("amsmath")
            documentClass("article", "14pt")
        }
    }

    @Test
    fun testFrame() {
        val byteOutputStream = ByteArrayOutputStream()
        val printStream = PrintStream(byteOutputStream)

        val doc =
                document {
                    documentClass("beamer")
                    frame(frameTitle = "hello") {
                        + "test text"
                        enumerate {
                            item { +"how make" }
                            item { +"good frames" }
                        }

                        math("ans = EZ + \\sum knowledge")
                    }
                }.toOutputStream(printStream)

        assertEquals(String(byteOutputStream.toByteArray()), """
            |\documentclass{beamer}
            |\begin{document}
            |\begin{frame}
            |\frametitle{hello}
            |test text
            |\begin{enumerate}
            |\item
            |how make
            |\item
            |good frames
            |\end{enumerate}
            |${'$'}ans = EZ + \sum knowledge${'$'}
            |\end{frame}
            |\end{document}
            |""".trimMargin())
    }
}