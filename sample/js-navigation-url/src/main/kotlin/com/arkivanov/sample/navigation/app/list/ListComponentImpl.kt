package com.arkivanov.sample.navigation.app.list

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.list.ListComponent.Model
import com.arkivanov.sample.navigation.app.list.ListComponent.Output

class ListComponentImpl(
    private val output: (Output) -> Unit
) : ListComponent {

    override val models: Value<Model> =
        MutableValue(Model(items = listOf("Item 1", "Item 2", "Item 3")))

    override fun onItemClicked(text: String) {
        output(Output.ItemSelected(text = text))
    }
}
