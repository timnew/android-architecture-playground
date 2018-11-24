package me.timnew.architecture.di.modules

import com.readystatesoftware.chuck.internal.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
  @ContributesAndroidInjector
  abstract fun bindMainActivity(): MainActivity
}
