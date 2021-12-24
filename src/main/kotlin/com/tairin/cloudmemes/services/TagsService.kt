package com.tairin.cloudmemes.services

import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.repositories.ImageTagsRepository
import com.tairin.cloudmemes.repositories.TagsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

interface TagsService {
    fun addTag(value: String): Tag
    fun deleteTag(id: Long): Boolean
    fun getTagById(id: Long): Tag
    fun getAllTags(): List<Tag>
    fun getUserTags(): List<Tag>
    fun deleteTagForUser(id: Long): Boolean
}

@Service
class TagsServiceImpl: TagsService {

    @Autowired
    private lateinit var tagsRepository: TagsRepository

    @Autowired
    private lateinit var imageTagsRepository: ImageTagsRepository

    @Autowired
    private lateinit var userService: UserService

    override fun addTag(value: String): Tag {
        val existing = tagsRepository.findByValue(value)
        if (existing != null) return existing

        val user = userService.getCurrentUser()
        val tag = Tag(value, user)
        return tagsRepository.save(tag)
    }

    override fun deleteTag(id: Long): Boolean {
        val exists = tagsRepository.existsById(id)
        if (!exists) throw NoSuchElementException("No tag with id = $id exists")

        tagsRepository.deleteById(id)
        return true
    }

    override fun getTagById(id: Long): Tag {
        return tagsRepository.findByIdOrNull(id) ?: throw NoSuchElementException("No tag with id = $id exists")
    }

    override fun getAllTags(): List<Tag> {
        return tagsRepository.findAll().toList()
    }

    override fun getUserTags(): List<Tag> {
        val user = userService.getCurrentUser()
        return tagsRepository.findDistinctByImageTagsUser(user)
    }

    override fun deleteTagForUser(id: Long): Boolean {
        val user = userService.getCurrentUser()
        return imageTagsRepository.deleteAllByTagIdAndUser(id, user) > 0
    }
}