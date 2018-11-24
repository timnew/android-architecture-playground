package me.timnew.architecture.di.modules

import com.readystatesoftware.chuck.internal.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.timnew.architecture.di.ActivityScope

@Module
abstract class ActivityBindingModule {
  @ContributesAndroidInjector
  @ActivityScope
  abstract fun mainActivity(): MainActivity
}
