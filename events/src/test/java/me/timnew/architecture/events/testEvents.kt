package me.timnew.architecture.events

object TestSingletonEvent

enum class TestAction {
  PrintToConsole,
  WriteToFile,
  SendToNetwork
}

data class TestSubjectEvent(override val subject: String) : SubjectEvent<String>
data class TestActionEvent(override val action: TestAction) : ActionEvent<TestAction>
data class TestBehaviorEvent(override val action: TestAction, override val subject: String) :
  BehaviorEvent<TestAction, String>
