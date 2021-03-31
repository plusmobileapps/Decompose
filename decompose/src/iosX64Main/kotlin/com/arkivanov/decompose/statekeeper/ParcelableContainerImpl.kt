package com.arkivanov.decompose.statekeeper

import platform.Foundation.*
import kotlin.reflect.KClass

internal actual class ParcelableContainerImpl private constructor(
    private var value: Parcelable?
) : ParcelableContainer {

    actual constructor() : this(null)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Parcelable> consume(clazz: KClass<out T>): T? =
        (value as T?).also {
            value = null
        }

    override fun set(value: Parcelable?) {
        this.value = value
    }

    override fun asHolder(): ParcelableHolder = Holder(this)

    private class Holder(
        override val value: ParcelableContainerImpl
    ) : ParcelableHolder() {
        override fun encodeWithCoder(coder: NSCoder) {
            coder.encodeObject(value.value, "value")
        }

        override fun initWithCoder(coder: NSCoder): Holder =
            Holder(
                ParcelableContainerImpl(
                    value = coder.decodeObjectForKey("value") as Parcelable?
                )
            )
    }
}