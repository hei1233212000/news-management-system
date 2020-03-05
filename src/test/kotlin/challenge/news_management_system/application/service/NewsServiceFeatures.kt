package challenge.news_management_system.application.service

import challenge.news_management_system.application.exception.ResourceAlreadyExistException
import challenge.news_management_system.application.exception.ResourceNotFoundException
import challenge.news_management_system.domain.model.News
import challenge.news_management_system.domain.model.NewsTestBuilder
import challenge.news_management_system.infrastructure.repository.NewsRepository
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object NewsServiceFeatures : Spek({
    lateinit var newsRepository: NewsRepository
    lateinit var clock: Clock
    lateinit var newsService: NewsService

    Feature("Create news") {
        lateinit var newsToBeCreated: News

        Scenario("Create news with provided info") {
            lateinit var persistedNews: News

            Given("newsToBeCreated is defined") {
                newsToBeCreated = NewsTestBuilder.default()
            }

            And("clock is initialized") {
                val instant = Instant.parse("2018-08-19T16:02:42.00Z")
                val zoneId = ZoneId.systemDefault()
                clock = Clock.fixed(instant, zoneId)
            }

            And("newsRepository is initialized") {
                newsRepository = mock()
                whenever(newsRepository.save(any<News>())).thenAnswer {
                    it.arguments.first()
                }
            }

            And("newsService is initialized") {
                newsService = NewsService(newsRepository, clock)
            }

            When("we use newsService to create a news") {
                newsService.createNews(newsToBeCreated)
            }

            Then("newsRepository is used for persisting the news") {
                argumentCaptor<News>().apply {
                    verify(newsRepository)
                            .save(capture())

                    persistedNews = firstValue
                }
            }

            And("persistedNews.caption should be correctly set") {
                persistedNews.caption `should be equal to` newsToBeCreated.caption
            }

            And("persistedNews.slug should be correctly set") {
                persistedNews.slug `should be equal to` newsToBeCreated.slug
            }

            And("persistedNews.content should be correctly set") {
                persistedNews.content `should be equal to` newsToBeCreated.content
            }

            And("persistedNews.createdTime should be correctly set") {
                persistedNews.createdTime `should be equal to` LocalDateTime.now(clock)
            }

            And("persistedNews.updatedTime should be correctly set") {
                persistedNews.updatedTime `should be equal to` LocalDateTime.now(clock)
            }
        }

        arrayOf(
                arrayOf(NewsTestBuilder.default(), null),
                arrayOf(null, NewsTestBuilder.default()),
                arrayOf(NewsTestBuilder.default(), NewsTestBuilder.default())
        ).forEach { data ->
            val newsFoundByCaption: News? = data[0]
            val newsFoundBySlug: News? = data[1]
            val captionCondition = "caption is ${newsFoundByCaption?.let{ "already" } ?: "not"} in use"
            val slugCondition = "slug is ${newsFoundBySlug?.let{ "already" } ?: "not"} in use"

            Scenario("Create news where $captionCondition and $slugCondition") {
                Given("newsToBeCreated is defined") {
                    newsToBeCreated = NewsTestBuilder.default()
                }

                And("clock is initialized") {
                    clock = Clock.systemUTC()
                }

                And("newsRepository is initialized") {
                    newsRepository = mock()
                    whenever(newsRepository.findByCaption(any()))
                            .thenReturn(newsFoundByCaption)
                    whenever(newsRepository.findBySlug(any()))
                            .thenReturn(newsFoundBySlug)
                }

                And("newsService is initialized") {
                    newsService = NewsService(newsRepository, clock)
                }

                When("I create a news where $captionCondition and $slugCondition, exception should be thrown") {
                    invoking { newsService.createNews(newsToBeCreated) } `should throw` ResourceAlreadyExistException::class
                }
            }
        }
    }

    Feature("Find list of news") {
        Scenario("Find list of news from news repository") {
            val expectedResult = listOf(NewsTestBuilder.default())
            lateinit var actualResult: List<News>

            Given("clock is initialized") {
                clock = Clock.systemUTC()
            }

            And("newsRepository is initialized") {
                newsRepository = mock()
                whenever(newsRepository.findAllByOrderByCreatedTimeDesc())
                        .thenReturn(expectedResult)
            }

            And("newsService is initialized") {
                newsService = NewsService(newsRepository, clock)
            }

            When("we use newsService to find all news") {
                actualResult = newsService.findAllNews()
            }

            Then("all news in the repository should be returned") {
                actualResult `should be equal to` expectedResult
            }
        }
    }

    Feature("Find a news by slug") {
        arrayOf(
                null,
                NewsTestBuilder.default()
        ).forEach { newsInRepository: News? ->
            val repositoryCondition = "${newsInRepository?.let { "the news exist" } ?: "NO news"} in repository"

            Scenario("Find a news by slug from news repository where $repositoryCondition") {
                var result: News? = null

                Given("clock is initialized") {
                    clock = Clock.systemUTC()
                }

                And("newsRepository is initialized") {
                    newsRepository = mock()
                }

                And(repositoryCondition) {
                    whenever(newsRepository.findBySlug(any()))
                            .thenReturn(newsInRepository)
                }

                And("newsService is initialized") {
                    newsService = NewsService(newsRepository, clock)
                }

                When("we use newsService to find a news by slug") {
                    result = newsService.findBySlug("any slug")
                }

                Then("we should get $newsInRepository") {
                    result `should be equal to` newsInRepository
                }
            }
        }
    }

    Feature("Update the content of a news") {
        Scenario("Update a news which is not exist") {
            Given("clock is initialized") {
                clock = Clock.systemUTC()
            }

            And("newsRepository is initialized") {
                newsRepository = mock()
            }

            And("newsService is initialized") {
                newsService = NewsService(newsRepository, clock)
            }

            When("I update a news which does not exist, exception should be thrown") {
                invoking { newsService.updateContent("non exist news", "any content") } `should throw` ResourceNotFoundException::class
            }
        }

        Scenario("Update a news which is exist") {
            val existingNews = NewsTestBuilder.default()
            val newContent = "updated content"
            lateinit var updatedNews: News

            Given("clock is initialized") {
                clock = Clock.systemUTC()
            }

            And("newsRepository is initialized") {
                newsRepository = mock()
                whenever(newsRepository.findBySlug(existingNews.slug))
                        .thenReturn(existingNews)
                whenever(newsRepository.save(any<News>())).thenAnswer {
                    it.arguments.first()
                }
            }

            And("newsService is initialized") {
                newsService = NewsService(newsRepository, clock)
            }

            When("I update the content of the news to \"$newContent\"") {
                newsService.updateContent(existingNews.slug, newContent)
            }

            Then("the corresponding news should be updated in repository") {
                argumentCaptor<News>().apply {
                    verify(newsRepository)
                            .save(capture())
                    updatedNews = firstValue
                }
            }

            And("the id should be unchanged") {
                updatedNews.id `should be` existingNews.id
            }

            And("the caption should be unchanged") {
                updatedNews.caption `should be` existingNews.caption
            }

            And("the slug should be unchanged") {
                updatedNews.slug `should be` existingNews.slug
            }

            And("the createdTime should be unchanged") {
                updatedNews.createdTime `should be` existingNews.createdTime
            }

            And("the content should be updated") {
                updatedNews.content `should be equal to` newContent
            }

            And("the content should be updated") {
                updatedNews.updatedTime `should not be` null
                updatedNews.updatedTime `should not be` existingNews.updatedTime
            }
        }
    }
})