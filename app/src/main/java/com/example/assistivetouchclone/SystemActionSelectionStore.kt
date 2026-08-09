package com.example.assistivetouchclone

import java.util.concurrent.ConcurrentHashMap

object SystemActionSelectionStore {
    private val selections = ConcurrentHashMap<Int, SystemActionItem?>()

    fun set(index: Int, action: SystemActionItem?) {
        selections[index] = action
    }

    fun get(index: Int): SystemActionItem? = selections[index]

    fun clear(index: Int) {
        selections.remove(index)
    }

    fun clearAll() {
        selections.clear()
    }

    fun asMap(): Map<Int, SystemActionItem?> = selections.toMap()
}
