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
        stackSaver.register(key) { stack.save() }
        retainedInstance.activeEntry = stack.active
        stack.active.lifecycleRegistry.resume()

        lifecycle.doOnDestroy(::destroy)
    }

    private fun RouterStack<C, *>.save(): StackSaver.Stack<C> =
        StackSaver.Stack(
            active = active.let { entry ->
                StackSaver.Entry(
                    configuration = entry.configuration,
                    savedState = entry.stateKeeperDispatcher.save()
                )
            },
            backStack = backStack.map { entry ->
                StackSaver.Entry(
                    configuration = entry.configuration,
                    savedState = entry.savedState
                )
            }
        )

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
        val savedStack = stackSaver.restore(key)
        val activeRetainedEntry = retainedInstance.activeEntry

        var activeInstanceKeeperDispatcher: InstanceKeeperDispatcher? = null
        if (activeRetainedEntry != null) {
            if (savedStack?.isActiveEntryPreserved == true) {
                activeInstanceKeeperDispatcher = activeRetainedEntry.instanceKeeperDispatcher
            } else {
                retainedInstance.destroyActiveEntry()
            }
        }

        return savedStack?.stack?.restore(activeInstanceKeeperDispatcher)
    }

    private fun StackSaver.Stack<C>.restore(activeInstanceKeeperDispatcher: InstanceKeeperDispatcher?): RouterStack<C, T> =
        RouterStack(
            active = active.let { entry ->
                routerEntryFactory(
                    configuration = entry.configuration,
                    savedState = entry.savedState,
                    instanceKeeperDispatcher = activeInstanceKeeperDispatcher
                )
            },
            backStack = backStack.map { entry ->
                RouterEntry.Destroyed(
                    configuration = entry.configuration,
                    savedState = entry.savedState
                )
            }
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
