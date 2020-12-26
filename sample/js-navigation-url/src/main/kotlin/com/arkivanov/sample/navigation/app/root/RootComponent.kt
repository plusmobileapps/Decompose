package com.arkivanov.sample.navigation.app.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.details.DetailsComponent
import com.arkivanov.sample.navigation.app.list.ListComponent
import com.arkivanov.sample.navigation.app.root.RootComponent.Deeplink
import com.arkivanov.sample.navigation.app.root.RootComponent.Output

interface RootComponent {

    val routerState: Value<RouterState<*, Child>>

    sealed class Child {
        data class List(val component: ListComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
    }

    sealed class Deeplink {
        data class Details(val text: String) : Deeplink()
    }

    sealed class Output {
        data class DetailsShown(val text: String) : Output()
        object DetailsClosed : Output()
    }
}

fun RootComponent(
    context: ComponentContext,
    deeplink: Deeplink?,
    output: (Output) -> Unit
): RootComponent =
    RootComponentImpl(
        context = context,
        deeplink = deeplink,
        output = output
    )
