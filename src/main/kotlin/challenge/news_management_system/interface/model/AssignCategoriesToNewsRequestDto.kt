package challenge.news_management_system.`interface`. model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

@Schema(name = "AssignCategoriesToNewsRequest", description = "DTO for assigning categories to a news request")
data class AssignCategoriesToNewsRequestDto(
        @field:NotEmpty
        @field:Schema(required = true)
        var categories: List<String> = emptyList()
)
