package me.timnew.architecture.events

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

data class ActionEventSubscriber<TEvent, TAction>(
  @PublishedApi internal val parent: EventSubscriber,
  private val typedSource: Observable<TEvent>
) : Iterable<Disposable> by parent.subscriptions
  where TEvent : ActionEvent<TAction> {

  private inline fun buildChain(action: TAction, builder: Observable<TEvent>.() -> Disposable) =
    apply {
      parent.subscriptions.add(builder(typedSource.filter { it.action == action }))
    }

  @Suppress("unused")
  fun respondAction(action: TAction, responder: Responder<TEvent>): ActionEventSubscriber<TEvent, TAction> =
    buildChain(action) { subscribe(responder) }

  @Suppress("unused")
  fun respondAction(action: TAction, responder: NoParamResponder): ActionEventSubscriber<TEvent, TAction> =
    buildChain(action) { subscribe { responder() } }

  @Suppress("unused")
  inline fun <reified T> respond(@Suppress("UNUSED_PARAMETER") event: T, noinline responder: NoParamResponder): EventSubscriber =
    parent.respond(event, responder)

  @Suppress("unused")
  inline fun <reified TEvent> respond(@Suppress("UNUSED_PARAMETER") event: TEvent, noinline responder: Responder<TEvent>): EventSubscriber =
    parent.respond(event, responder)

  @Suppress("unused")
  inline fun <reified T> respond(noinline responder: Responder<T>): EventSubscriber =
    parent.respond(responder)
}
