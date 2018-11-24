package me.timnew.interactor

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Interactor : Disposable {
  private val subscriptions = CompositeDisposable()
  override fun isDisposed(): Boolean = subscriptions.isDisposed
  override fun dispose(): Unit = subscriptions.dispose()

  open fun start() {
    subscriptions.clear()

    subscription().forEach { subscriptions.add(it) }
  }

  open fun stop() {
    subscriptions.clear()
  }

  protected abstract fun subscription(): Iterator<Disposable>
}
