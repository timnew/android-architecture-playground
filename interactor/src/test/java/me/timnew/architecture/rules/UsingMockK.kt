package me.timnew.architecture.rules

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import org.junit.rules.ExternalResource

class UsingMockK(private val testSuite: Any) : ExternalResource() {
  override fun before() {
    MockKAnnotations.init(testSuite)
  }

  override fun after() {
    unmockkAll()
  }
}
