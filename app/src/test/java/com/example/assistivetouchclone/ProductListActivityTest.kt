package com.example.assistivetouchclone

import org.junit.Assert.assertNotNull
import org.junit.Test

class ProductListActivityTest {
    @Test
    fun productListActivity_shouldBeAvailable() {
        val activityClass = Class.forName("com.example.assistivetouchclone.ProductListActivity")
        assertNotNull(activityClass)
    }
}
