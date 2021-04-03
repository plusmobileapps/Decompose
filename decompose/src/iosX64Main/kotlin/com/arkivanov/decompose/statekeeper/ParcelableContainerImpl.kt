package com.arkivanov.decompose.statekeeper

import platform.Foundation.NSCoder
import platform.Foundation.NSCodingProtocol
import platform.Foundation.encodeInt
import platform.darwin.NSObject
import kotlin.reflect.KClass

internal actual class ParcelableContainerImpl actual constructor() : ParcelableContainer {

    private var value: Any? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : Parcelable> consume(clazz: KClass<out T>): T? =
        (value as T?).also {
            value = null
        }

    override fun set(value: Parcelable?) {
        this.value = value
    }

    override fun coding(): NSCodingProtocol {
        TODO("Not yet implemented")
    }
}

//@Parcelize
//class Some(
//    val a: Int
//) : Parcelable {
//
//    fun coding2(): NSCodingProtocol = CodingImpl2(this)
//
//    private class CodingImpl2(
//        private val data: Some
//    ) : NSObject(), NSCodingProtocol {
//        override fun encodeWithCoder(coder: NSCoder) {
//            coder.encodeInt(data.a, "a")
//        }
//
//        override fun initWithCoder(coder: NSCoder): NSCodingProtocol? = null
//    }
//}
//
