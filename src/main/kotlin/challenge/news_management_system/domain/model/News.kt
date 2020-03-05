package challenge.news_management_system.domain.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "NEWS")
data class News(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "CAPTION", nullable = false, updatable = false, unique = true)
        val caption: String,

        @Column(name = "SLUG", nullable = false, updatable = false, unique = true)
        val slug: String,

        @Column(name = "CONTENT", nullable = false)
        val content: String,

        @Column(name = "CREATED_TIMESTAMP", nullable = false, updatable = false)
        var createdTime: LocalDateTime? = null,

        @Version
        @Column(name = "VERSION", nullable = false, updatable = false)
        val version: Long? = null,

        @Column(name = "UPDATED_TIMESTAMP", nullable = false)
        var updatedTime: LocalDateTime? = null,

        @OneToMany(mappedBy = "news")
        val categories: MutableList<Category> = mutableListOf()
)
