package com.tairin.cloudmemes.controllers

import com.tairin.cloudmemes.dto.ImageDTO
import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.services.ImagesService
import com.tairin.cloudmemes.services.TagsService
import com.tairin.cloudmemes.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController {

    @Autowired
    private lateinit var tagsService: TagsService

    @Autowired
    private lateinit var imagesService: ImagesService

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/tags")
    fun getUserTags(): List<Tag> {
        return tagsService.getUserTags()
    }

    @DeleteMapping("/tags/{id}")
    fun deleteUserTags(@PathVariable id: Long): Boolean {
        return tagsService.deleteTagForUser(id)
    }

    @GetMapping("/images")
    fun getUserImages(): List<ImageDTO> {
        return imagesService.getUserImages().map { it.toImageDto() }
    }

    @DeleteMapping("/images/{id}")
    fun deleteUserImage(@PathVariable id: Long): Boolean {
        return imagesService.deleteUserImage(id)
    }

    private fun Image.toImageDto(): ImageDTO {
        val user = userService.getCurrentUser()
        val url = imagesService.getImageUrl(this)

        return toDto(user, url)
    }
}