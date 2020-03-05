package challenge.news_management_system.`interface`.rest

import challenge.news_management_system.`interface`.model.AssignParentCategoryRequestDto
import challenge.news_management_system.`interface`.model.CategoryDto
import challenge.news_management_system.`interface`.model.CreateCategoryRequestDto
import challenge.news_management_system.application.service.CategoryService
import challenge.news_management_system.domain.model.Category
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("api/categories/")
class CategoryResource(
        private val categoryService: CategoryService
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Create new Category")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "201",
                description = "The category is created successfully",
                headers = [Header(name = "Location", description = "The resource path to new created category")]
        ),
        ApiResponse(
                responseCode = "400",
                description = "Validation error"
        )
    ])
    fun createCategory(
            @Valid @RequestBody createCategoryRequestDto: CreateCategoryRequestDto
    ): ResponseEntity<Unit> {
        val category = Category(
                name = createCategoryRequestDto.name!!
        )
        categoryService.createCategory(category)
        val newCategoryLocation = ServletUriComponentsBuilder
                .fromCurrentRequest().path("{name}").buildAndExpand(category.name)
                .toUri()

        return ResponseEntity.created(newCategoryLocation).build()
    }

    @GetMapping(path = ["{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Retrieve a category by slug")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "a category"
        ),
        ApiResponse(
                responseCode = "404",
                description = "The category does not exist"
        )
    ])
    fun findByName(@PathVariable("name") slug: String): ResponseEntity<CategoryDto> {
        val category = categoryService.findByName(slug)
        return category?.let {
            ResponseEntity.ok(it.toDto())
        } ?: ResponseEntity.notFound().build<CategoryDto>()
    }

    @PatchMapping(path = ["{name}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Assign this category to another category as a sub-category")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "the assignment is successfully completed"
        )
    ])
    fun linkCategories(
            @PathVariable("name") name: String,
            @Valid @RequestBody assignParentCategoryRequestDto: AssignParentCategoryRequestDto
    ): ResponseEntity<Unit> {
        categoryService.linkCategories(name, parentCategoryName = assignParentCategoryRequestDto.parentName!!)
        return ResponseEntity.ok().build()
    }

    private fun Category.toDto(): CategoryDto = CategoryDto(
            name = this.name,
            subCategories = this.subcategories.map { it.toDto() }
    )
}