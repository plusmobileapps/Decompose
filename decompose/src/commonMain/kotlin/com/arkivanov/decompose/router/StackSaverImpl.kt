package com.arkivanov.decompose.router

import com.arkivanov.decompose.router.StackSaver.Entry
import com.arkivanov.decompose.router.StackSaver.RestoredStack
import com.arkivanov.decompose.router.StackSaver.Stack
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.ParcelableContainer
import com.arkivanov.decompose.statekeeper.Parcelize
import com.arkivanov.decompose.statekeeper.StateKeeper
import com.arkivanov.decompose.statekeeper.consume
import com.arkivanov.decompose.statekeeper.consumeRequired
import kotlin.reflect.KClass

internal class StackSaverImpl<C : Parcelable>(
    private val configurationClass: KClass<out C>,
    private val stateKeeper: StateKeeper,
    private val onRestoreStack: (List<C>) -> List<C>?,
    private val parcelableContainerFactory: (Parcelable?) -> ParcelableContainer
) : StackSaver<C> {

    override fun register(key: String, supplier: () -> Stack<C>) {
        stateKeeper.register(key) { save(supplier()) }
    }

    private fun save(stack: Stack<C>): SavedState =
        SavedState(
            stack = stack.backStack.map { SavedState.Entry(parcelableContainerFactory(it.configuration), it.savedState) } +
                stack.active.let { SavedState.Entry(parcelableContainerFactory(it.configuration), it.savedState) }
        )

    override fun unregister(key: String) {
        stateKeeper.unregister(key)
    }

    override fun restore(key: String): RestoredStack<C>? {
        val savedState = stateKeeper.consume<SavedState>(key) ?: return null
        val savedStateList = savedState.stack.map { it.configuration.consumeRequired(configurationClass) to it.state }

        val restoredConfigurations = onRestoreStack(savedStateList.map { it.first }) ?: return null
        check(restoredConfigurations.isNotEmpty()) { "Configuration stack can not be empty" }

        val restoredStateList =
            restoredConfigurations.map { configuration ->
                configuration to savedStateList.find { it.first === configuration }?.second
            }

        val activePair = restoredStateList.last()
        val backStackPairs = restoredStateList.dropLast(1)

        val activeEntry = Entry(configuration = activePair.first, savedState = activePair.second)
        val backStackEntries = backStackPairs.map { Entry(configuration = it.first, savedState = it.second) }

        return RestoredStack(
            stack = Stack(active = activeEntry, backStack = backStackEntries),
            isActiveEntryPreserved = activePair.first === savedStateList.last().first
        )
    }

    @Parcelize
    private class SavedState(
        val stack: List<Entry>
    ) : Parcelable {
        @Parcelize
        class Entry(
            val configuration: ParcelableContainer,
            val state: ParcelableContainer?
        ) : Parcelable
    }
}
