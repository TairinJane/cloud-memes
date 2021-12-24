package com.tairin.cloudmemes.dto

import com.tairin.cloudmemes.model.Tag
import java.time.LocalDateTime

class ImageDTO(
    val url: String,
    val id: Long,
    val created: LocalDateTime,
    val tags: List<Tag>
)