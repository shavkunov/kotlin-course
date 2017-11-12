package ru.spbau.mit

data class InterpretationException(private val error: String) : Exception(error)