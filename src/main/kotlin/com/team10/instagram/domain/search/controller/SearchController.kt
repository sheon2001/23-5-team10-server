package com.team10.instagram.domain.search.controller

import com.team10.instagram.domain.search.dto.DeleteSearchRequest
import com.team10.instagram.domain.search.dto.GetSearchHistoryResponse
import com.team10.instagram.domain.search.dto.SaveSearchRequest
import com.team10.instagram.domain.search.dto.SaveSearchResponse
import com.team10.instagram.domain.search.service.SearchService
import com.team10.instagram.domain.user.LoggedInUser
import com.team10.instagram.domain.user.model.User
import com.team10.instagram.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "검색 API")
class SearchController(
    private val searchService: SearchService,
) {
    @PostMapping("/recent")
    fun saveSearchHistory(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @RequestBody request: SaveSearchRequest,
    ): ApiResponse<SaveSearchResponse> {
        val response = searchService.saveSearchHistory(user.userId!!, request.toUserId)
        return ApiResponse.onSuccess(response)
    }

    @GetMapping("/recent")
    fun getSearchHistory(
        @Parameter(hidden = true) @LoggedInUser user: User,
    ): ApiResponse<GetSearchHistoryResponse?> {
        val response = searchService.getSearchHistory(user.userId!!)
        return ApiResponse.onSuccess(response)
    }

    @DeleteMapping("/recent")
    fun deleteSearchHistory(
        @Parameter(hidden = true) @LoggedInUser user: User,
        @RequestBody request: DeleteSearchRequest,
    ): ApiResponse<Unit> {
        searchService.deleteSearchHistory(user.userId!!, request.toUserId)
        return ApiResponse.onSuccess(Unit)
    }
}
