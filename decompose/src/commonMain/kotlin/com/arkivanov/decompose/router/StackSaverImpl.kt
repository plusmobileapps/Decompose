package com.arkivanov.decompose.router

import com.arkivanov.decompose.router.StackSaver.RestoredStack
import com.arkivanov.decompose.statekeeper.*
import kotlin.reflect.KClass

internal class StackSaverImpl<C : Parcelable>(
    private val configurationClass: KClass<out C>,
    private val stateKeeper: StateKeeper,
    private val parcelableContainerFactory: (Parcelable?) -> ParcelableContainer
) : StackSaver<C> {

    override fun register(key: String, supplier: () -> RouterStack<C, *>) {
        stateKeeper.register(key) { supplier().save() }
    }

    private fun RouterStack<C, *>.save(): SavedState =
        SavedState(
            active = SavedEntry(
                configuration = parcelableContainerFactory(active.configuration),
                savedState = active.stateKeeperDispatcher.save()
            ),
            backStack = backStack.map {
                SavedEntry(
                    configuration = parcelableContainerFactory(it.configuration),
                    savedState = it.savedState
                )
            }
        )

    override fun unregister(key: String) {
        stateKeeper.unregister(key)
    }

    override fun restore(key: String): RestoredStack<C>? =
        stateKeeper
            .consume<SavedState>(key)
            ?.restore()

    private fun SavedState.restore(): RestoredStack<C> =
        RestoredStack(
            active = active.restore(),
            backStack = backStack.map { it.restore() }
        )

    private fun SavedEntry.restore(): RouterEntry.Destroyed<C> =
        RouterEntry.Destroyed(
            configuration = configuration.consumeRequired(configurationClass),
            savedState = savedState
        )

    @Parcelize
    private class SavedState(
        val active: SavedEntry,
        val backStack: List<SavedEntry>
    ) : Parcelable {
        override fun asHolder(): ParcelableHolder = Holder(this)

        private class Holder(override val value: SavedState): NSObject(), ParcelableHolder {
            override fun encodeWithCoder(coder: NSCoder) {
                coder.encodeParcelable(value.active, "active")
                coder.encodeParcelableList(value.backStack, "backStack")
            }

            override fun initWithCoder(coder: NSCoder): Holder? =
                Holder(
                    SavedState(
                        active = coder.decodeParcelable("active")!!,
                        backStack = coder.decodeParcelableList("backStack")
                    )
                )
        }
    }

    @Parcelize
    private class SavedEntry(
        val configuration: ParcelableContainer,
        val savedState: ParcelableContainer?
    ) : Parcelable {
        override fun asHolder(): ParcelableHolder = Holder(this)

        private class Holder(override val value: SavedEntry): NSObject(), ParcelableHolder {
            override fun encodeWithCoder(coder: NSCoder) {
                coder.encodeParcelable(value.configuration, "configuration")
                coder.encodeParcelable(value.savedState, "savedState")
            }

            override fun initWithCoder(coder: NSCoder): Holder? =
                Holder(
                    SavedEntry(
                        configuration = coder.decodeParcelable("configuration")!!,
                        savedState = coder.decodeParcelable("savedState")
                    )
                )
        }
    }
}
