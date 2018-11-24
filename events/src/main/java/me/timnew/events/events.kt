package me.timnew.events

typealias Responder<T> = (T) -> Unit
typealias NoParamResponder = () -> Unit

interface SubjectEvent<T> {
  val subject: T
}

interface ActionEvent<T> {
  val action: T
}

interface BehaviorEvent<TSubject, TAction> : SubjectEvent<TSubject>,
  ActionEvent<TAction>

