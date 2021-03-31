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
    ) : NSObject(), ParcelableHolder {
        override fun encodeWithCoder(coder: NSCoder) {
            coder.encodeParcelable(value.value, "value")
        }

        override fun initWithCoder(coder: NSCoder): Holder =
            Holder(
                ParcelableContainerImpl(
                    value = coder.decodeParcelable("value")
                )
            )
    }
}