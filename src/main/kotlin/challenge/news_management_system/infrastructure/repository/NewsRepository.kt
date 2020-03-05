package challenge.news_management_system.infrastructure.repository

import challenge.news_management_system.domain.model.News
import org.springframework.data.jpa.repository.JpaRepository

interface NewsRepository : JpaRepository<News, Long> {
    fun findAllByOrderByCreatedTimeDesc(): List<News>

    fun findByCaption(caption: String): News?

    fun findBySlug(slug: String): News?
}
