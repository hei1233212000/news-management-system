package challenge.news_management_system.`interface`.rest

import challenge.news_management_system.`interface`.model.AssignCategoriesToNewsRequestDto
import challenge.news_management_system.application.service.NewsCategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api/news/{slug}/categories")
class NewsCategoryResource(
        private val newsCategoryService: NewsCategoryService
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Assign categories to a news")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "the assignment is successfully completed"
        )
    ])
    fun assignCategories(
            @PathVariable("slug") slug: String,
            @Valid @RequestBody assignCategoriesToNewsRequestDto: AssignCategoriesToNewsRequestDto
    ): ResponseEntity<Unit> {
        newsCategoryService.assignCategories(
                slug = slug,
                categoryNames = assignCategoriesToNewsRequestDto.categories
        )
        return ResponseEntity.ok().build()
    }
}