package com.arkivanov.sample.navigation.app

import react.RBuilder

inline fun <T : Any, reified R : RenderableComponent<T, *>> RBuilder.renderableChild(component: T) {
    child(R::class) {
        key = component.uniqueId().toString()
        attrs.component = component
    }
}
