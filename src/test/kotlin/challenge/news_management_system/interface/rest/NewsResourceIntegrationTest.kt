package challenge.news_management_system.`interface`.rest

import challenge.news_management_system.`interface`.model.CreateNewsRequestDto
import challenge.news_management_system.`interface`.model.UpdateNewsRequestDto
import challenge.news_management_system.application.exception.ResourceAlreadyExistException
import challenge.news_management_system.application.exception.ResourceNotFoundException
import challenge.news_management_system.application.service.NewsService
import challenge.news_management_system.domain.model.News
import challenge.news_management_system.domain.model.NewsTestBuilder
import challenge.news_management_system.test.util.toIso8601Format
import challenge.news_management_system.test.util.toJson
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.any
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.StringEndsWith.endsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.streams.asStream

@WebMvcTest(NewsResource::class)
class NewsResourceIntegrationTest {
    @MockBean
    private lateinit var newsService: NewsService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should able to create new news`() {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = "any caption",
                slug = "any-slug",
                content = "any content"
        )

        // when
        mvc.perform(post("/api/news/")
                .content(createNewsRequest.toJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated)
                .andExpect(header().string(LOCATION, endsWith("/api/news/${createNewsRequest.slug}")))

        // then
        argumentCaptor<News>().apply {
            verify(newsService)
                    .createNews(capture())

            val newsToBeCreated = firstValue
            newsToBeCreated.caption `should be equal to` createNewsRequest.caption
            newsToBeCreated.slug `should be equal to` createNewsRequest.slug
            newsToBeCreated.content `should be equal to` createNewsRequest.content
        }
    }

    @ParameterizedTest(name = "{index} => when caption = \"{0}\"")
    @ValueSource(strings = [
        "{null}",
        "",
        " "
    ])
    fun `should throw bad request error because of missing caption when creating news`(caption: String?) {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = if (caption == "{null}") null else caption,
                slug = "any-slug",
                content = "any content"
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @ParameterizedTest(name = "{index} => when slug = \"{0}\"")
    @ValueSource(strings = [
        "{null}",
        "",
        " "
    ])
    fun `should throw bad request error because of missing slug when creating news`(slug: String?) {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = "any caption",
                slug = if (slug == "{null}") null else slug,
                content = "any content"
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @ParameterizedTest(name = "{index} => when content = \"{0}\"")
    @ValueSource(strings = [
        "{null}",
        "",
        " "
    ])
    fun `should throw bad request error because of missing content when creating news`(content: String?) {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = "any caption",
                slug = "any-slug",
                content = if (content == "{null}") null else content
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @Test
    fun `should throw conflict error because of resource already exist when creating news`() {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = "any caption",
                slug = "any-slug",
                content = "any content"
        )
        whenever(newsService.createNews(any()))
                .thenThrow(ResourceAlreadyExistException::class.java)

        // when
        shouldReceiveConflictErrorWhenCreatingNews(createNewsRequest)
    }

    @Test
    fun `should throw bad request error when creating news because the length of the caption longer than 80 characters`() {
        // given
        val captionLongerThan80Chars = "a".repeat(81)
        val createNewsRequest = CreateNewsRequestDto(
                caption = captionLongerThan80Chars,
                slug = "any-slug",
                content = "any content"
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @Test
    fun `should throw bad request error when creating news because the length of the slug longer than 80 characters`() {
        // given
        val slugLongerThan80Chars = "a".repeat(81)
        val createNewsRequest = CreateNewsRequestDto(
                caption = "amy caption",
                slug = slugLongerThan80Chars,
                content = "any content"
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @ParameterizedTest(name = "{index} => when slug = \"{0}\"")
    @MethodSource("invalidSlugProvider")
    fun `should should throw bad request error when creating news because not only contains alphanumeric and hyphen`(slug: String) {
        // given
        val createNewsRequest = CreateNewsRequestDto(
                caption = "any caption",
                slug = slug,
                content = "any content"
        )

        // when
        shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequest)
    }

    @Test
    fun `should able to retrieve all news`() {
        // given
        val listOfNews = listOf(
                NewsTestBuilder.default().copy(
                        caption = "News 1",
                        slug = "news-1",
                        content = "content 1",
                        createdTime = LocalDateTime.parse("2020-01-01T00:00:01.000")
                ),
                NewsTestBuilder.default().copy(
                        caption = "News 2",
                        slug = "news-2",
                        content = "content 2",
                        createdTime = LocalDateTime.parse("2020-01-01T00:00:00.000")
                )
        )
        whenever(newsService.findAllNews())
                .thenReturn(listOfNews)

        // when
        mvc.perform(get("/api/news/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Collection<*>>(2)))
                .andExpect(jsonPath("$[0].caption", `is`(listOfNews[0].caption)))
                .andExpect(jsonPath("$[0].slug", `is`(listOfNews[0].slug)))
                .andExpect(jsonPath("$[0].content", `is`(listOfNews[0].content)))
                .andExpect(jsonPath("$[0].createdTime", `is`(listOfNews[0].createdTime.toIso8601Format())))
                .andExpect(jsonPath("$[0].updatedTime", `is`(listOfNews[0].updatedTime.toIso8601Format())))
                .andExpect(jsonPath("$[1].caption", `is`(listOfNews[1].caption)))
                .andExpect(jsonPath("$[1].slug", `is`(listOfNews[1].slug)))
                .andExpect(jsonPath("$[1].content", `is`(listOfNews[1].content)))
                .andExpect(jsonPath("$[1].createdTime", `is`(listOfNews[1].createdTime.toIso8601Format())))
                .andExpect(jsonPath("$[1].updatedTime", `is`(listOfNews[1].updatedTime.toIso8601Format())))
    }

    @Test
    fun `should receive empty list if no news exist`() {
        // given
        whenever(newsService.findAllNews())
                .thenReturn(emptyList())

        // when
        mvc.perform(get("/api/news/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Collection<*>>(0)))
    }

    @Test
    fun `should return 404 when the news not found`() {
        mvc.perform(get("/api/news/NonExistNews"))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should able to retrieve a news by slug`() {
        // given
        val existingNews = NewsTestBuilder.default()
        whenever(newsService.findBySlug(existingNews.slug))
                .thenReturn(existingNews)

        // when
        mvc.perform(get("/api/news/${existingNews.slug}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.caption", `is`(existingNews.caption)))
                .andExpect(jsonPath("$.slug", `is`(existingNews.slug)))
                .andExpect(jsonPath("$.content", `is`(existingNews.content)))
                .andExpect(jsonPath("$.createdTime", `is`(existingNews.createdTime.toIso8601Format())))
                .andExpect(jsonPath("$.updatedTime", `is`(existingNews.updatedTime.toIso8601Format())))
    }

