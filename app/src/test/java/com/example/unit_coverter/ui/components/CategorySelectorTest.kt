package com.example.unit_coverter.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class CategorySelectorTest {

    @Test
    fun shortLabelsFitTheRail() {
        assertEquals("Mass", shortCategoryLabel("Mass / Weight"))
        assertEquals("Fuel", shortCategoryLabel("Fuel Consumption"))
        assertEquals("Data", shortCategoryLabel("Data Storage"))
        assertEquals("Length", shortCategoryLabel("Length"))
        assertEquals("Temp", shortCategoryLabel("Temperature"))
    }
}
