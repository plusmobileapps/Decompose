package com.arkivanov.sample.navigation.app

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.lifecycle.LifecycleRegistry
import com.arkivanov.decompose.lifecycle.destroy
import com.arkivanov.decompose.lifecycle.resume
import com.arkivanov.sample.navigation.app.root.RootComponent
import com.arkivanov.sample.navigation.app.root.RootR
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.styles.Breakpoint
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

class App : RComponent<RProps, RState>() {

    private val lifecycle = LifecycleRegistry()
    private val ctx = DefaultComponentContext(lifecycle = lifecycle)
    private val root = RootComponent(context = ctx, deeplink = getDeeplink(), output = ::onRootOutput)

    private fun getDeeplink(): RootComponent.Deeplink? {
        val params = URLSearchParams(window.location.search)

        val detailsText: String? = params.get("detailsText")
        if (detailsText != null) {
            return RootComponent.Deeplink.Details(text = detailsText)
        }

        return null
    }

    private fun onRootOutput(output: RootComponent.Output): Unit =
        when (output) {
            is RootComponent.Output.DetailsShown -> replaceState(params = "?detailsText=${output.text}")
            is RootComponent.Output.DetailsClosed -> replaceState(params = "")
        }

    private fun replaceState(params: String) {
        window.history.replaceState(data = null, title = document.title, url = window.location.origin + params)
    }

    override fun componentDidMount() {
        lifecycle.resume()
    }

    override fun componentWillUnmount() {
        lifecycle.destroy()
    }

    override fun RBuilder.render() {
        mCssBaseline()

        mContainer(maxWidth = Breakpoint.xs) {
            renderableChild<RootComponent, RootR>(root)
        }
    }
}
