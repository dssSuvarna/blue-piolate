package com.bluepilot.models.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class PageResponse<T>(
    @JsonProperty("totalCount")
    val totalCount: Long,
    @JsonProperty("pageNumber")
    val pageNumber: Int,
    @JsonProperty("pageSize")
    val pageSize: Int,
    @JsonProperty("currentPageSize")
    val currentPageSize: Int,
    @JsonProperty("contents")
    val contents: List<T>
)
