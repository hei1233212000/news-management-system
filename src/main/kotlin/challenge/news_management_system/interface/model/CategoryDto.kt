package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Category", description = "DTO for storing category information")
data class CategoryDto(
        @field:Schema(required = true)
        var name: String? = null,

        @field:Schema(required = true)
        var subCategories: List<CategoryDto> = emptyList()
)
