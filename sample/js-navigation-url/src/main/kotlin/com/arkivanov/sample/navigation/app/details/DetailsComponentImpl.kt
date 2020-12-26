package com.arkivanov.sample.navigation.app.details

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.details.DetailsComponent.Model
import com.arkivanov.sample.navigation.app.details.DetailsComponent.Output

class DetailsComponentImpl(
    text: String,
    private val output: (Output) -> Unit
) : DetailsComponent {

    override val models: Value<Model> =
        MutableValue(Model(text = text))

    override fun onCloseClicked() {
        output(Output.Finished)
    }
}
