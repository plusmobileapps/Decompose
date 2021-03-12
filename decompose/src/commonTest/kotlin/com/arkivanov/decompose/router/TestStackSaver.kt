package com.arkivanov.decompose.router

import com.arkivanov.decompose.router.StackSaver.RestoredStack
import com.arkivanov.decompose.router.StackSaver.Stack

internal class TestStackSaver<C>(
    private val copyConfiguration: (C) -> C,
    private val isActiveEntryPreserved: Boolean = true,
    savedState: SavedState<C>? = null
) : StackSaver<C> {

    private val map: MutableMap<String, Stack<C>> = savedState?.map ?: HashMap()
    private val suppliers = HashMap<String, () -> Stack<C>>()

    override fun register(key: String, supplier: () -> Stack<C>) {
        check(key !in suppliers)
        suppliers[key] = supplier
    }

    override fun unregister(key: String) {
        check(key in suppliers)
        suppliers -= key
    }

    override fun restore(key: String): RestoredStack<C>? =
        map.remove(key)?.let { stack ->
            RestoredStack(
                stack = stack,
                isActiveEntryPreserved = isActiveEntryPreserved
            )
        }

    fun save(): SavedState<C> =
        SavedState(suppliers.mapValuesTo(HashMap()) { it.value().copy(copyConfiguration) })

    private companion object {
        private fun <C> Stack<C>.copy(copyConfiguration: (C) -> C): Stack<C> =
            Stack(
                active = active.copy(copyConfiguration),
                backStack = backStack.map { it.copy(copyConfiguration) }
            )

        private fun <C> StackSaver.Entry<C>.copy(copyConfiguration: (C) -> C): StackSaver.Entry<C> =
            StackSaver.Entry(
                configuration = copyConfiguration(configuration),
                savedState = savedState
            )
    }

    class SavedState<C>(
        val map: MutableMap<String, Stack<C>>
    ) {
        fun copy(copyConfiguration: (C) -> C): SavedState<C> =
            SavedState(
                map = map.mapValuesTo(HashMap()) { (_, stack) ->
                    stack.copy(copyConfiguration)
                }
            )
    }
}
