package challenge.news_management_system.test.repository

import challenge.news_management_system.domain.model.News
import org.springframework.data.jpa.repository.JpaRepository

interface NewsTestRepository : JpaRepository<News, Long> {
    fun findBySlug(slug: String): News?
}