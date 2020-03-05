package challenge.news_management_system.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Clock

@Configuration
@Profile("!test")
class ClockConfig {
    @Bean
    fun createClock(): Clock = Clock.systemUTC()
}