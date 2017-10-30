package ru.spbau.mit

import junit.framework.Assert.assertEquals
import org.junit.Test

class Test {
    @Test
    fun test1() {
        val k = 3
        val pattern = "a?c"
        val result = PatternHandler(pattern.toCharArray(), k).solve()
        val expected = "IMPOSSIBLE"

        assertEquals(expected, result)
    }

    @Test
    fun test2() {
        val k = 2
        val pattern = "a??a"
        val result = PatternHandler(pattern.toCharArray(), k).solve()
        val expected = "abba"

        assertEquals(expected, result)
    }

    @Test
    fun test3() {
        val k = 2
        val pattern = "?b?a"
        val result = PatternHandler(pattern.toCharArray(), k).solve()
        val expected = "abba"

        assertEquals(expected, result)
    }
}