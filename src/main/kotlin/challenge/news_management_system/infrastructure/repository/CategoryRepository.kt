package challenge.news_management_system.infrastructure.repository

import challenge.news_management_system.domain.model.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?

    fun findByNameIn(names: Set<String>): Set<Category>
}
