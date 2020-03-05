package challenge.news_management_system.test.acceptance.service

import challenge.news_management_system.`interface`.model.AssignCategoriesToNewsRequestDto
import challenge.news_management_system.`interface`.model.CreateNewsRequestDto
import challenge.news_management_system.`interface`.model.NewsDto
import challenge.news_management_system.`interface`.model.UpdateNewsRequestDto
import io.cucumber.spring.CucumberTestContext
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be equal to`
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Scope
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@Scope(CucumberTestContext.SCOPE_CUCUMBER_GLUE)
class NewsTestService(
        private val restTemplate: TestRestTemplate,
        @LocalServerPort
        private val port: Int
) {
    private val newsResourcePath = "http://localhost:$port/api/news/"

    fun createNews(createNewsRequestDto: CreateNewsRequestDto) {
        val responseEntity = restTemplate.postForEntity(newsResourcePath, createNewsRequestDto, Unit::class.java)
        responseEntity.statusCode `should be` HttpStatus.CREATED

        val locationHeaders = responseEntity.headers["Location"]!!
        locationHeaders.size `should be equal to` 1
        val newCreatedNewsLocation = locationHeaders.first()
        newCreatedNewsLocation `should be equal to` "$newsResourcePath${createNewsRequestDto.slug}"

        val newCreatedNews = restTemplate.getForObject(newCreatedNewsLocation, NewsDto::class.java)
        newCreatedNews `should not be equal to` null
    }

    fun createNewsWithValidationError(createNewsRequestDto: CreateNewsRequestDto) {
        createNewsWithExpectedHttpStatus(
                createNewsRequestDto,
                expectedHttpStatus = HttpStatus.BAD_REQUEST
        )
    }

    fun createNewsWithConflictError(createNewsRequestDto: CreateNewsRequestDto) {
        createNewsWithExpectedHttpStatus(
                createNewsRequestDto,
                expectedHttpStatus = HttpStatus.CONFLICT
        )
    }

    fun findAllNews(): List<NewsDto> {
        return restTemplate.exchange(
                newsResourcePath,
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<NewsDto>>() {}
        ).body!!
    }

    fun findNews(slug: String): NewsDto? {
        return restTemplate.getForObject("$newsResourcePath$slug", NewsDto::class.java)
    }

    fun updateContent(slug: String, updateNewsRequestDto: UpdateNewsRequestDto) {
        updateContentWithExpectedHttpStatus(slug, updateNewsRequestDto, expectedHttpStatus = HttpStatus.OK)
    }

    fun updateContentWithNotFoundError(slug: String, updateNewsRequestDto: UpdateNewsRequestDto) {
        updateContentWithExpectedHttpStatus(slug, updateNewsRequestDto, expectedHttpStatus = HttpStatus.NOT_FOUND)
    }

    fun assignCategories(slug: String, categories: List<String>) {
        val assignCategoriesToNewsRequestDto = AssignCategoriesToNewsRequestDto(
                categories = categories
        )

        val responseEntity = restTemplate.postForEntity("$newsResourcePath$slug/categories/", assignCategoriesToNewsRequestDto, Unit::class.java)
        responseEntity.statusCode `should be equal to` HttpStatus.OK
    }

    private fun createNewsWithExpectedHttpStatus(createNewsRequestDto: CreateNewsRequestDto, expectedHttpStatus: HttpStatus) {
        val responseEntity = restTemplate.postForEntity(newsResourcePath, createNewsRequestDto, String::class.java)
        responseEntity.statusCode `should be` expectedHttpStatus
    }

    private fun updateContentWithExpectedHttpStatus(slug: String, updateNewsRequestDto: UpdateNewsRequestDto, expectedHttpStatus: HttpStatus) {
        val responseEntity = restTemplate.exchange(
                "$newsResourcePath$slug",
                HttpMethod.PATCH,
                HttpEntity(updateNewsRequestDto),
                Unit::class.java
        )
        responseEntity.statusCode `should be equal to` expectedHttpStatus
    }
}