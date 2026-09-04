package com.example.unit_coverter.core.math

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

/**
 * Recursive-descent evaluator for scientific-calculator expressions over [Double].
 *
 * Unlike [ExpressionEvaluator] (BigDecimal, used for unit-conversion formulas), this
 * supports transcendental functions, constants, factorial and percent, and respects an
 * angle mode for trigonometry — at the cost of floating-point precision.
 *
 * Grammar:
 *   expr     → term (('+' | '-') term)*
 *   term     → unary (('*' | '/') unary)*
 *   unary    → '-' unary | power
 *   power    → postfix ('^' unary)?          // right-associative
 *   postfix  → primary ('!' | '%')*
 *   primary  → NUMBER | CONSTANT | FUNC '(' expr ')' | '(' expr ')'
 */
object ScientificEvaluator {

    enum class AngleMode { RADIAN, DEGREE }

    /**
     * Evaluates [input]. Accepts pretty operators (×, ÷, −), the π and √ symbols, and
     * is case-insensitive for function/constant names.
     *
     * @throws IllegalStateException on malformed input or a non-finite result.
     */
    fun evaluate(input: String, angleMode: AngleMode = AngleMode.RADIAN): Double {
        val src = input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-") // U+2212 minus sign
            .replace("π", "pi") // normalize π symbol → pi constant
            .replace("√", "sqrt") // normalize √ symbol → sqrt function
            .lowercase()
            .filterNot { it.isWhitespace() }

        if (src.isEmpty()) error("Empty expression")

        val parser = Parser(src, angleMode)
        val result = parser.parseExpr()
        if (parser.pos < src.length) {
            error("Unexpected character at ${parser.pos}: '${src[parser.pos]}'")
        }
        if (!result.isFinite()) error("Result is undefined")
        return result
    }

    private class Parser(private val src: String, private val angleMode: AngleMode) {
        var pos = 0

        fun parseExpr(): Double {
            var result = parseTerm()
            while (true) {
                when (peek()) {
                    '+' -> { pos++; result += parseTerm() }
                    '-' -> { pos++; result -= parseTerm() }
                    else -> return result
                }
            }
        }

        fun parseTerm(): Double {
            var result = parseUnary()
            while (true) {
                when (peek()) {
                    '*' -> { pos++; result *= parseUnary() }
                    '/' -> {
                        pos++
                        val divisor = parseUnary()
                        if (divisor == 0.0) error("Division by zero")
                        result /= divisor
                    }
                    else -> return result
                }
            }
        }

        fun parseUnary(): Double =
            if (peek() == '-') { pos++; -parseUnary() } else parsePower()

        fun parsePower(): Double {
            val base = parsePostfix()
            return if (peek() == '^') { pos++; base.pow(parseUnary()) } else base
        }

        fun parsePostfix(): Double {
            var value = parsePrimary()
            while (true) {
                when (peek()) {
                    '!' -> { pos++; value = factorial(value) }
                    '%' -> { pos++; value /= 100.0 }
                    else -> return value
                }
            }
        }

        fun parsePrimary(): Double {
            val c = peek()
            if (c == '(') {
                pos++
                val result = parseExpr()
                if (peek() != ')') error("Expected ')'")
                pos++
                return result
            }
            if (c.isLetter()) return parseIdentifier()
            return parseNumber()
        }

        private fun parseIdentifier(): Double {
            val start = pos
            while (pos < src.length && src[pos].isLetter()) pos++
            // Function names may end in digits (log2). Consume trailing digits only
            // when doing so is followed by '(' — otherwise "pi2" style juxtaposition
            // would be swallowed into the name and reported as an unknown function.
            val letterEnd = pos
            var digitEnd = pos
            while (digitEnd < src.length && src[digitEnd].isDigit()) digitEnd++
            if (digitEnd > letterEnd && digitEnd < src.length && src[digitEnd] == '(') {
                pos = digitEnd
            }
            val name = src.substring(start, pos)

            // Constants (no parentheses).
            when (name) {
                "pi" -> return Math.PI
                "e" -> return Math.E
            }

            // Functions require a parenthesised argument.
            if (peek() != '(') error("Unknown constant or missing '(' after '$name'")
            pos++
            val arg = parseExpr()
            if (peek() != ')') error("Expected ')' after '$name('")
            pos++
            return applyFunction(name, arg)
        }

        private val isDegree get() = angleMode == AngleMode.DEGREE

        private fun applyFunction(name: String, x: Double): Double = when (name) {
            "sin" -> if (isDegree) degSin(x) else sin(x)
            "cos" -> if (isDegree) degCos(x) else cos(x)
            "tan" -> if (isDegree) degTan(x) else tan(x)
            "asin" -> fromRadians(asin(x))
            "acos" -> fromRadians(acos(x))
            "atan" -> fromRadians(atan(x))
            "sinh" -> sinh(x)
            "cosh" -> cosh(x)
            "tanh" -> tanh(x)
            "ln" -> ln(x)
            "log" -> log10(x)
            "log2" -> log2(x)
            "sqrt" -> sqrt(x)
            "cbrt" -> cbrt(x)
            "exp" -> exp(x)
            "abs" -> abs(x)
            else -> error("Unknown function '$name'")
        }

        /** Inverse trig returns radians; convert to degrees when in degree mode. */
        private fun fromRadians(x: Double): Double =
            if (isDegree) Math.toDegrees(x) else x

        // ── Degree-mode trig ──────────────────────────────────────────────────
        // Quadrantal angles (multiples of 90°) return exact values instead of
        // floating-point noise — e.g. cos(90°) is exactly 0.0, not 6.12E-17 —
        // because 90° → π/2 rad is not exactly representable in a Double.

        private fun normalizeDegrees(deg: Double): Double {
            val d = deg % 360.0
            return if (d < 0) d + 360.0 else d
        }

        private fun degSin(deg: Double): Double = when (normalizeDegrees(deg)) {
            0.0, 180.0 -> 0.0
            90.0 -> 1.0
            270.0 -> -1.0
            else -> sin(Math.toRadians(deg))
        }

        private fun degCos(deg: Double): Double = when (normalizeDegrees(deg)) {
            0.0 -> 1.0
            90.0, 270.0 -> 0.0
            180.0 -> -1.0
            else -> cos(Math.toRadians(deg))
        }

        private fun degTan(deg: Double): Double = when (normalizeDegrees(deg)) {
            0.0, 180.0 -> 0.0
            90.0, 270.0 -> error("tan is undefined at ${deg}°")
            else -> tan(Math.toRadians(deg))
        }

        private fun parseNumber(): Double {
            val start = pos
            var seenDot = false
            while (pos < src.length && (src[pos].isDigit() || src[pos] == '.')) {
                if (src[pos] == '.') {
                    if (seenDot) error("Malformed number at $pos")
                    seenDot = true
                }
                pos++
            }
            val token = src.substring(start, pos)
            if (token.isEmpty() || token == ".") error("Expected number at $start")
            return token.toDoubleOrNull() ?: error("Invalid number '$token'")
        }

        private fun factorial(value: Double): Double {
            val n = value.toLong()
            if (n.toDouble() != value || n < 0) error("Factorial needs a non-negative integer")
            if (n > 170) error("Factorial too large")
            var acc = 1.0
            for (i in 2..n) acc *= i
            return acc
        }

        private fun peek(): Char = if (pos < src.length) src[pos] else ' '
    }
}
