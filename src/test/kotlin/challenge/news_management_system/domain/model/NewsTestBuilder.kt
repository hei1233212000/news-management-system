package challenge.news_management_system.domain.model

import java.time.LocalDateTime

object NewsTestBuilder {
    /**
     * a News instance with all field initialized exception id and version
     */
    fun defaultForNewPersist(): News = default().copy(
            id = null,
            version = null
    )

    /**
     * a News instance with all field initialized
     */
    fun default(): News = News(
            id = 1L,
            caption = "any caption",
            slug = "any-caption",
            content = "any content",
            createdTime = LocalDateTime.now(),
            updatedTime = LocalDateTime.now(),
            version = 0
    )
}
