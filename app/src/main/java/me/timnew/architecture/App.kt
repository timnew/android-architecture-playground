package me.timnew.architecture

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import me.timnew.architecture.di.components.DaggerAppComponent
import javax.inject.Inject

class App : Application(), HasActivityInjector {

  @Inject
  lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

  override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

  override fun onCreate() {
    super.onCreate()
    DaggerAppComponent
      .builder()
      .bindApp(this)
      .build()
      .injectApp(this)
  }
}
