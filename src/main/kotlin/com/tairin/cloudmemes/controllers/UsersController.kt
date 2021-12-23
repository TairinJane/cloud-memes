package com.tairin.cloudmemes.controllers

import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.services.ImagesService
import com.tairin.cloudmemes.services.TagsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController {

    @Autowired
    private lateinit var tagsService: TagsService

    @Autowired
    private lateinit var imagesService: ImagesService

    @GetMapping("/tags")
    fun getUserTags(): List<Tag> {
        return tagsService.getUserTags()
    }

    @DeleteMapping("/tags/{id}")
    fun deleteUserTags(@PathVariable id: Long): Boolean {
        return tagsService.deleteTagForUser(id)
    }

    @GetMapping("/images")
    fun getUserImages(): List<Image> {
        return imagesService.getUserImages()
    }
}