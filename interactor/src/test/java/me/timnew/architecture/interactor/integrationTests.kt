package me.timnew.architecture.interactor

import io.mockk.Ordering
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class TestPresenter {
  fun renderData(value: Int) {
    println("Render: $value")
  }
}

class UseSubscriptionAsTest {
  class TestInteractor(
    private val dataSource: Observable<Int>,
    private val presenter: TestPresenter
  ) : Interactor() {
    override fun subscription(): Iterable<Disposable> =
      subscriptionAs { subscription ->
        subscription has
          dataSource
            .filter { it > 0 }
            .map { it * it }
            .subscribe(presenter::renderData)
      }
  }

  private lateinit var presenter: TestPresenter
  private lateinit var interactor: Interactor

  @Before
  fun setUp() {
    presenter = spyk()
  }

  @After
  fun tearDown() {
    interactor.dispose()
  }

  @Test
  fun `should run`() {
    interactor = TestInteractor(
      dataSource = Observable.fromArray(-2, -1, 0, 1, 2, 3),
      presenter = presenter
    )

    interactor.start()

    verify(Ordering.SEQUENCE) {
      presenter.renderData(1)
      presenter.renderData(4)
      presenter.renderData(9)
    }
  }

  @Test
  fun `should stop`() {
    interactor = TestInteractor(
      dataSource = Observable.intervalRange(1, 5, 0, 20, TimeUnit.MILLISECONDS).map(Long::toInt),
      presenter = presenter
    )

    interactor.start()

    Thread.sleep(25)

    interactor.stop()

    verify(Ordering.SEQUENCE) {
      presenter.renderData(1)
    }
  }
}

class UseIteratorTest {
  class TestInteractor(
    private val dataSource: Observable<Int>,
    private val presenter: TestPresenter
  ) : Interactor() {
    override fun subscription(): Iterable<Disposable> =
      iterator {
        yield(
          dataSource
            .filter { it > 0 }
            .map { it * it }
            .subscribe(presenter::renderData)
        )
      }
        .asSequence().toList() // Convert Iterator to Iterable
  }

  private lateinit var presenter: TestPresenter
  private lateinit var interactor: Interactor

  @Before
  fun setUp() {
    presenter = spyk()
  }

  @After
  fun tearDown() {
    interactor.dispose()
  }

  @Test
  fun `should run`() {
    interactor = TestInteractor(
      dataSource = Observable.fromArray(-2, -1, 0, 1, 2, 3),
      presenter = presenter
    )

    interactor.start()

    verify(Ordering.SEQUENCE) {
      presenter.renderData(1)
      presenter.renderData(4)
      presenter.renderData(9)
    }
  }

  @Test
  fun `should stop`() {
    interactor = TestInteractor(
      dataSource = Observable.intervalRange(1, 5, 0, 20, TimeUnit.MILLISECONDS).map(Long::toInt),
      presenter = presenter
    )

    interactor.start()

    Thread.sleep(25)

    interactor.stop()

    verify(Ordering.SEQUENCE) {
      presenter.renderData(1)
    }
  }
}
