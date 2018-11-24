package me.timnew.events

import io.reactivex.disposables.Disposable

data class EventSubscriber(
  val eventSource: EventSource,
  val subscriptions: MutableList<Disposable> = mutableListOf()
) : Iterable<Disposable> by subscriptions {

  @Suppress("unused")
  inline fun <reified TEvent, reified TAction> respondAction(
    action: TAction,
    noinline responder: Responder<TEvent>
  ): ActionEventSubscriber<TEvent, TAction>
    where TEvent : ActionEvent<TAction> =
    ActionEventSubscriber(this, eventSource.allEvents.ofType(TEvent::class.java)).respondAction(action, responder)

  @Suppress("unused")
  inline fun <reified TEvent, reified TAction> respondAction(
    action: TAction,
    noinline responder: NoParamResponder
  ): ActionEventSubscriber<TEvent, TAction>
    where TEvent : ActionEvent<TAction> =
    ActionEventSubscriber(this, eventSource.allEvents.ofType(TEvent::class.java)).respondAction(action, responder)

  @Suppress("unused")
  inline fun <reified TEvent> respond(@Suppress("UNUSED_PARAMETER") event: TEvent, noinline responder: NoParamResponder): EventSubscriber =
    apply {
      subscriptions.add(
        eventSource.allEvents.ofType(TEvent::class.java).subscribe { responder() }
      )
    }

  @Suppress("unused")
  inline fun <reified TEvent> respond(noinline responder: Responder<TEvent>): EventSubscriber =
    apply {
      subscriptions.add(
        eventSource.allEvents.ofType(TEvent::class.java).subscribe(responder)
      )
    }
}
