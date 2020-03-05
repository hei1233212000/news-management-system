package challenge.news_management_system.test.repository

import challenge.news_management_system.domain.model.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryTestRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?
}