package me.timnew.architecture.events

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.disposables.Disposable
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventSubscriberTest {
  private lateinit var subscriptions: MutableList<Disposable>
  private lateinit var eventSource: EventSource
  private lateinit var subscriber: EventSubscriber
  private lateinit var actionSubscriber: ActionEventSubscriber<TestBehaviorEvent, TestAction>

  @RelaxedMockK
  lateinit var noParamResponder: NoParamResponder

  @RelaxedMockK
  lateinit var responder: Responder<TestBehaviorEvent>

  private val event = TestBehaviorEvent(action = TestAction.SendToNetwork, subject = "data")
  private val differentEvent = TestSubjectEvent("Data")

  @Before
  fun setUp() {
    MockKAnnotations.init(this)

    subscriptions = mutableListOf()
    eventSource = EventSource()
    subscriber = spyk(EventSubscriber(eventSource, subscriptions))
    actionSubscriber =
      spyk(ActionEventSubscriber(subscriber, eventSource.allEvents.ofType(TestBehaviorEvent::class.java)))
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should delegate call the event subscriber for singleton event with no param`() {
    val eventSubscriber = eventSource.respond(event, noParamResponder)

    assertThat(eventSubscriber).isInstanceOf(EventSubscriber::class.java)
    assertThat(eventSubscriber.subscriptions).hasSize(1)
  }

  @Test
  fun `should accept the non param responder for singleton event`() {
    Assertions.assertThat(subscriber.respond(event, noParamResponder)).isEqualTo(subscriber)

    eventSource.publish(event)

    verify(exactly = 1) { noParamResponder() }

    subscriptions.forEach { it.dispose() }
    eventSource.publish(TestSingletonEvent)

    verify(exactly = 1) { noParamResponder() }

    eventSource.publish(differentEvent)

    verify(exactly = 1) { noParamResponder() }
  }

  @Test
  fun `should delegate call the event subscriber for singleton event`() {
    val eventSubscriber = eventSource.respond(event, responder)

    assertThat(eventSubscriber).isInstanceOf(EventSubscriber::class.java)
    assertThat(eventSubscriber.subscriptions).hasSize(1)
  }

  @Test
  fun `should accept the responder for singleton event`() {
    Assertions.assertThat(subscriber.respond(event, responder)).isEqualTo(subscriber)

    eventSource.publish(event)

    verify(exactly = 1) { responder(eq(event)) }

    subscriptions.forEach { it.dispose() }
    eventSource.publish(TestSingletonEvent)

    verify(exactly = 1) { responder(any()) }

    eventSource.publish(differentEvent)

    verify(exactly = 1) { responder(any()) }
  }

  @Test
  fun `should delegate call the event subscriber for event`() {
    val eventSubscriber = eventSource.respond(event, responder)

    assertThat(eventSubscriber).isInstanceOf(EventSubscriber::class.java)
    assertThat(eventSubscriber.subscriptions).hasSize(1)
  }

  @Test
  fun `should accept the responder for event`() {
    Assertions.assertThat(subscriber.respond(responder)).isEqualTo(subscriber)

    eventSource.publish(event)

    verify(exactly = 1) { responder(eq(event)) }

    subscriptions.forEach { it.dispose() }
    eventSource.publish(TestSingletonEvent)

    verify(exactly = 1) { responder(any()) }

    eventSource.publish(differentEvent)

    verify(exactly = 1) { responder(any()) }
  }

  @Test
  fun `should delegate call from action event subscriber for event`() {
    val eventSubscriber = actionSubscriber.respond(event, noParamResponder)

    assertThat(eventSubscriber).isEqualTo(subscriber)
    assertThat(subscriptions).hasSize(1)
  }

  @Test
  fun `should delegate call from action event subscriber for singleton event`() {
    val eventSubscriber = actionSubscriber.respond(event, responder)

    assertThat(eventSubscriber).isEqualTo(subscriber)
    assertThat(subscriptions).hasSize(1)
  }

  @Test
  fun `should delegate call from action event subscriber for singleton event with no param`() {
    val eventSubscriber = actionSubscriber.respond(responder)

    assertThat(eventSubscriber).isEqualTo(subscriber)
    assertThat(subscriptions).hasSize(1)
  }
}
