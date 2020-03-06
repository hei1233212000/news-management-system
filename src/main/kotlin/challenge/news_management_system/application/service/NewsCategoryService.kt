package challenge.news_management_system.application.service

import challenge.news_management_system.infrastructure.repository.CategoryRepository
import challenge.news_management_system.infrastructure.repository.NewsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NewsCategoryService(
        private val newsRepository: NewsRepository,
        private val categoryRepository: CategoryRepository
) {
    fun assignCategories(slug: String, categoryNames: List<String>) {
        val news = newsRepository.findBySlug(slug)!!
        val categories = categoryRepository.findByNameIn(categoryNames.toSet())
        news.categories.addAll(categories)
        categoryRepository.saveAll(categories)
    }
}
