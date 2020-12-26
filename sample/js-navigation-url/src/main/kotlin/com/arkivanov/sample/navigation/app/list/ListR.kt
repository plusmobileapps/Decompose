package com.arkivanov.sample.navigation.app.list

import com.arkivanov.sample.navigation.app.RenderableComponent
import com.arkivanov.sample.navigation.app.list.ListComponent.Model
import com.ccfraser.muirwik.components.MPaperVariant
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mPaper
import react.RBuilder
import react.RState

class ListR(props: Props<ListComponent>) : RenderableComponent<ListComponent, ListR.State>(
    props = props,
    initialState = State(model = props.component.models.value)
) {

    init {
        component.models.bindToState { model = it }
    }

    override fun RBuilder.render() {
        val model = state.model

        mPaper(variant = MPaperVariant.outlined) {
            mList {
                model.items.forEach { item ->
                    mListItem(button = true, key = item, onClick = { component.onItemClicked(text = item) }) {
                        mListItemText(primary = item)
                    }
                }
            }
        }
    }

    class State(
        var model: Model
    ) : RState
}
