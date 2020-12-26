package com.arkivanov.sample.navigation.app.details

import com.arkivanov.decompose.value.Value
import com.arkivanov.sample.navigation.app.details.DetailsComponent.Output

interface DetailsComponent {

    val models: Value<Model>

    fun onCloseClicked()

    data class Model(
        val text: String
    )

    sealed class Output {
        object Finished : Output()
    }
}

fun DetailsComponent(
    text: String,
    output: (Output) -> Unit
): DetailsComponent =
    DetailsComponentImpl(
        text = text,
        output = output
    )
