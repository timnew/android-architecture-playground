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

class ActionEventSubscriberTest {
  private lateinit var subscriptions: MutableList<Disposable>
  private lateinit var eventSource: EventSource
  private lateinit var subscriber: ActionEventSubscriber<TestBehaviorEvent, TestAction>
  private lateinit var parent: EventSubscriber

  @RelaxedMockK
  lateinit var noParamResponder: NoParamResponder

  @RelaxedMockK
  lateinit var responder: Responder<TestBehaviorEvent>

  private val action = TestAction.SendToNetwork
  private val event = TestBehaviorEvent(action = action, subject = "data")
  private val differentEvent = TestBehaviorEvent(action = TestAction.WriteToFile, subject = "data")

  @Before
  fun setUp() {
    MockKAnnotations.init(this)

    subscriptions = mutableListOf()
    eventSource = EventSource()
    parent = spyk(EventSubscriber(eventSource, subscriptions))
    subscriber =
      spyk(ActionEventSubscriber(parent, eventSource.allEvents.ofType(TestBehaviorEvent::class.java)))
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should be iterable`() {
    assertThat(subscriber).isInstanceOf(Iterable::class.java)
  }

  @Test
  fun `should delegate call the action event subscriber from event source`() {
    val eventSubscriber = eventSource.respondAction(action, responder)

    Assertions.assertThat(eventSubscriber).isInstanceOf(ActionEventSubscriber::class.java)
    Assertions.assertThat(eventSubscriber.parent.subscriptions).hasSize(1)
  }

  @Test
  fun `should delegate call the action event subscriber from event subscriber`() {
    val eventSubscriber = parent.respondAction(action, responder)

    Assertions.assertThat(eventSubscriber).isInstanceOf(ActionEventSubscriber::class.java)
    Assertions.assertThat(subscriptions).hasSize(1)
  }

  @Test
  fun `should accept the non param responder for singleton event`() {
    Assertions.assertThat(subscriber.respondAction(action, responder)).isEqualTo(subscriber)

    eventSource.publish(event)

    verify(exactly = 1) { responder(eq(event)) }

    subscriptions.forEach { it.dispose() }
    eventSource.publish(event)

    verify(exactly = 1) { responder(any()) }

    eventSource.publish(differentEvent)

    verify(exactly = 1) { responder(any()) }
  }

  @Test
  fun `should delegate event back to parent`() {
    assertThat(subscriber.respond(responder)).isEqualTo(parent)

    assertThat(subscriptions).hasSize(1)
  }

  @Test
  fun `should delegate singleton event with no param back to parent`() {
    assertThat(subscriber.respond(event, noParamResponder)).isEqualTo(parent)

    assertThat(subscriptions).hasSize(1)
  }

  @Test
  fun `should delegate singleton event back to parent`() {
    assertThat(subscriber.respond(event, responder)).isEqualTo(parent)

    assertThat(subscriptions).hasSize(1)
  }
}
