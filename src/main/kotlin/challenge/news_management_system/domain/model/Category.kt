package challenge.news_management_system.domain.model

import javax.persistence.*

@Entity
@Table(name = "CATEGORY")
data class Category(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "NAME", nullable = false, updatable = false, unique = true)
        @OrderBy
        val name: String,

        @OneToMany(mappedBy = "parent")
        val subcategories: MutableList<Category> = mutableListOf()
) {
        @ManyToOne
        @JoinColumn(name = "PARENT_ID")
        var parent: Category? = null

        @ManyToOne
        @JoinColumn(name = "NEWS_ID")
        var news: News? = null
}