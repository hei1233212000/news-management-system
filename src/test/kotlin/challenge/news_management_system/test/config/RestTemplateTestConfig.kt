package challenge.news_management_system.test.config

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory

@Configuration
@Profile("test")
class RestTemplateTestConfig {
    @Bean
    fun restTemplate(): TestRestTemplate {
        val requestFactory = HttpComponentsClientHttpRequestFactory()
        val oneSecond = 1000
        requestFactory.setConnectTimeout(oneSecond)
        requestFactory.setReadTimeout(oneSecond)

        val restTemplate = TestRestTemplate()
        restTemplate.restTemplate.requestFactory = requestFactory
        return restTemplate
    }
}
