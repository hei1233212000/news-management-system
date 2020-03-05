package challenge.news_management_system.`interface`.rest

import challenge.news_management_system.`interface`.model.CreateNewsRequestDto
import challenge.news_management_system.`interface`.model.NewsDto
import challenge.news_management_system.`interface`.model.UpdateNewsRequestDto
import challenge.news_management_system.application.service.NewsService
import challenge.news_management_system.domain.model.News
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("api/news/")
class NewsResource(
        private val newsService: NewsService
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Create new News")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "201",
                description = "The news is created successfully",
                headers = [Header(name = "Location", description = "The resource path to new created news")]
        ),
        ApiResponse(
                responseCode = "400",
                description = "Validation error"
        )
    ])
    fun createNews(
            @Valid @RequestBody createNewsRequestDto: CreateNewsRequestDto
    ): ResponseEntity<Unit> {
        val news = News(
                caption = createNewsRequestDto.caption!!,
                slug = createNewsRequestDto.slug!!,
                content = createNewsRequestDto.content!!
        )
        newsService.createNews(news)
        val newNewsLocation = ServletUriComponentsBuilder
                .fromCurrentRequest().path("{slug}").buildAndExpand(news.slug)
                .toUri()

        return ResponseEntity.created(newNewsLocation).build()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Retrieve all the news")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "list of news ordered by its time in descending order"
        )
    ])
    fun findAllNews(): List<NewsDto> {
        return newsService.findAllNews().map { it.toDto() }
    }

    @GetMapping(path = ["{slug}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Retrieve a news by slug")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "a news"
        ),
        ApiResponse(
                responseCode = "404",
                description = "The news does not exist"
        )
    ])
    fun findBySlug(@PathVariable("slug") slug: String): ResponseEntity<NewsDto> {
        val news = newsService.findBySlug(slug)
        return news?.let {
            ResponseEntity.ok(it.toDto())
        } ?: ResponseEntity.notFound().build<NewsDto>()
    }

    @PatchMapping(path = ["{slug}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(description = "Update a news")
    @ApiResponses(value = [
        ApiResponse(
                responseCode = "200",
                description = "the news is updated successfully"
        ),
        ApiResponse(
                responseCode = "404",
                description = "The news does not exist"
        )
    ])
    fun updateNews(
            @PathVariable("slug") slug: String,
            @Valid @RequestBody updateNewsRequestDto: UpdateNewsRequestDto
    ): ResponseEntity<Unit> {
        newsService.updateContent(slug, updateNewsRequestDto.newContent!!)
        return ResponseEntity.ok().build()
    }

    private fun News.toDto(): NewsDto = NewsDto(
            slug = this.slug,
            caption = this.caption,
            content = this.content,
            createdTime = this.createdTime!!,
            updatedTime = this.updatedTime!!,
            categories = this.categories.map { it.name }
    )
}