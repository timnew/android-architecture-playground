package me.timnew.architecture.interactor

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import io.reactivex.disposables.Disposable
import me.timnew.architecture.rules.UsingMockK
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test

class InteractorTest {
    @Rule
    @JvmField
    val usingMockK = UsingMockK(this)

    @RelaxedMockK
    lateinit var disposable: Disposable

    @RelaxedMockK
    lateinit var anotherDisposable: Disposable

    @SpyK
    var interactor: Interactor = object : Interactor() {
        override fun subscription(): Iterator<Disposable> = iterator {
            yield(disposable)
            yield(anotherDisposable)
        }
    }

    @Test
    fun `should populate isRunning`() {
        assertThat(interactor.isRunning).isFalse()

        interactor.start()

        assertThat(interactor.isRunning).isTrue()

        interactor.stop()

        assertThat(interactor.isRunning).isFalse()
    }

    @Test
    fun `should subscribe the source when start`() {
        verify(exactly = 0) { interactor["subscription"]() }
        interactor.start()
        verify(exactly = 1) { interactor["subscription"]() }
        interactor.start()
        verify(exactly = 2) { interactor["subscription"]() }
    }

    @Test
    fun `should dispose subscription when stop`() {
        interactor.start()

        verify(exactly = 0) { disposable.dispose() }
        verify(exactly = 0) { anotherDisposable.dispose() }

        interactor.stop()

        verify(exactly = 1) { disposable.dispose() }
        verify(exactly = 1) { anotherDisposable.dispose() }

        interactor.stop()

        verify(exactly = 1) { disposable.dispose() }
        verify(exactly = 1) { anotherDisposable.dispose() }
    }

    @Test
    fun `should stop before start again when interactor is running`() {
        interactor.start()

        verify(exactly = 0) { interactor.stop() }

        interactor.start()

        verify(exactly = 1) { interactor.stop() }
    }

    @Test
    fun `should dispose should stop`() {
        interactor.start()

        assertThat(interactor.isDisposed).isFalse()

        interactor.dispose()

        assertThat(interactor.isRunning).isFalse()
        verify(exactly = 1) { disposable.dispose() }
        verify(exactly = 1) { anotherDisposable.dispose() }
        verify(exactly = 1) { interactor.stop() }
    }

    @Test
    fun `should fail if start after disposed`() {
        interactor.dispose()

        assertThatThrownBy { interactor.start() }.hasMessage("Interactor has been disposed")
    }
}
