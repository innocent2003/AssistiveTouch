package com.example.assistivetouchclone

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SystemActionSelectionStoreTest {

    @Test
    fun `selection store keeps nine slots and syncs by index`() {
        SystemActionSelectionStore.clearAll()

        val action = SystemActionCatalog.getSystemActions().first()
        SystemActionSelectionStore.set(0, action)

        assertEquals(action.label, SystemActionSelectionStore.get(0)?.label)
        assertNull(SystemActionSelectionStore.get(1))

        SystemActionSelectionStore.clear(0)
        assertNull(SystemActionSelectionStore.get(0))
    }
}
