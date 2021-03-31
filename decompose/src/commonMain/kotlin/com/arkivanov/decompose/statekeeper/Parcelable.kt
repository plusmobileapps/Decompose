package com.arkivanov.decompose.statekeeper

interface Parcelable {

    fun asHolder(): ParcelableHolder
}

interface ParcelableHolder : NSCodingProtocol {

    val value: Parcelable
}

expect open class NSObject()

expect interface NSCodingProtocol {
    fun encodeWithCoder(coder: NSCoder)

    fun initWithCoder(coder: NSCoder): NSCodingProtocol?
}

expect open class NSCoder

expect fun NSCoder.encodeInt_(value: Int, forKey: String)
expect fun NSCoder.decodeIntForKey_(key: String): Int
expect fun NSCoder.encodeBool_(value: Boolean, forKey: String)
expect fun NSCoder.decodeBoolForKey_(key: String): Boolean
expect fun NSCoder.encodeString(value: String, forKey: String)
expect fun NSCoder.decodeStringForKey(key: String): String
expect fun NSCoder.encodeParcelable(value: Parcelable?, forKey: String)
expect fun <T: Parcelable> NSCoder.decodeParcelable(key: String): T?

fun NSCoder.encodeParcelableList(value: List<Parcelable>, forKey: String) {
    encodeInt_(value.size, "$forKey-size")
    value.forEachIndexed { index, t ->
        encodeParcelable(t, "$forKey-$index")
    }
}

fun <T : Parcelable> NSCoder.decodeParcelableList(key: String): List<T> {
    val size = decodeIntForKey_("$key-size")

    return List(size) { decodeParcelable("$key-$it")!! }
}

