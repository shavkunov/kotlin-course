package ru.spbau.mit

/**
 * Class that solves codeforces problem.
 * It tries to replace wild cards(?) to letters to create lexicographically smallest palindrome.
 */
class PatternHandler(private val pattern: CharArray,
                     private val alphabet: Int) {

    private val IMPOSSIBLE_MESSAGE = "IMPOSSIBLE"
    private val usedChars = BooleanArray(alphabet)

    init {
        pattern
                .filter { it != '?' }
                .forEach { usedChars[getIndex(it)] = true }
    }

    /**
     * Returns index of symbol in usedChars array
     */
    private fun getIndex(symbol: Char): Int = symbol - 'a'

    /**
     * Get letter that should be on the wild card sign.
     * It's take the most biggest lexicographically letter,
     * because we are starting to remove wild cards from center of the palindrome.
     */
    private fun getAppropriateLetter(): Char {
        val index = usedChars.lastIndexOf(false)
        val result: Char

        result = if (index == -1) {
            'a'
        } else {
            'a' + index
        }

        return result
    }

    /**
     * l, r -- indices in pattern.
     * If they are both wild cards they will be replaced by most biggest lexicographically letter.
     */
    private fun removeWildCardPairs(l: Int, r: Int) {
        if (pattern[l] == '?' && pattern[r] == '?') {
            val letter = getAppropriateLetter()
            usedChars[getIndex(letter)] = true
            pattern[l] = letter
            pattern[r] = letter
        }
    }

    /**
     * Cleaning rest of wild cards.
     * If there any of chars at indices l, r equals wild card, then it should be replaced by another char.
     */
    private fun cleanWildCards(l: Int, r: Int) {
        if (pattern[l] == '?') {
            pattern[l] = pattern[r]
        }

        if (pattern[r] == '?') {
            pattern[r] = pattern[l]
        }
    }

    /**
     * Solving codeforces task.
     * It returns processed symmetric palindrome if it's possible.
     * Otherwise it returns IMPOSSIBLE_MESSAGE
     */
    fun solve(): String {
        if (alphabet > pattern.size) {
            return IMPOSSIBLE_MESSAGE
        }

        var r = pattern.size / 2
        var l = r - (pattern.size + 1) % 2
        while (r - l + 1 <= pattern.size) {
            // process pattern
            removeWildCardPairs(l, r)
            cleanWildCards(l, r)

            if (pattern[l] != pattern[r]) {
                return IMPOSSIBLE_MESSAGE
            }

            r++
            l--
        }

        if (usedChars.contains(false)) {
            return IMPOSSIBLE_MESSAGE
        }

        return pattern.joinToString(separator = "")
    }
}

/**
 * CLI for task: http://codeforces.com/contest/59/problem/C
 * Successful submission: http://codeforces.com/contest/59/my
 * Input contains of two lines. At the first line there is a number k.
 * On the second line there is pattern for constructing palindrome.
 */
fun main(args: Array<String>) {
    val k = Integer.parseInt(readLine())
    val pattern = readLine()!!
    val result = PatternHandler(pattern.toCharArray(), k).solve()
    print(result)
}