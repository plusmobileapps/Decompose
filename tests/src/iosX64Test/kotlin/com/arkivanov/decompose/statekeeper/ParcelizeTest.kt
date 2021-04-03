package com.arkivanov.decompose.statekeeper

import com.arkivanov.plugin.runtime.DecodedValue
import com.arkivanov.plugin.runtime.Parcelable
import com.arkivanov.plugin.runtime.Parcelize
import kotlinx.cinterop.Arena
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import platform.Foundation.NSCoder
import platform.Foundation.NSCodingProtocol
import platform.Foundation.NSData
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSKeyedUnarchiver
import platform.Foundation.dataWithBytes
import platform.Foundation.decodeObjectForKey
import platform.Foundation.encodeObject
import platform.darwin.NSObject
import kotlin.test.Test
import kotlin.test.assertEquals

class ParcelizeTest {

    @Test
    fun encodes_and_decodes() {
        val some =
            Some(
                a = 1,
                b = 2,
                c = null,
                other1 = Other(a = 1),
                other2 = null,
                parcelableList1 = listOf(Other(a = 3), Other(a = 4)),
                parcelableList2 = null,
                parcelableList3 = mutableListOf(Other(a = 5), Other(a = 6)),
                parcelableList4 = null,
                parcelableSet1 = setOf(Other(a = 3), Other(a = 4)),
                parcelableSet2 = null,
                parcelableSet3 = mutableSetOf(Other(a = 5), Other(a = 6)),
                parcelableSet4 = null,
                stringList1 = listOf("a", "b"),
                stringList2 = null,
                stringList3 = mutableListOf("c", "d"),
                stringList4 = null,
                stringSet1 = setOf("aa", "bb"),
                stringSet2 = null,
                stringSet3 = mutableSetOf("cc", "dd"),
                stringSet4 = null
            )
        val coding = some.coding()
        val data = NSKeyedArchiver.archivedDataWithRootObject(coding)
        val arr = data.bytes()
        val len = data.length()
        val bytes = arr!!.readBytes(len.toInt())
        val scope = Arena()

        val data2: NSData = NSData.dataWithBytes(bytes.toCValues().getPointer(scope), len)
        val some2 = (NSKeyedUnarchiver.unarchiveObjectWithData(data2) as DecodedValue).value as Some
        assertEquals(some, some2)
    }

    @Parcelize
    private data class Other(
        val a: Int
    ) : Parcelable

    @Parcelize
    private data class Some(
        val a: Int,
        val b: Int?,
        val c: Int?,
        val other1: Other,
        val other2: Other?,
        val parcelableList1: List<Other>,
        val parcelableList2: List<Other>?,
        val parcelableList3: MutableList<Other>?,
        val parcelableList4: MutableList<Other>?,
        val parcelableSet1: Set<Other>,
        val parcelableSet2: Set<Other>?,
        val parcelableSet3: MutableSet<Other>?,
        val parcelableSet4: MutableSet<Other>?,
        val stringList1: List<String>,
        val stringList2: List<String>?,
        val stringList3: MutableList<String>,
        val stringList4: MutableList<String>?,
        val stringSet1: Set<String>,
        val stringSet2: Set<String>?,
        val stringSet3: MutableSet<String>,
        val stringSet4: MutableSet<String>?,
    ) : Parcelable
}