package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Error", description = "Error DTO for storing information about the error")
data class RestApiError(
        @field:Schema(required = true)
        val message: String
)
