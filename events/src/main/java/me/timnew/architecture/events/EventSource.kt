package me.timnew.architecture.events

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

open class EventSource(
  @PublishedApi internal val allEvents: Subject<Any>
) {
  @Suppress("unused")
  @Inject
  constructor() : this(allEvents = PublishSubject.create())

  @Suppress("unused")
  inline fun <reified T> publish(event: T) =
    allEvents.onNext(event as Any)

  @Suppress("unused")
  inline fun <reified TEvent, reified TAction> respondAction(
    action: TAction,
    noinline responder: Responder<TEvent>
  ): ActionEventSubscriber<TEvent, TAction>
    where TEvent : ActionEvent<TAction> =
    EventSubscriber(this).respondAction(action, responder)

  @Suppress("unused")
  inline fun <reified TEvent, reified TAction> respondAction(
    action: TAction,
    noinline responder: NoParamResponder
  ): ActionEventSubscriber<TEvent, TAction>
    where TEvent : ActionEvent<TAction> =
    EventSubscriber(this).respondAction(action, responder)

  @Suppress("unused")
  inline fun <reified TEvent> respond(@Suppress("UNUSED_PARAMETER") event: TEvent, noinline responder: NoParamResponder): EventSubscriber =
    EventSubscriber(this).respond(event, responder)

  @Suppress("unused")
  inline fun <reified TEvent> respond(@Suppress("UNUSED_PARAMETER") event: TEvent, noinline responder: Responder<TEvent>): EventSubscriber =
    EventSubscriber(this).respond(event, responder)

  @Suppress("unused")
  inline fun <reified T> respond(noinline responder: Responder<T>): EventSubscriber =
    EventSubscriber(this).respond(responder)
}

typealias EventPublisher<T> = (T) -> Unit
