package com.arkivanov.decompose.router

import com.arkivanov.decompose.instancekeeper.InstanceKeeper
import com.arkivanov.decompose.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.decompose.instancekeeper.getOrCreate
import com.arkivanov.decompose.lifecycle.Lifecycle
import com.arkivanov.decompose.lifecycle.destroy
import com.arkivanov.decompose.lifecycle.doOnDestroy
import com.arkivanov.decompose.lifecycle.resume
import kotlin.properties.Delegates.observable

internal class StackHolderImpl<C, T>(
    private val initialConfiguration: () -> C,
    private val initialBackStack: () -> List<C>,
    lifecycle: Lifecycle,
    private val key: String,
    private val stackSaver: StackSaver<C>,
    instanceKeeper: InstanceKeeper,
    private val routerEntryFactory: RouterEntryFactory<C, T>
) : StackHolder<C, T> {

    private val retainedInstance: RetainedInstance<C, T> = instanceKeeper.getOrCreate(key, ::RetainedInstance)

    override var stack: RouterStack<C, T>
        by observable(restoreStack() ?: initialStack()) { _, _, newValue ->
            retainedInstance.activeEntry = newValue.active
        }

    init {
        stackSaver.register(key) { stack }
        retainedInstance.activeEntry = stack.active
        stack.active.lifecycleRegistry.resume()

        lifecycle.doOnDestroy(::destroy)
    }

    private fun destroy() {
        stackSaver.unregister(key)

        val stack = stack
        stack.active.lifecycleRegistry.destroy()
        stack.backStack.destroy()
    }

    private fun initialStack(): RouterStack<C, T> =
        RouterStack(
            active = routerEntryFactory(initialConfiguration()),
            backStack = initialBackStack().map { RouterEntry.Destroyed(configuration = it) }
        )

    private fun restoreStack(): RouterStack<C, T>? {
        val savedStack: StackSaver.RestoredStack<C>? = stackSaver.restore(key)
        val activeRetainedEntry: RouterEntry.Created<C, T>? = retainedInstance.activeEntry

        var activeInstanceKeeperDispatcher: InstanceKeeperDispatcher? = null
        if (activeRetainedEntry != null) {
            if (savedStack != null) {
                activeInstanceKeeperDispatcher = activeRetainedEntry.instanceKeeperDispatcher
            } else {
                retainedInstance.destroyActiveEntry()
            }
        }

        return savedStack?.restore(activeInstanceKeeperDispatcher)
    }

    private fun StackSaver.RestoredStack<C>.restore(activeInstanceKeeperDispatcher: InstanceKeeperDispatcher?): RouterStack<C, T> =
        RouterStack(
            active = routerEntryFactory(
                configuration = active.configuration,
                savedState = active.savedState,
                instanceKeeperDispatcher = activeInstanceKeeperDispatcher
            ),
            backStack = backStack
        )

    private class RetainedInstance<C, T> : InstanceKeeper.Instance {
        var activeEntry: RouterEntry.Created<C, T>? = null

        override fun onDestroy() {
            destroyActiveEntry()
        }

        fun destroyActiveEntry() {
            activeEntry?.instanceKeeperDispatcher?.destroy()
            activeEntry = null
        }
    }
}
