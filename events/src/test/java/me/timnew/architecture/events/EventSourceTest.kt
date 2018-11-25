package me.timnew.architecture.events

import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventSourceTest {
    private lateinit var allEvents: PublishSubject<Any>
    private lateinit var eventSource: EventSource

    @Before
    fun setUp() {
        allEvents = spyk(PublishSubject.create())
        eventSource = EventSource(allEvents)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should post and subscribe singleton event`() {
        val responder = mockk<NoParamResponder>(relaxed = true)

        eventSource.respond(TestSingletonEvent, responder)

        verify(exactly = 0) { responder() }

        eventSource.publish(TestSingletonEvent)

        verify(exactly = 1) { responder() }
    }

    @Test
    fun `should publish and subscribe subject event`() {
        val responder = mockk<Responder<TestSubjectEvent>>(relaxed = true)

        eventSource.respond(responder)

        verify(exactly = 0) { responder(any()) }

        val testSubject = "testSubject"

        eventSource.publish(TestSubjectEvent(subject = testSubject))

        verify(exactly = 1) { responder(match { it.subject == testSubject }) }
    }

    @Test
    fun `should publish and subscribe action event`() {
        val responder = mockk<Responder<TestActionEvent>>(relaxed = true)

        eventSource.respond(responder)

        verify(exactly = 0) { responder(any()) }

        eventSource.publish(TestActionEvent(action = TestAction.PrintToConsole))

        verify(exactly = 1) { responder(match { it.action == TestAction.PrintToConsole }) }
    }

    @Test
    fun `should publish and subscribe behaviour event`() {
        val responder = mockk<Responder<TestBehaviorEvent>>(relaxed = true)

        eventSource.respond(responder)

        verify(exactly = 0) { responder(any()) }

        val testSubject = "testSubject"

        eventSource.publish(TestBehaviorEvent(action = TestAction.PrintToConsole, subject = testSubject))

        verify(exactly = 1) { responder(match { it.action == TestAction.PrintToConsole && it.subject == testSubject }) }
    }

    @Test
    fun `should dispatch events based on action`() {
        val printResponder = mockk<Responder<TestActionEvent>>(relaxed = true)
        val writeResponder = mockk<Responder<TestActionEvent>>(relaxed = true)

        eventSource.respondAction(TestAction.PrintToConsole, printResponder)
        eventSource.respondAction(TestAction.WriteToFile, writeResponder)

        eventSource.publish(TestActionEvent(TestAction.PrintToConsole))

        verify(exactly = 1) { printResponder(match { it.action == TestAction.PrintToConsole }) }
        verify(exactly = 0) { writeResponder(any()) }

        eventSource.publish(TestActionEvent(TestAction.WriteToFile))

        verify(exactly = 1) { printResponder(any()) }
        verify(exactly = 1) { writeResponder(match { it.action == TestAction.WriteToFile }) }

        eventSource.publish(TestActionEvent(TestAction.SendToNetwork))

        verify(exactly = 1) { printResponder(any()) }
        verify(exactly = 1) { writeResponder(any()) }
    }
}
