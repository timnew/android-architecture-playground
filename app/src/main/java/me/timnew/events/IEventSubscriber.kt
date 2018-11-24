package me.timnew.events

interface IEventSubscriber {
  fun belongsTo(subscription: me.timnew.interactor.SubscriptionDescriber)
}
