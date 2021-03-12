package com.arkivanov.decompose.router

import com.arkivanov.decompose.router.StackSaver.Entry
import com.arkivanov.decompose.router.StackSaver.Stack
import com.arkivanov.decompose.router.statekeeper.TestParcelableContainer
import com.arkivanov.decompose.router.statekeeper.TestStateKeeperDispatcher
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.statekeeper.Parcelize
import com.arkivanov.decompose.statekeeper.StateKeeperDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class StackSaverTest {

    @Test
    fun WHEN_StackSaver_recreated_THEN_active_component_restored() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        val originalStack = stack()
        oldSaver.register("key") { originalStack }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState))
        val restoredStack = newSaver.restore("key")

        assertEquals(originalStack.active.configuration, restoredStack?.stack?.active?.configuration)
    }

    @Test
    fun WHEN_StackSaver_recreated_THEN_back_stack_restored() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        val originalStack = stack()
        oldSaver.register("key") { originalStack }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState))
        val restoredStack = newSaver.restore("key")

        assertEquals(originalStack.backStack.map { it.configuration }, restoredStack?.stack?.backStack?.map { it.configuration })
    }

    @Test
    fun WHEN_StackSaver_recreated_THEN_state_restored_for_active_component() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        val originalStack = stack()
        oldSaver.register("key") { originalStack }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState))
        val restoredStack = newSaver.restore("key")

        assertEquals(originalStack.active.savedState, restoredStack?.stack?.active?.savedState)
    }

    @Test
    fun WHEN_StackSaver_recreated_THEN_state_restored_for_back_stack_components() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        val originalStack = stack()
        oldSaver.register("key") { originalStack }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState))
        val restoredStack = newSaver.restore("key")
        val restoredComponentState1 = restoredStack?.stack?.backStack?.getOrNull(0)?.savedState
        val restoredComponentState2 = restoredStack?.stack?.backStack?.getOrNull(1)?.savedState

        assertEquals(originalStack.backStack.map { it.savedState }, listOf(restoredComponentState1, restoredComponentState2))
    }

    @Test
    fun WHEN_StackSaver_recreated_and_active_entry_preserved_THEN_isActiveEntryPreserved_true() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        oldSaver.register("key") { stack() }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState), onRestoreStack = { it })
        val restoredStack = newSaver.restore("key")

        assertEquals(true, restoredStack?.isActiveEntryPreserved)
    }

    @Test
    fun WHEN_StackSaver_recreated_and_active_entry_not_preserved_THEN_isActiveEntryPreserved_false() {
        val stateKeeperDispatcher = TestStateKeeperDispatcher()
        val oldSaver = stackSaver(stateKeeperDispatcher = stateKeeperDispatcher)
        oldSaver.register("key") { stack() }
        val savedState = stateKeeperDispatcher.save()

        val newSaver = stackSaver(stateKeeperDispatcher = TestStateKeeperDispatcher(savedState), onRestoreStack = { it + Config("x") })
        val restoredStack = newSaver.restore("key")

        assertEquals(false, restoredStack?.isActiveEntryPreserved)
    }

    private fun stackSaver(
        stateKeeperDispatcher: StateKeeperDispatcher = TestStateKeeperDispatcher(),
        onRestoreStack: (List<Config>) -> List<Config>? = { it }
    ): StackSaverImpl<Config> =
        StackSaverImpl(
            configurationClass = Config::class,
            stateKeeper = stateKeeperDispatcher,
            onRestoreStack = onRestoreStack,
            parcelableContainerFactory = ::TestParcelableContainer
        )

    private fun stack(): Stack<Config> =
        Stack(
            active = entry(Config("3")),
            backStack = listOf(entry(Config("1")), entry(Config("2")))
        )

    private fun entry(config: Config): Entry<Config> =
        Entry(configuration = config, savedState = TestParcelableContainer())

    @Parcelize
    private data class Config(val data: String) : Parcelable
}
