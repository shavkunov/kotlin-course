package ru.spbau.mit

import java.io.PrintStream

@DslMarker
annotation class TexMarker

@TexMarker
abstract class TexElement(private val additionalArgs: MutableList<String> = mutableListOf()) {

    abstract fun render(builder: StringBuilder)

    open operator fun String.unaryPlus() {
        additionalArgs.add(this)
    }
}

class Text(private val text: String) : TexElement() {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

open class Command(
        private val name: String,
        protected val arg: String,
        private val additionalArgs : MutableList<String>
) : TexElement(additionalArgs) {
    override fun render(builder: StringBuilder) {
        builder.append("\\$name")

        if (additionalArgs.isNotEmpty()) {
            builder.append(additionalArgs.joinToString(",", "[", "]"))
        }

        builder.append("{$arg}\n")
    }
}

class DocumentClass(mainArg: String, additionalArgs: MutableList<String>)
                : Command("documentclass", mainArg, additionalArgs)

class Math(formula: String, additionalArgs: MutableList<String>) : Command("math", formula, additionalArgs) {
    override fun render(builder: StringBuilder) {
        builder.append("$")
        builder.append(arg)
        builder.append("$\n")
    }
}

class FrameTitle(title: String) : Command("frametitle", title, mutableListOf())

class UsePackage(packageName: String, args: MutableList<String>) : Command("usepackage", packageName, args)


/*
\begin{tagName}[...]
...
\end{tagName}
 */
abstract class Tag(private val tagName: String,
                   private val additionalArgs : MutableList<String>) : TexElement(additionalArgs) {
    protected val children = mutableListOf<TexElement>()

    override fun render(builder: StringBuilder) {
        builder.append("\\begin{$tagName}")

        if (additionalArgs.isNotEmpty()) {
            builder.append(additionalArgs.joinToString(",", "[", "]"))
        }
        builder.append("\n")
        renderChildren(builder)
        builder.append("\\end{$tagName}\n")
    }

    protected fun <T : TexElement> initElement(element: T, init: T.() -> Unit) {
        element.init()
        children.add(element)
    }

    override operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    protected fun renderChildren(builder: StringBuilder) {
        for (child in children) {
            child.render(builder)
        }
    }
}


/*
\begin{tagName}[...]
\item ...
\item ...
\end{tagName}
 */
open class ItemizedTag(tagName: String, additionalArgs: MutableList<String>) : Tag(tagName, additionalArgs) {
    fun item(vararg additionalArgs: String, init: Item.() -> Unit) = initElement(Item(additionalArgs.toMutableList()), init)
}

class Item(additionalArgs: MutableList<String>) : TagWithContent("item", additionalArgs) {
    override fun render(builder: StringBuilder) {
        builder.append("\\item\n")

        renderChildren(builder)
    }
}

class Itemize(additionalArgs: MutableList<String>) : ItemizedTag("itemize", additionalArgs)

class Enumerate(additionalArgs: MutableList<String>) : ItemizedTag("enumerate", additionalArgs)

abstract class TagWithContent(tagName: String, additionalArgs : MutableList<String>) : Tag(tagName, additionalArgs) {
    fun math(formula: String, vararg additionalArgs: String) {
        children.add(Math(formula, additionalArgs.toMutableList()))
    }

    fun itemize(vararg additionalArgs: String, init: Itemize.() -> Unit) =
            initElement(Itemize(additionalArgs.toMutableList()), init)

    fun enumerate(vararg additionalArgs: String, init: Enumerate.() -> Unit) =
            initElement(Enumerate(additionalArgs.toMutableList()), init)

    fun customTag(name: String, vararg additionalArgs: String, init: CustomTag.() -> Unit) =
            initElement(CustomTag(name, additionalArgs.toMutableList()), init)

    fun left(init: Left.() -> Unit) = initElement(Left(), init)

    fun center(init: Center.() -> Unit) = initElement(Center(), init)

    fun right(init: Right.() -> Unit) = initElement(Right(), init)
}

class Left : TagWithContent("flushleft", mutableListOf())

class Center : TagWithContent("center", mutableListOf())

class Right : TagWithContent("flushright", mutableListOf())

class Frame(frameTitle: String, additionalArgs: MutableList<String>) : TagWithContent("frame", additionalArgs) {
    init {
        children.add(FrameTitle(frameTitle))
    }
}

class CustomTag(name: String, additionalArgs: MutableList<String>) : TagWithContent(name, additionalArgs)

class Document : TagWithContent("document", mutableListOf()) {
    private val usedPackages = mutableListOf<UsePackage>()
    var documentClass: DocumentClass? = null

    override fun render(builder: StringBuilder) {
        documentClass!!.render(builder)
        usedPackages.forEach { it.render(builder) }
        super.render(builder)
    }

    fun frame(vararg additionalArgs: String, frameTitle: String, init: Frame.() -> Unit) =
            initElement(Frame(frameTitle, additionalArgs.toMutableList()), init)

    fun documentClass(
            mainArg: String,
            vararg additionalArgs: String,
            init: DocumentClass.() -> Unit = {}) {

        require(documentClass == null) {
            "More than one document class found"
        }

        val documentClass = DocumentClass(mainArg, additionalArgs.toMutableList())
        documentClass.init()
        this.documentClass = documentClass
    }

    fun usePackage(
            mainArg: String,
            vararg additionalArgs: String,
            init: UsePackage.() -> Unit = {}
    ) {
        val usePackage = UsePackage(mainArg, additionalArgs.toMutableList())
        usePackage.init()
        usedPackages.add(usePackage)
    }

    override fun toString(): String = buildString(this::render)

    fun toOutputStream(printStream: PrintStream) {
        printStream.append(toString())
    }
}

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()

    if (document.documentClass == null) {
        throw TexException("No documentclass found")
    }

    return document
}