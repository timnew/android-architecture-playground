package me.timnew.architecture.events

typealias Responder<T> = (T) -> Unit
typealias NoParamResponder = () -> Unit

interface SubjectEvent<T> {
  val subject: T
}

interface ActionEvent<T> {
  val action: T
}

interface BehaviorEvent<TAction, TSubject> :
  ActionEvent<TAction>,
  SubjectEvent<TSubject>


