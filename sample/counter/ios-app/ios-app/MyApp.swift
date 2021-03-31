//
//  MyApp.swift
//  ios-app
//
//  Created by Arkadii Ivanov on 31/03/2021.
//  Copyright © 2021 Arkadii Ivanov. All rights reserved.
//

import Foundation
import SwiftUI
import Counter

@main
struct MyApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
//    private var componentHolder: ComponentHolder<CounterRootComponent>? = nil
    
    var body: some Scene {
        let h = ComponentHolder(stateKeeper: appDelegate.stateKeeper, factory: CounterRootComponent.init)

        
        return WindowGroup {
            ContentView(component: h.component)
                .onAppear { LifecycleRegistryExtKt.resume(h.lifecycle) }
                .onDisappear { LifecycleRegistryExtKt.stop(h.lifecycle) }
        }
    }
}
