package me.timnew.architecture.di.components

import dagger.BindsInstance
import dagger.Component
import me.timnew.architecture.App
import me.timnew.architecture.di.modules.ActivityBindingModule
import me.timnew.architecture.di.modules.FragmentBindingModule

@Component(
  modules = [
    ActivityBindingModule::class,
    FragmentBindingModule::class
  ]
)
interface AppComponent {
  fun injectApp(app: App)

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun bindApp(app: App): Builder

    fun build(): AppComponent
  }
}
