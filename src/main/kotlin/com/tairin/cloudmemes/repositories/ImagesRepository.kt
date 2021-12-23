package com.tairin.cloudmemes.repositories

import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ImagesRepository : JpaRepository<Image, Long> {
    fun existsByHash(hash: String): Boolean
    fun getImageByHash(hash: String): Image
    fun getDistinctByImageTagsUser(user: User): List<Image>
    fun findDistinctByImageTagsUserAndImageTagsTagIdIn(user: User, tagIds: List<Long>): List<Image>

    @Query(
        value = "SELECT i FROM Image i LEFT JOIN i.imageTags it WHERE it.tag.id IN :tagIds AND it.user = :user"
                + " GROUP BY i HAVING COUNT(it) = :tagIdsCount"
    )
    fun findAllByUserAndTagIdsExact(
        @Param("user") user: User,
        @Param("tagIds") tagIds: List<Long>,
        @Param("tagIdsCount") tagIdsCount: Long
    ): List<Image>
}