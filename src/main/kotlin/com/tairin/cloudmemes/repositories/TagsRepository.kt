package com.tairin.cloudmemes.repositories

import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.model.User
import org.springframework.data.repository.CrudRepository

interface TagsRepository: CrudRepository<Tag, Long> {
    fun findDistinctByImageTagsUser(user: User): List<Tag>
    fun findDistinctByImageTagsImageAndImageTagsUser(image: Image, user: User) : List<Tag>
}