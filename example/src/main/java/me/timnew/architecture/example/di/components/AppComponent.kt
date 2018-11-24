package me.timnew.architecture.example.di.components

import dagger.BindsInstance
import dagger.Component
import me.timnew.architecture.example.App
import me.timnew.architecture.example.di.modules.ActivityBindingModule
import me.timnew.architecture.example.di.modules.FragmentBindingModule
import javax.inject.Singleton

@Component(
  modules = [
    ActivityBindingModule::class,
    FragmentBindingModule::class
  ]
)
@Singleton
interface AppComponent {
  fun injectApp(app: App)

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun bindApp(app: App): Builder

    fun build(): AppComponent
  }
}
