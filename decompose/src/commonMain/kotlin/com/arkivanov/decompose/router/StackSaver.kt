package com.arkivanov.decompose.router

import com.arkivanov.decompose.statekeeper.ParcelableContainer

internal interface StackSaver<C> {

    fun register(key: String, supplier: () -> Stack<C>)

    fun unregister(key: String)

    fun restore(key: String): RestoredStack<C>?

    class RestoredStack<C>(
        val stack: Stack<C>,
        val isActiveEntryPreserved: Boolean
    )

    class Stack<C>(
        val active: Entry<C>,
        val backStack: List<Entry<C>>
    )

    class Entry<C>(
        val configuration: C,
        val savedState: ParcelableContainer?
    )
}
