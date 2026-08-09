package com.example.assistivetouchclone

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemActionCatalogTest {

    @Test
    fun `catalog exposes the expected system actions`() {
        val actions = SystemActionCatalog.getSystemActions()

        assertEquals(7, actions.size)
        assertEquals("Open Wifi", actions[0].label)
        assertEquals("Toggle Flash", actions.last().label)
    }
}
