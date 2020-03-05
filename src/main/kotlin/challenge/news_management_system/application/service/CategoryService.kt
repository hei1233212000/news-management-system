package challenge.news_management_system.application.service

import challenge.news_management_system.domain.model.Category
import challenge.news_management_system.infrastructure.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryService(
        private val categoryRepository: CategoryRepository
) {
    fun createCategory(category: Category) {
        categoryRepository.save(category)
    }

    @Transactional(readOnly = true)
    fun findByName(name: String): Category? {
        return categoryRepository.findByName(name)
    }

    fun linkCategories(name: String, parentCategoryName: String) {
        val subCategory = categoryRepository.findByName(name)!!
        val parentCategory = categoryRepository.findByName(parentCategoryName)!!
        subCategory.parent = parentCategory
        categoryRepository.save(subCategory)
    }
}
