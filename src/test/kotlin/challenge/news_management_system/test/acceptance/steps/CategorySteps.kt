package challenge.news_management_system.test.acceptance.steps

import challenge.news_management_system.`interface`.model.CreateCategoryRequestDto
import challenge.news_management_system.domain.model.Category
import challenge.news_management_system.test.acceptance.service.CategoryTestService
import challenge.news_management_system.test.repository.CategoryTestRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.`should not be`

class CategorySteps(
        private val categoryTestRepository: CategoryTestRepository,
        private val categoryTestService: CategoryTestService
) : En {
    init {
        Given("A category {string} does not exist") { name: String ->
            val category = categoryTestRepository.findByName(name)
            category `should be` null
        }

        Given("the listed categories already exist in the repository:") { dataTable: DataTable ->
            dataTable.asList()
                    .forEach { name ->
                        val category = Category(
                                name = name
                        )
                        categoryTestRepository.save(category)
                    }
        }

        When("I create a category {string}") { name: String ->
            val createCategoryRequestDto = CreateCategoryRequestDto(
                    name = name
            )
            categoryTestService.createCategory(createCategoryRequestDto)
        }

        When("I assign {string} as a sub-category of {string}") { subCategoryName: String, categoryName: String ->
            categoryTestService.linkCategories(categoryName, subCategoryName)
        }

        Then("I could see the category {string} in the repository") { name: String ->
            val category = categoryTestService.findCategory(name)
            category `should not be` null
        }

        Then("I could see the category {string} has the following sub-categories:") { name: String, dataTable: DataTable ->
            val category = categoryTestService.findCategory(name)
            val actualSubCategoryNames = category!!.subCategories.map { it.name }
            val expectedSubCategoryNames = dataTable.asList()
            actualSubCategoryNames `should contain all` expectedSubCategoryNames
        }
    }
}