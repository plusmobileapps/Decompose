package com.arkivanov.sample.navigation.app.list

import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.list.ListComponent.Output

interface ListComponent {

    val models: Value<Model>

    fun onItemClicked(text: String)

    data class Model(
        val items: List<String>
    )

    sealed class Output {
        data class ItemSelected(val text: String) : Output()
    }
}

fun ListComponent(output: (Output) -> Unit): ListComponent =
    ListComponentImpl(
        output = output
    )
