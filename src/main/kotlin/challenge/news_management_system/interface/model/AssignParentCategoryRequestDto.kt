package challenge.news_management_system.`interface`. model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

@Schema(name = "AssignParentCategoryRequest", description = "DTO for assigning sub-category to another category request")
data class AssignParentCategoryRequestDto(
        @field:NotBlank
        @field:Schema(required = true)
        var parentName: String? = null
)
