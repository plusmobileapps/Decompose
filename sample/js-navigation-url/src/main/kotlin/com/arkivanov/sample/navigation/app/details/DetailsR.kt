package com.arkivanov.sample.navigation.app.details

import com.arkivanov.sample.navigation.app.RenderableComponent
import com.arkivanov.sample.navigation.app.details.DetailsComponent.Model
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.MPaperVariant
import com.ccfraser.muirwik.components.MTypographyAlign
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.mPaper
import com.ccfraser.muirwik.components.mTypography
import react.RBuilder
import react.RState

class DetailsR(props: Props<DetailsComponent>) : RenderableComponent<DetailsComponent, DetailsR.State>(
    props = props,
    initialState = State(model = props.component.models.value)
) {

    init {
        component.models.bindToState { model = it }
    }

    override fun RBuilder.render() {
        val model = state.model

        mPaper(variant = MPaperVariant.outlined) {
            mTypography(text = model.text, align = MTypographyAlign.center)

            mButton(
                caption = "Back",
                variant = MButtonVariant.contained,
                color = MColor.primary,
                onClick = { component.onCloseClicked() }
            )
        }
    }

    class State(
        var model: Model
    ) : RState
}
