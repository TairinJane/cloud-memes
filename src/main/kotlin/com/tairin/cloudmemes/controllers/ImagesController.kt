package com.tairin.cloudmemes.controllers

import com.tairin.cloudmemes.dto.ImageDTO
import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.services.ImagesService
import com.tairin.cloudmemes.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImagesController {

    @Autowired
    lateinit var imagesService: ImagesService

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/{id}")
    fun getImageById(@PathVariable id: Long): ImageDTO {
        return imagesService.getImageById(id).toImageDto()
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addImage(@RequestPart image: MultipartFile, @RequestParam tags: List<Long>): ImageDTO {
        return imagesService.addImageToUser(image, tags).toImageDto()
    }

    @GetMapping
    fun findImageByTags(@RequestParam tags: List<Long>, @RequestParam(required = false) exact: Boolean = false): List<ImageDTO> {
        return imagesService.findUserImagesByTags(tags, exact).map { it.toImageDto() }
    }

    private fun Image.toImageDto(): ImageDTO {
        val user = userService.getCurrentUser()
        val url = imagesService.getImageUrl(this)

        return toDto(user, url)
    }
}