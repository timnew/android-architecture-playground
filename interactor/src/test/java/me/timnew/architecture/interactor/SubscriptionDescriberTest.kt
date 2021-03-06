package me.timnew.architecture.interactor

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.junit.Before
import org.junit.Test

class SubscriptionDescriberTest {
    private val knownSubscription: MutableList<Disposable> = mutableListOf()

    private fun createSubscription() = CompositeDisposable().also { knownSubscription.add(it) }

    @Before
    fun setUp() {
        knownSubscription.clear()
    }

    @Test
    fun `should create subscription`() {
        val subscription = subscriptionAs {}

        ObjectAssert(subscription).isInstanceOf(Iterable::class.java)
    }

    @Test
    fun `should populate members`() {
        val subscription = subscriptionAs {
            it has createSubscription()
            it has createSubscription()
        }

        assertThat(subscription.toList()).containsExactlyElementsOf(knownSubscription)
    }

    @Test
    fun `should populate iterable members`() {
        val subscription = subscriptionAs {
            it has listOf(
                createSubscription(),
                createSubscription()
            ).asIterable()
        }

        assertThat(subscription.toList()).containsExactlyElementsOf(knownSubscription)
    }

    @Test
    fun `should populate iterator`() {
        val subscription = subscriptionAs {
            it has iterator {
                yield(createSubscription())
                yield(createSubscription())
            }
        }

        assertThat(subscription.toList()).containsExactlyElementsOf(knownSubscription)
    }
}
