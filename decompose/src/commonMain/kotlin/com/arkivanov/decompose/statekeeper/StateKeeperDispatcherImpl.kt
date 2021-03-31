package com.arkivanov.decompose.statekeeper

import kotlin.reflect.KClass

internal class StateKeeperDispatcherImpl(savedState: ParcelableContainer?) : StateKeeperDispatcher {

    private val savedState: MutableMap<String, ParcelableContainer>? = savedState?.consume<SavedState>()?.map
    private val suppliers = HashMap<String, () -> Parcelable>()

    override fun save(): ParcelableContainer? =
        try {
            ParcelableContainer(SavedState(suppliers.mapValuesTo(HashMap()) { ParcelableContainer(it.value()) }))
        } catch (e: Exception) {
            null
        }

    override fun <T : Parcelable> consume(key: String, clazz: KClass<out T>): T? =
        savedState
            ?.remove(key)
            ?.consume(clazz)

    override fun <T : Parcelable> register(key: String, supplier: () -> T) {
        check(key !in suppliers)
        suppliers[key] = supplier
    }

    override fun unregister(key: String) {
        check(key in suppliers)
        suppliers -= key
    }

    @Parcelize
    private class SavedState(
        val map: HashMap<String, ParcelableContainer>
    ) : Parcelable {
        override fun asHolder(): NSCodingProtocol = Holder(this)

        @ExportObjCClass("Holder9") private class Holder(private val value: SavedState): NSObject(), NSCodingProtocol {
            override fun encodeWithCoder(coder: NSCoder) {
                coder.encodeInt_(value.map.size, "size")
                value.map.entries.forEachIndexed { index, entry ->
                    coder.encodeString(entry.key, "key-$index")
                    coder.encodeParcelable(entry.value, "value-$index")
                }
            }

            override fun initWithCoder(coder: NSCoder): ValueHolder {
                val size = coder.decodeIntForKey_("size")
                val map = HashMap<String, ParcelableContainer>()
                repeat(size) {
                    val key = coder.decodeStringForKey("key-$it")
                    val value = coder.decodeParcelable<ParcelableContainer>("value-$it")!!
                    map[key] = value
                }

                return ValueHolder(SavedState(map))
            }
        }
    }
}
