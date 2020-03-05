package challenge.news_management_system.test.config

import challenge.news_management_system.test.util.MockClock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class ClockTestConfig {
    @Bean
    fun mockClock(): MockClock = MockClock()
}