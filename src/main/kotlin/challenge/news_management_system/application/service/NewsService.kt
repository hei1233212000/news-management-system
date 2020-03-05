package challenge.news_management_system.application.service

import challenge.news_management_system.application.exception.ResourceAlreadyExistException
import challenge.news_management_system.application.exception.ResourceNotFoundException
import challenge.news_management_system.domain.model.News
import challenge.news_management_system.infrastructure.repository.NewsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime

@Service
@Transactional
class NewsService(
        private val newsRepository: NewsRepository,
        private val clock: Clock
) {
    fun createNews(news: News): News {
        val isCaptionInUse = newsRepository.findByCaption(news.caption) != null
        val isSlugInUse = newsRepository.findBySlug(news.slug) != null
        if (isCaptionInUse || isSlugInUse) {
            throw ResourceAlreadyExistException(
                    resourceUniqueKey = news.caption,
                    resourceClass = News::class.java
            )
        }

        val now = LocalDateTime.now(clock)
        val copyOfNews = news.copy(
                createdTime = now,
                updatedTime = now
        )
        return newsRepository.save(copyOfNews)
    }

    @Transactional(readOnly = true)
    fun findAllNews(): List<News> {
        return newsRepository.findAllByOrderByCreatedTimeDesc()
    }

    @Transactional(readOnly = true)
    fun findBySlug(slug: String): News? {
        return newsRepository.findBySlug(slug)
    }

    fun updateContent(slug: String, newContent: String) {
        val news = findBySlug(slug)
        news?.let {
            val now = LocalDateTime.now(clock)
            val updatedNews = it.copy(
                    content = newContent,
                    updatedTime = now
            )
            newsRepository.save(updatedNews)
        } ?: throw ResourceNotFoundException(
                resourceBusinessKey = slug,
                resourceClass = News::class.java
        )
    }
}