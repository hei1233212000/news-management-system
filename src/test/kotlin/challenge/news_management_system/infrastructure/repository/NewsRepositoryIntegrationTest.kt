package challenge.news_management_system.infrastructure.repository

import challenge.news_management_system.domain.model.NewsTestBuilder
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be equal to`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDateTime

@DataJpaTest
class NewsRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var newsRepository: NewsRepository

    @Test
    fun `should able to find all news which is ordered by its time in descending order`() {
        // given
        val listOfNewsToBeCreated = listOf(
                NewsTestBuilder.defaultForNewPersist().copy(
                        caption = "News 2",
                        slug = "slug-2",
                        content = "content 2",
                        createdTime = LocalDateTime.parse("2020-01-01T00:00:02.000"),
                        updatedTime = LocalDateTime.parse("2020-01-01T00:00:02.000")
                ),
                NewsTestBuilder.defaultForNewPersist().copy(
                        caption = "News 3",
                        slug = "slug-3",
                        content = "content 3",
                        createdTime = LocalDateTime.parse("2020-01-01T00:00:03.000"),
                        updatedTime = LocalDateTime.parse("2020-01-01T00:00:03.000")
                ),
                NewsTestBuilder.defaultForNewPersist().copy(
                        caption = "News 1",
                        slug = "slug-1",
                        content = "content 1",
                        createdTime = LocalDateTime.parse("2020-01-01T00:00:01.000"),
                        updatedTime = LocalDateTime.parse("2020-01-01T00:00:01.000")
                )
        )
        listOfNewsToBeCreated.forEach { news ->
            entityManager.persist(news)
        }

        // when
        val listOfNews = newsRepository.findAllByOrderByCreatedTimeDesc()
        listOfNews.size `should be equal to` 3

        // then
        listOfNews.forEachIndexed { index, news ->
            val newsNumber = 3 - index
            news.caption `should be equal to` "News $newsNumber"
        }
    }

    @Test
    fun `should return null if the news does not exist`() {
        newsRepository.findBySlug("news does not exist") `should be equal to` null
    }

    @Test
    fun `should able to find a news by slug`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        entityManager.persist(existingNews)

        // when
        val news = newsRepository.findBySlug(existingNews.slug)

        // then
        news `should not be equal to` null
        news `should be equal to` existingNews
    }

    @Test
    fun `should able to find a news by caption`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        entityManager.persist(existingNews)

        // when
        val news = newsRepository.findByCaption(existingNews.caption)

        // then
        news `should not be equal to` null
        news `should be equal to` existingNews
    }
}