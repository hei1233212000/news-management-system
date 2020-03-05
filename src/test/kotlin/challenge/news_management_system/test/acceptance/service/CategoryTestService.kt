package challenge.news_management_system.test.acceptance.service

import challenge.news_management_system.`interface`.model.AssignParentCategoryRequestDto
import challenge.news_management_system.`interface`.model.CategoryDto
import challenge.news_management_system.`interface`.model.CreateCategoryRequestDto
import io.cucumber.spring.CucumberTestContext
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be equal to`
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@Scope(CucumberTestContext.SCOPE_CUCUMBER_GLUE)
class CategoryTestService(
        private val restTemplate: TestRestTemplate,
        @LocalServerPort
        private val port: Int
) {
    private val categoryResourcePath = "http://localhost:$port/api/categories/"

    fun createCategory(createCategoryRequestDto: CreateCategoryRequestDto) {
        val responseEntity = restTemplate.postForEntity(categoryResourcePath, createCategoryRequestDto, Unit::class.java)
        responseEntity.statusCode `should be` HttpStatus.CREATED

        val locationHeaders = responseEntity.headers["Location"]!!
        locationHeaders.size `should be equal to` 1
        val newCreatedCategoryLocation = locationHeaders.first()
        newCreatedCategoryLocation `should be equal to` "$categoryResourcePath${createCategoryRequestDto.name}"

        val newCreatedCategory = restTemplate.getForObject(newCreatedCategoryLocation, CategoryDto::class.java)
        newCreatedCategory `should not be equal to` null
    }

    fun findCategory(name: String): CategoryDto? {
        return restTemplate.getForObject("$categoryResourcePath$name", CategoryDto::class.java)
    }

    fun linkCategories(categoryName: String, subCategoryName: String) {
        val assignParentCategoryRequestDto = AssignParentCategoryRequestDto(
                parentName = categoryName
        )
        val responseEntity = restTemplate.exchange(
                "$categoryResourcePath$subCategoryName",
                HttpMethod.PATCH,
                HttpEntity(assignParentCategoryRequestDto),
                Unit::class.java
        )
        responseEntity.statusCode `should be equal to` HttpStatus.OK
    }
}
