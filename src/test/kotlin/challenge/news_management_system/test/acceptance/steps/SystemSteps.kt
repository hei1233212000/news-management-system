package challenge.news_management_system.test.acceptance.steps

import challenge.news_management_system.test.util.MockClock
import io.cucumber.java8.En
import java.time.LocalDateTime

class SystemSteps(
        private val clock: MockClock
) : En {
    init {
        Given("the server time is {string}") { dateTimeInString: String ->
            val time = LocalDateTime.parse(dateTimeInString)
            clock.setTime(time)
        }
    }
}