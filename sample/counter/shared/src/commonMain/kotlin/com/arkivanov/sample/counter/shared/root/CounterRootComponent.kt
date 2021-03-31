package com.arkivanov.sample.counter.shared.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.Router
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.counter.shared.counter.Counter
import com.arkivanov.sample.counter.shared.counter.CounterComponent
import com.arkivanov.sample.counter.shared.inner.CounterInnerComponent
import com.arkivanov.sample.counter.shared.root.CounterRoot.Child

class CounterRootComponent(
    componentContext: ComponentContext
) : CounterRoot, ComponentContext by componentContext {

    override val counter: Counter = CounterComponent(childContext(key = "counter"), index = 0)

    private val router: Router<ChildConfiguration, Child> =
        router(
            initialConfiguration = ChildConfiguration(index = 0, isBackEnabled = false),
            handleBackButton = true,
            childFactory = ::resolveChild
        )

    override val routerState: Value<RouterState<*, Child>> = router.state

    private fun resolveChild(configuration: ChildConfiguration, componentContext: ComponentContext): Child =
        Child(
            inner = CounterInnerComponent(componentContext, index = configuration.index),
            isBackEnabled = configuration.isBackEnabled
        )

    override fun onNextChild() {
        router.push(ChildConfiguration(index = router.state.value.backStack.size + 1, isBackEnabled = true))
    }

    override fun onPrevChild() {
        router.pop()
    }

    @Parcelize
    private data class ChildConfiguration(val index: Int, val isBackEnabled: Boolean) : Parcelable {
        override fun asHolder(): ParcelableHolder = Holder(this)

        private class Holder(override val value: ChildConfiguration) : ParcelableHolder() {
            override fun encodeWithCoder(coder: NSCoder) {
                coder.encodeInt_(value.index, "index")
                coder.encodeBool_(value.isBackEnabled, "isBackEnabled")
            }

            override fun initWithCoder(coder: NSCoder): Holder =
                Holder(
                    ChildConfiguration(
                        index = coder.decodeIntForKey_("index"),
                        isBackEnabled = coder.decodeBoolForKey_("isBackEnabled")
                    )
                )
        }
    }
}
