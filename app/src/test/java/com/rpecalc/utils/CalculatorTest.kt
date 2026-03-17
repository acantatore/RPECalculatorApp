package com.rpecalc.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {

    @Test
    fun e1rm_calculation_isCorrect() {
        val e1rm = Calculator.calculateE1RM(100.0, 5, 8.0)
        // 100 * (1 + (5 + 2)/30) = 100 * (1 + 7/30) = 100 * 1.2333 = 123.333
        assertEquals(123.333, e1rm, 0.01)
    }

    @Test
    fun targetWeight_calculation_isCorrect() {
        // e1rm = 123.333
        // target reps = 8, target rpe = 8.0 (rir 2) -> 10 effective reps
        // target weight = 123.333 / (1 + 10/30) = 123.333 / 1.333 = 92.5
        val e1rm = 123.33333333333333
        val targetWeight = Calculator.calculateTargetWeight(e1rm, 8, 8.0)
        assertEquals(92.5, targetWeight, 0.01)
        
        val rounded = Calculator.roundToNearestPlate(targetWeight)
        assertEquals(92.5, rounded, 0.01)
    }
}
