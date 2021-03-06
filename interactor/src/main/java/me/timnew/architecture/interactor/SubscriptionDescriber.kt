package me.timnew.architecture.interactor

import io.reactivex.disposables.Disposable

typealias SubscriptionDescriptor = SubscriptionDescriber.(SubscriptionDescriber) -> Any

class SubscriptionDescriber(
  descriptor: SubscriptionDescriptor,
  private val subscriptions: MutableList<Disposable> = mutableListOf()
) : Iterable<Disposable> by subscriptions {
  init {
    descriptor(this)
  }

  @Suppress("unused")
  infix fun has(disposable: Disposable) =
    subscriptions.add(disposable)

  @Suppress("unused")
  infix fun has(disposable: Iterable<Disposable>) =
    disposable.forEach { subscriptions.add(it) }

  @Suppress("unused")
  infix fun has(disposable: Iterator<Disposable>) =
    disposable.forEach { subscriptions.add(it) }
}

fun subscriptionAs(descriptor: SubscriptionDescriptor) =
  SubscriptionDescriber(descriptor)
