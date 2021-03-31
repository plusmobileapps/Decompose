package com.arkivanov.sample.counter.shared.inner

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
import com.arkivanov.sample.counter.shared.inner.CounterInner.Child

internal class CounterInnerComponent(
    componentContext: ComponentContext,
    index: Int
) : CounterInner, ComponentContext by componentContext {


    override val counter: Counter = CounterComponent(childContext(key = "counter"), index = index)

    private val leftRouter: Router<ChildConfiguration, Child> =
        router(
            initialConfiguration = ChildConfiguration(index = 0, isBackEnabled = false),
            key = "LeftRouter",
            childFactory = ::resolveChild
        )

    override val leftRouterState: Value<RouterState<*, Child>> = leftRouter.state

    private val rightRouter: Router<ChildConfiguration, Child> =
        router(
            initialConfiguration = ChildConfiguration(index = 0, isBackEnabled = false),
            key = "RightRouter",
            childFactory = ::resolveChild
        )

    override val rightRouterState: Value<RouterState<*, Child>> = rightRouter.state

    private fun resolveChild(configuration: ChildConfiguration, componentContext: ComponentContext): Child =
        Child(
            counter = CounterComponent(componentContext, index = configuration.index),
            isBackEnabled = configuration.isBackEnabled
        )

    override fun onNextLeftChild() {
        leftRouter.pushNextChild()
    }

    override fun onPrevLeftChild() {
        leftRouter.pop()
    }

    override fun onNextRightChild() {
        rightRouter.pushNextChild()
    }

    override fun onPrevRightChild() {
        rightRouter.pop()
    }

    private fun Router<ChildConfiguration, *>.pushNextChild() {
        push(ChildConfiguration(index = state.value.backStack.size + 1, isBackEnabled = true))
    }

    @Parcelize
    private data class ChildConfiguration(val index: Int, val isBackEnabled: Boolean) : Parcelable {
        override fun asHolder(): ParcelableHolder = Holder(this)

        private class Holder(override val value: ChildConfiguration) : NSObject(), ParcelableHolder {
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
