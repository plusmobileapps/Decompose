package com.arkivanov.sample.navigation.app.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.Parcelable
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.details.DetailsComponent
import com.arkivanov.sample.navigation.app.list.ListComponent
import com.arkivanov.sample.navigation.app.root.RootComponent.Child
import com.arkivanov.sample.navigation.app.root.RootComponent.Deeplink
import com.arkivanov.sample.navigation.app.root.RootComponent.Output

class RootComponentImpl(
    context: ComponentContext,
    deeplink: Deeplink?,
    private val output: (Output) -> Unit
) : RootComponent, ComponentContext by context {

    private val router =
        router(
            initialConfiguration = { getInitialConfiguration(deeplink) },
            initialBackStack = { getInitialBackStack(deeplink) },
            componentFactory = ::createComponent
        )

    private fun getInitialConfiguration(deeplink: Deeplink?): Configuration =
        when (deeplink) {
            is Deeplink.Details -> Configuration.Details(text = deeplink.text)
            null -> Configuration.List
        }

    private fun getInitialBackStack(deeplink: Deeplink?): List<Configuration> =
        when (deeplink) {
            is Deeplink.Details -> listOf(Configuration.List)
            null -> emptyList()
        }

    override val routerState: Value<RouterState<*, Child>> = router.state

    private fun createComponent(configuration: Configuration, context: ComponentContext): Child =
        when (configuration) {
            is Configuration.List -> Child.List(list())
            is Configuration.Details -> Child.Details(details(text = configuration.text))
        }

    private fun list(): ListComponent =
        ListComponent(
            output = ::onListOutput
        )

    private fun details(text: String): DetailsComponent =
        DetailsComponent(
            text = text,
            output = ::onDetailsOutput
        )

    private fun onListOutput(output: ListComponent.Output): Unit =
        when (output) {
            is ListComponent.Output.ItemSelected -> {
                router.push(Configuration.Details(text = output.text))
                output(Output.DetailsShown(text = output.text))
            }
        }

    private fun onDetailsOutput(output: DetailsComponent.Output): Unit =
        when (output) {
            is DetailsComponent.Output.Finished -> {
                router.pop()
                output(Output.DetailsClosed)
            }
        }

    private sealed class Configuration : Parcelable {
        object List : Configuration()
        data class Details(val text: String) : Configuration()
    }
}
