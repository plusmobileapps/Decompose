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

    override fun asHolder(): NSCodingProtocol = Holder(this)

    @ExportObjCClass("Holder4") private class Holder(
        private val value: ParcelableContainerImpl
    ) : NSObject(), NSCodingProtocol {
        override fun encodeWithCoder(coder: NSCoder) {
            coder.encodeParcelable(value.value, "value")
        }

        override fun initWithCoder(coder: NSCoder): ValueHolder =
            ValueHolder(
                ParcelableContainerImpl(
                    value = coder.decodeParcelable("value")
                )
            )
    }
}