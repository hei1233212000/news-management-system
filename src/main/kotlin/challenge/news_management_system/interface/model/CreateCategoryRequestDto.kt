package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Schema(name = "CreateCategoryRequest", description = "DTO for creating category request")
data class CreateCategoryRequestDto(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9-]*\$")
    @field:Schema(required = true)
    var name: String? = null
)
