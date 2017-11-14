package ru.spbau.mit

class InterpretationException(override val message: String) : Exception()

class ParsingException : Exception()