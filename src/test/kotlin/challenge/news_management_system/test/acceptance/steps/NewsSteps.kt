package challenge.news_management_system.test.acceptance.steps

import challenge.news_management_system.`interface`.model.CreateNewsRequestDto
import challenge.news_management_system.`interface`.model.UpdateNewsRequestDto
import challenge.news_management_system.domain.model.News
import challenge.news_management_system.domain.model.NewsTestBuilder
import challenge.news_management_system.test.acceptance.service.NewsTestService
import challenge.news_management_system.test.repository.NewsTestRepository
import challenge.news_management_system.test.util.toFieldValueMap
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.`should not be`
import java.time.LocalDateTime

class NewsSteps(
        private val newsTestRepository: NewsTestRepository,
        private val newsTestService: NewsTestService
) : En {
    init {
        Given("A news {string} does not exist") { slug: String ->
            val news = newsTestRepository.findBySlug(slug)
            news `should be` null
        }

        Given("There are news in data repository as below:") { dataTable: DataTable ->
            val listOfNews = dataTable.toListOfNewsForPersist()
            newsTestRepository.saveAll(listOfNews)
        }

        Given("a news {string} already exist with following information:") { slug: String, dataTable: DataTable ->
            val news = dataTable.toNewsForPersist(slug)
            newsTestRepository.save(news)
        }

        When("I create a news with following details:") { dataTable: DataTable ->
            val createNewsRequest = dataTable.toCreateNewsRequestDto()
            newsTestService.createNews(createNewsRequest)
        }

        When("I create a news with following details will cause validation error:") { dataTable: DataTable ->
            val createNewsRequest = dataTable.toCreateNewsRequestDto()
            newsTestService.createNewsWithValidationError(createNewsRequest)
        }

        When("I create a news with following details will cause conflict error:") { dataTable: DataTable ->
            val createNewsRequest = dataTable.toCreateNewsRequestDto()
            newsTestService.createNewsWithConflictError(createNewsRequest)
        }

        When("I retrieve the list of news, I should see the following result:") { dataTable: DataTable ->
            val newsDtos = newsTestService.findAllNews()
            val expectedFieldValueMaps = dataTable.asMaps<String, String>(String::class.java, String::class.java)
            val actualFieldValueMaps = newsDtos.map { it.toFieldValueMap() }
            expectedFieldValueMaps.forEachIndexed { index, expectedFieldValueMap ->
                val actualFieldValueMap = actualFieldValueMaps[index]
                actualFieldValueMap `should contain all` expectedFieldValueMap
            }
        }

        When("I retrieve the news {string}, I should see the following result:") { slug: String, dataTable: DataTable ->
            val news = newsTestService.findNews(slug)
            news `should not be` null
            val actualFieldValueMap = news.toFieldValueMap()
            val expectedFieldValueMap = dataTable.toExpectedFieldValueMap()
            actualFieldValueMap `should contain all` expectedFieldValueMap
        }

        When("I update the {string} with following information:") { slug: String, dataTable: DataTable ->
            val updateNewsRequestDto = dataTable.toUpdateNewsRequestDto()
            newsTestService.updateContent(slug, updateNewsRequestDto)
        }

        When("I update the {string} with following information will cause resource not found error:") { slug: String, dataTable: DataTable ->
            val updateNewsRequestDto = dataTable.toUpdateNewsRequestDto()
            newsTestService.updateContentWithNotFoundError(slug, updateNewsRequestDto)
        }

        When("I assign the listed categories to news {string}:") { slug: String, dataTable: DataTable ->
            val categories = dataTable.asList()
            newsTestService.assignCategories(slug, categories)
        }

        Then("I could see the news {string} with following information:") { slug: String, dataTable: DataTable ->
            val news = newsTestService.findNews(slug)
            news `should not be` null

            val expectedFieldValueMap = dataTable.toExpectedFieldValueMap()
            val actualFieldValueMap = news.toFieldValueMap()
            actualFieldValueMap `should contain all` expectedFieldValueMap
        }

        Then("I could see the news {string} has the listed category labels:") { slug: String, dataTable: DataTable ->
            val news = newsTestService.findNews(slug)!!
            val expectedCategories = dataTable.asList()
            val actualCategories = news.categories
            actualCategories `should contain all` expectedCategories
        }
    }

    private fun DataTable.toCreateNewsRequestDto(): CreateNewsRequestDto {
        val fieldValueMap = this.asMap<String, String>(String::class.java, String::class.java)
        val caption = fieldValueMap["caption"] ?: error("caption should be existed")
        val slug = fieldValueMap["slug"] ?: error("slug should be existed")
        val content = fieldValueMap["content"] ?: error("content should be existed")
        return CreateNewsRequestDto(
                caption = caption,
                slug = slug,
                content = content
        )
    }

    private fun DataTable.toUpdateNewsRequestDto(): UpdateNewsRequestDto {
        val fieldValueMap = this.asMap<String, String>(String::class.java, String::class.java)
        val content = fieldValueMap["content"] ?: error("content should be existed")
        return UpdateNewsRequestDto(
                newContent = content
        )
    }

    private fun DataTable.toListOfNewsForPersist(): List<News> {
        val fieldValueMaps = this.asMaps<String, String>(String::class.java, String::class.java)
        return fieldValueMaps.map { fieldValueMap -> fieldValueMap.toNewsForPersist() }
    }

    private fun DataTable.toNewsForPersist(slug: String): News {
        val fieldValueMap = this.asMap<String, String>(String::class.java, String::class.java).toMutableMap()
        fieldValueMap["slug"] = slug
        return fieldValueMap.toNewsForPersist()
    }

    private fun Map<String, String>.toNewsForPersist(): News {
        val fieldValueMap = this
        val caption = fieldValueMap["caption"] ?: error("caption should be existed")
        val slug = fieldValueMap["slug"] ?: error("slug should be existed")
        val content = fieldValueMap["content"] ?: error("content should be existed")
        val createdTime = LocalDateTime.parse(fieldValueMap["createdTime"] ?: error("createdTime should be existed"))
        return NewsTestBuilder.defaultForNewPersist().copy(
                caption = caption,
                slug = slug,
                content = content,
                createdTime = createdTime
        )
    }

    private fun DataTable.toExpectedFieldValueMap(): Map<String, String> = this.asMap<String, String>(String::class.java, String::class.java)
}