package com.arkivanov.decompose.statekeeper

import platform.Foundation.*
import platform.darwin.NSObject
import kotlin.reflect.KClass

actual interface Parcelable {

    fun asHolder(): ParcelableHolder
}

abstract class ParcelableHolder : NSObject(), NSCodingProtocol {

    abstract val value: Parcelable
}

data class MyClass(
    val some: Int
) : Parcelable {

    override fun asHolder(): ParcelableHolder = Holder(this)

    private class Holder(
        override val value: MyClass
    ) : ParcelableHolder() {

        override fun encodeWithCoder(coder: NSCoder) {
            coder.encodeInt(value.some, "some")
        }

        override fun initWithCoder(coder: NSCoder): Holder =
             Holder(
                MyClass(
                    some = coder.decodeIntForKey("some")
                )
             )
    }
}
