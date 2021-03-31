//
//  ContentView.swift
//  ios-app
//
//  Created by Arkadii Ivanov on 9/11/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI
import Counter

struct ContentView: View {
    var component: CounterRootComponent

    var body: some View {
        CounterRootView(component)
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView(stateKeeper: nil)
//    }
//}

class ComponentHolder<T> {
    let lifecycle: LifecycleRegistry
    let component: T
    
    init(stateKeeper: StateKeeper?, factory: (ComponentContext) -> T) {
        let lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        let component =
            factory(
                DefaultComponentContext(lifecycle: lifecycle, stateKeeper: stateKeeper, instanceKeeper:nil, backPressedDispatcher: nil
                    )
            )
        self.lifecycle = lifecycle
        self.component = component

        lifecycle.onCreate()
    }
    
    deinit {
        lifecycle.onDestroy()
    }
}
