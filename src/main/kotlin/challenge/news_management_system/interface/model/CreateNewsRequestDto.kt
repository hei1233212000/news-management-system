package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Schema(name = "CreateNewsRequest", description = "DTO for creating news request")
data class CreateNewsRequestDto(
        @field:NotBlank
        @field:Size(min = 1, max = 80)
        @field:Schema(description = "The length could not be longer than 80", required = true)
        var caption: String? = null,

        @field:NotBlank
        @field:Size(min = 1, max = 80)
        @field:Pattern(regexp = "^[a-zA-Z0-9-]*\$")
        @field:Schema(description = "The length could not be longer than 80. It can only contain alphanumeric and hyphen", required = true)
        var slug: String? = null,

        @field:NotBlank
        @field:Schema(required = true)
        var content: String? = null
)
