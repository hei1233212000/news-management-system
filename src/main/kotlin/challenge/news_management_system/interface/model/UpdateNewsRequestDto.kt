package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

@Schema(name = "UpdateNewsRequest", description = "DTO for updating news request")
data class UpdateNewsRequestDto(
        @field:NotBlank
        @field:Schema(required = true)
        var newContent: String? = null
)
