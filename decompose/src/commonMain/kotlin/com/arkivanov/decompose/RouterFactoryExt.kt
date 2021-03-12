package com.arkivanov.decompose

import com.arkivanov.decompose.statekeeper.Parcelable

/**
 * A convenience extension function for [RouterFactory.router].
 */
inline fun <reified C : Parcelable, T : Any> RouterFactory.router(
    initialConfiguration: C,
    initialBackStack: List<C> = emptyList(),
    key: String = "DefaultRouter",
    noinline onRestoreStack: (List<C>) -> List<C>? = { it },
    handleBackButton: Boolean = false,
    noinline componentFactory: (configuration: C, ComponentContext) -> T
): Router<C, T> =
    router(
        initialConfiguration = { initialConfiguration },
        initialBackStack = { initialBackStack },
        configurationClass = C::class,
        key = key,
        onRestoreStack = onRestoreStack,
        handleBackButton = handleBackButton,
        componentFactory = componentFactory
    )

/**
 * A convenience extension function for [RouterFactory.router].
 */
inline fun <reified C : Parcelable, T : Any> RouterFactory.router(
    noinline initialConfiguration: () -> C,
    noinline initialBackStack: () -> List<C> = ::emptyList,
    key: String = "DefaultRouter",
    noinline onRestoreStack: (List<C>) -> List<C>? = { it },
    handleBackButton: Boolean = false,
    noinline componentFactory: (configuration: C, ComponentContext) -> T
): Router<C, T> =
    router(
        initialConfiguration = initialConfiguration,
        initialBackStack = initialBackStack,
        configurationClass = C::class,
        onRestoreStack = onRestoreStack,
        key = key,
        handleBackButton = handleBackButton,
        componentFactory = componentFactory
    )
