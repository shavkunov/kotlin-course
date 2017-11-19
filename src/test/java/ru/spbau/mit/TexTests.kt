package ru.spbau.mit

import junit.framework.Assert.assertEquals
import org.junit.Test

class TexTests {
    @Test
    fun testDocument() {
        val rows = listOf(1, 2, 3, 4)
        val doc =
                document {
                    documentClass("beamer")
                    usePackage("babel", "russian" /* varargs */)
                    frame(frameTitle="frametitle") {
                        +("arg1" to "arg2")

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
                }

        assertEquals(doc.toString(), """
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
        val doc =
                document {
                    documentClass("article")
                    center { +"center" }
                    right { +"right" }
                    left { +"left" }
                }
        assertEquals(doc.toString(), """
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
        val doc =
                document {
                    documentClass("article")
                    math("A_n = \\sum_i^n i")
                }
        assertEquals(doc.toString(), """
            |\documentclass{article}
            |\begin{document}
            |${'$'}A_n = \sum_i^n i${'$'}
            |\end{document}
            |""".trimMargin())
    }

    @Test
    fun testEnumerate() {
        val doc =
                document {
                    documentClass("article")
                    math("A_n = \\sum_i^n i")
                    center {
                        +"test"

                        enumerate {
                            item { +"1" }
                            item { +"test" }
                        }
                    }
                }
        assertEquals(doc.toString(), """
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

    @Test(expected = TexException::class)
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
                }
        assertEquals(doc.toString(), """
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