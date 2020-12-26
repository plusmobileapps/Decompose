package com.arkivanov.sample.navigation.app.root

import com.arkivanov.decompose.RouterState
import com.arkivanov.sample.navigation.app.RenderableComponent
import com.arkivanov.sample.navigation.app.details.DetailsComponent
import com.arkivanov.sample.navigation.app.details.DetailsR
import com.arkivanov.sample.navigation.app.list.ListComponent
import com.arkivanov.sample.navigation.app.list.ListR
import com.arkivanov.sample.navigation.app.renderableChild
import com.arkivanov.sample.navigation.app.root.RootComponent.Child
import com.ccfraser.muirwik.components.MPaperVariant
import com.ccfraser.muirwik.components.mPaper
import react.RBuilder
import react.RState

class RootR(props: Props<RootComponent>) : RenderableComponent<RootComponent, RootR.State>(
    props = props,
    initialState = State(routerState = props.component.routerState.value)
) {

    init {
        component.routerState.bindToState { routerState = it }
    }

    override fun RBuilder.render() {
        val activeChild = state.routerState.activeChild.component

        mPaper(variant = MPaperVariant.outlined) {
            when (activeChild) {
                is Child.List -> renderableChild<ListComponent, ListR>(activeChild.component)
                is Child.Details -> renderableChild<DetailsComponent, DetailsR>(activeChild.component)
            }.let {}
        }
    }

    class State(
        var routerState: RouterState<*, Child>,
    ) : RState
}
