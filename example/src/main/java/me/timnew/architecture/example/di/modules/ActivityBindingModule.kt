package me.timnew.architecture.example.di.modules

import com.readystatesoftware.chuck.internal.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.timnew.architecture.example.di.ActivityScope

@Module
abstract class ActivityBindingModule {
  @ContributesAndroidInjector
  @ActivityScope
  abstract fun mainActivity(): MainActivity
}
