package me.timnew.architecture.interactor

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Interactor : Disposable {
  private val subscriptions = CompositeDisposable()
  override fun isDisposed(): Boolean = subscriptions.isDisposed
  override fun dispose() {
    stop()
    subscriptions.dispose()
  }

  var isRunning: Boolean = false
    protected set(value) {
      field = value
    }

  open fun start() {
    if (isDisposed) {
      throw RuntimeException("Interactor has been disposed")
    }

    if (isRunning) {
      stop()
    }

    subscription().forEach { subscriptions.add(it) }

    isRunning = true
  }

  open fun stop() {
    subscriptions.clear()
    isRunning = false
  }

  protected abstract fun subscription(): Iterable<Disposable>
}
