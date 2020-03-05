package challenge.news_management_system.domain.model

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDateTime
import javax.persistence.PersistenceException

@DataJpaTest
class NewsJpaIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `should have exception if createdTime is not provided`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist().copy(
                createdTime = null
        )

        // when
        invoking { entityManager.persist(existingNews) } `should throw` PersistenceException::class
    }

    @Test
    fun `should have exception if updatedTime is not provided`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist().copy(
                updatedTime = null
        )

        // when
        invoking { entityManager.persist(existingNews) } `should throw` PersistenceException::class
    }

    @Test
    fun `should not able to update caption, slug and createdTime`() {
        // given
        val originalCaption = "originalCaption"
        val originalSlug = "original-slug"
        val originalCreatedTime = LocalDateTime.now()
        val existingNews = NewsTestBuilder.defaultForNewPersist().copy(
                caption = originalCaption,
                slug = originalSlug,
                createdTime = originalCreatedTime
        )
        entityManager.persist(existingNews)
        existingNews.id `should not be equal to` null

        // when
        val newsWithCaptionUpdated = existingNews.copy(
                caption = "updated caption",
                slug = "updated-slug",
                content = "updated content",
                createdTime = originalCreatedTime.plusDays(1)
        )
        entityManager.merge(newsWithCaptionUpdated)
        entityManager.flush()
        entityManager.clear()

        // then
        val updatedNews = entityManager.find(News::class.java, existingNews.id)
        updatedNews.caption `should be equal to` originalCaption
        updatedNews.slug `should be equal to` originalSlug
        updatedNews.content `should be equal to` newsWithCaptionUpdated.content
        updatedNews.createdTime `should be equal to` originalCreatedTime
    }

    @Test
    fun `should not able to persist a news with the caption which is already exist`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        entityManager.persist(existingNews)

        // when
        val newsWithSameCaption = existingNews.copy(
                id = null,
                slug = existingNews.slug + "-another"
        )
        invoking { entityManager.persist(newsWithSameCaption) } `should throw` PersistenceException::class
    }

    @Test
    fun `should not able to persist a news with the slug which is already exist`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        entityManager.persist(existingNews)

        // when
        val newsWithSameSlug = existingNews.copy(
                id = null,
                caption = existingNews.caption + "-another"
        )
        invoking { entityManager.persist(newsWithSameSlug) } `should throw` PersistenceException::class
    }

    @Test
    fun `id should be generated after persist`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        existingNews.id `should be equal to` null

        // when
        entityManager.persist(existingNews)

        // then
        existingNews.id `should not be equal to` null
    }

    @Test
    fun `version should be generated after persist`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        existingNews.version `should be equal to` null

        // when
        entityManager.persist(existingNews)

        // then
        existingNews.version `should not be equal to` null
    }

    @Test
    fun `version should be increased after update`() {
        // given
        val existingNews = NewsTestBuilder.defaultForNewPersist()
        entityManager.persist(existingNews)
        val versionAfterPersist = existingNews.version
        versionAfterPersist `should not be equal to` null

        // when
        val newsToBeUpdated = existingNews.copy(
                content = existingNews.content + " updated"
        )
        val updatedNews = entityManager.merge(newsToBeUpdated)
        entityManager.flush()

        // then
        updatedNews.version `should be equal to` versionAfterPersist!! + 1
    }
}