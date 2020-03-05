package challenge.news_management_system.test.acceptance

import challenge.news_management_system.test.repository.CategoryTestRepository
import challenge.news_management_system.test.repository.NewsTestRepository
import challenge.news_management_system.test.service.Resettable
import io.cucumber.java8.En

/**
 * It needs to be a Glue to make cucumber could scan it out. That's why it implements the [En]
 */
class StartupSpringInCucumber(
        private val resettables: List<Resettable>,
        private val newsTestRepository: NewsTestRepository,
        private val categoryTestRepository: CategoryTestRepository
) : AbstractAcceptanceTest(), En {
    init {
        Before(0) { _ ->
            cleanupReposities()
            resetAllResettables()
        }
    }

    private fun resetAllResettables() {
        resettables.forEach {
            it.reset()
        }
    }

    private fun cleanupReposities() {
        categoryTestRepository.deleteAllInBatch()
        newsTestRepository.deleteAllInBatch()
    }
}
