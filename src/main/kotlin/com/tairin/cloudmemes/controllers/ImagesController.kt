package com.tairin.cloudmemes.controllers

import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.services.ImagesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImagesController {

    @Autowired
    lateinit var imagesService: ImagesService

    @GetMapping("/{id}")
    fun getImageById(@PathVariable id: Long): String {
        val url = imagesService.getImageUrl(id)
        return "redirect:$url"
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addImage(@RequestPart image: MultipartFile, @RequestParam tags: List<Long>): Image {
        return imagesService.addImageToUser(image, tags)
    }

    @GetMapping
    fun findImageByTags(@RequestParam tags: List<Long>, @RequestParam(required = false) exact: Boolean = false): List<Image> {
        return imagesService.findUserImagesByTags(tags, exact)
    }



}