    @Test
    fun `should return 404 when the updating non-exist news`() {
        // given
        val slug = "any-non-exist-news"
        val updateNewsRequestDto = UpdateNewsRequestDto(
                newContent = "any content                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              "
        )
        whenever(newsService.updateContent(any(), any()))
                .thenThrow(ResourceNotFoundException::class.java)

        // when
        mvc.perform(patch("/api/news/$slug")
                .content(updateNewsRequestDto.toJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @ParameterizedTest(name = "{index} => when updateContent = \"{0}\"")
    @ValueSource(strings = [
        "{null}",
        "",
        " "
    ])
    fun `should not able to update the content to empty string or null`(updateContent: String?) {
        // given
        val slug = "any-slug"
        val updateNewsRequestDto = UpdateNewsRequestDto(
                newContent = if (updateContent == "{null}") null else updateContent
        )

        // when
        mvc.perform(patch("/api/news/$slug")
                .content(updateNewsRequestDto.toJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `should able to update the content`() {
        // given
        val slug = "any-slug"
        val updateNewsRequestDto = UpdateNewsRequestDto(
                newContent = "New Content"
        )

        // when
        mvc.perform(patch("/api/news/$slug")
                .content(updateNewsRequestDto.toJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)

        // then
        verify(newsService)
                .updateContent(eq(slug), eq(updateNewsRequestDto.newContent!!))
    }

    private fun shouldReceiveBadRequestErrorWhenCreatingNews(createNewsRequestDto: CreateNewsRequestDto) {
        shouldReceiveErrorWhenCreatingNews(
                createNewsRequestDto,
                expectedStatusResultMatcher = status().isBadRequest
        )
    }

    private fun shouldReceiveConflictErrorWhenCreatingNews(createNewsRequestDto: CreateNewsRequestDto) {
        shouldReceiveErrorWhenCreatingNews(
                createNewsRequestDto,
                expectedStatusResultMatcher = status().isConflict
        )
    }

    private fun shouldReceiveErrorWhenCreatingNews(createNewsRequestDto: CreateNewsRequestDto, expectedStatusResultMatcher: ResultMatcher) {
        mvc.perform(post("/api/news/")
                .content(createNewsRequestDto.toJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatusResultMatcher)
    }

    companion object {
        @JvmStatic
        private fun invalidSlugProvider(): Stream<Arguments> {
            // https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
            return (0..127)
                    .filter { !isAlphanumericOrHyphen(it) }
                    .map {
                        val char = it.toChar()
                        Arguments.of(char.toString())
                    }.asSequence().asStream()
        }

        private fun isAlphanumericOrHyphen(charIntValue: Int): Boolean {
            val isHyphen = charIntValue == 45
            val isDigit = charIntValue in 48..57
            val isUpperCaseAlphanumeric = charIntValue in 65..90
            val isLowerCaseAlphanumeric = charIntValue in 97..122
            return isHyphen || isDigit || isUpperCaseAlphanumeric || isLowerCaseAlphanumeric
        }
    }
}