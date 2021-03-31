package com.arkivanov.decompose.statekeeper

import platform.Foundation.*

actual typealias NSObject = platform.darwin.NSObject

actual typealias NSCodingProtocol = platform.Foundation.NSCodingProtocol

actual typealias NSCoder = platform.Foundation.NSCoder

actual fun NSCoder.encodeInt_(value: Int, forKey: String) {
    encodeInt(value, forKey)
}

actual fun NSCoder.decodeIntForKey_(key: String): Int = decodeIntForKey(key)

actual fun NSCoder.encodeBool_(value: Boolean, forKey: String) {
    encodeBool(value, forKey)
}

actual fun NSCoder.decodeBoolForKey_(key: String): Boolean = decodeBoolForKey(key)

actual fun NSCoder.encodeString(value: String, forKey: String) {
    encodeObject(value as NSData, forKey)
}

actual fun NSCoder.decodeStringForKey(key: String): String =
    decodeObject() as String

actual fun NSCoder.encodeParcelable(value: Parcelable?, forKey: String) {
    encodeObject(value?.asHolder(), forKey)
}

actual fun <T: Parcelable> NSCoder.decodeParcelable(key: String): T? =
    (decodeObjectForKey(key) as ParcelableHolder?)?.value as T?

//data class MyClass(
//    val some: Int
//) : Parcelable {
//
//    override fun asHolder(): ParcelableHolder = Holder(this)
//
//    private class Holder(
//        override val value: MyClass
//    ) : ParcelableHolder {
//
//        override fun encodeWithCoder(coder: NSCoder) {
//            coder.encodeInt(value.some, "some")
//        }
//
//        override fun initWithCoder(coder: NSCoder): Holder =
//             Holder(
//                MyClass(
//                    some = coder.decodeIntForKey("some")
//                )
//             )
//    }
//}
