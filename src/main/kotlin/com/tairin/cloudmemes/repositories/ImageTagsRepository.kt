package com.tairin.cloudmemes.repositories

import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.ImageTag
import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface ImageTagsRepository: JpaRepository<ImageTag, Long> {
    fun findAllByImageAndUser(image: Image, user: User): Set<ImageTag>
    fun findAllByTagAndUser(tag: Tag, user: User): Set<ImageTag>
    fun deleteAllByTagIdAndUser(tagId: Long, user: User): Long
    fun getAllByUserAndTagIdIn(user: User, tagIds: List<Long>): List<ImageTag>
    fun deleteAllByUserAndImageId(user: User, imageId: Long): Long
    fun existsByImageId(imageId: Long): Boolean
}