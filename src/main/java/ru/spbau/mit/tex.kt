package ru.spbau.mit

@DslMarker
annotation class TexMarker

@TexMarker
interface TexElement {
    fun render(builder: StringBuilder)
}

class Text(private val text: String) : TexElement {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n")
    }
}

open class Command(
        protected val name: String,
        protected val arg: String,
        protected val additionalArgs : MutableList<String>
) : TexElement {
    override fun render(builder: StringBuilder) {
        builder.append("\\$name")

        if (additionalArgs.isNotEmpty()) {
            builder.append(additionalArgs.joinToString(",", "[", "]"))
        }

        builder.append("{$arg}\n")
    }

    operator fun Pair<String, String>.unaryPlus() {
        additionalArgs.add("$first=$second")
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
                   private val additionalArgs : MutableList<String>) : TexElement {
    protected val children = mutableListOf<TexElement>()

    override fun render(builder: StringBuilder) {
        builder.append("\\begin{$tagName}")

        if (additionalArgs.isNotEmpty()) {
            builder.append(additionalArgs.joinToString(",", "[", "]"))
        }
        builder.append("\n")

        for (child in children) {
            child.render(builder)
        }
        builder.append("\\end{$tagName}\n")
    }

    protected fun <T : TexElement> initElement(element: T, init: T.() -> Unit) {
        element.init()
        children.add(element)
        //return element
    }

    operator fun String.unaryPlus() {
        children.add(Text(this))
    }

    operator fun Pair<String, String>.unaryPlus() {
        additionalArgs.add("$first=$second")
    }
}


/*
\begin{tagName}[...]
\item ...
\item ...
\end{tagName}
 */
open class ItemizedTag(tagName: String, additionalArgs: MutableList<String>) : Tag(tagName, additionalArgs) {
    fun item(init: Item.() -> Unit) = initElement(Item(), init)
}

class Item : TagWithContent("item", mutableListOf()) {
    override fun render(builder: StringBuilder) {
        builder.append("\\item\n")

        for (child in children) {
            child.render(builder)
        }
    }
}

class Itemize : ItemizedTag("itemize", mutableListOf())

class Enumerate : ItemizedTag("enumerate", mutableListOf())

abstract class TagWithContent(tagName: String, additionalArgs : MutableList<String>) : Tag(tagName, additionalArgs) {
    fun math(formula: String, vararg additionalArgs: String) {
        children.add(Math(formula, additionalArgs.toMutableList()))
    }

    fun itemize(init: Itemize.() -> Unit) = initElement(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initElement(Enumerate(), init)

    fun frame(frameTitle: String, init: Frame.() -> Unit) = initElement(Frame(frameTitle), init)

    fun customTag(name: String, vararg additionalArgs: String, init: CustomTag.() -> Unit) =
            initElement(CustomTag(name, additionalArgs.toMutableList()), init)

    fun left(init: Left.() -> Unit) = initElement(Left(), init)

    fun center(init: Center.() -> Unit) = initElement(Center(), init)

    fun right(init: Right.() -> Unit) = initElement(Right(), init)
}

class Left : TagWithContent("flushleft", mutableListOf())

class Center : TagWithContent("center", mutableListOf())

class Right : TagWithContent("flushright", mutableListOf())

class Frame(frameTitle: String) : TagWithContent("frame", mutableListOf()) {
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

    fun documentClass(
            mainArg: String,
            vararg additionalArgs: String,
            init: DocumentClass.() -> Unit = {}) {
        if (documentClass != null) {
            throw TexException("More than one document class found")
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

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        render(stringBuilder)

        return stringBuilder.toString()
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