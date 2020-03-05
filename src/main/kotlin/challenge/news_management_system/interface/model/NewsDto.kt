package challenge.news_management_system.`interface`.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(name = "News", description = "DTO for storing news information")
data class NewsDto(
        @field:Schema(required = true)
        val caption: String,

        @field:Schema(required = true)
        val slug: String,

        @field:Schema(required = true)
        val content: String,

        @field:Schema(description = "the create time of the news. (ISO 8601)", required = true)
        val createdTime: LocalDateTime,

        @field:Schema(description = "the latest update time of the news. (ISO 8601)", required = true)
        val updatedTime: LocalDateTime,

        @field:Schema(required = true)
        var categories: List<String> = emptyList()
)
