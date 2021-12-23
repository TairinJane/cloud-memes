package com.tairin.cloudmemes.controllers

import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.services.TagsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tags")
class TagsController {

    @Autowired
    private lateinit var tagsService: TagsService

    @PostMapping
    fun addTag(@RequestParam value: String): Tag {
        return tagsService.addTag(value)
    }

    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): Tag {
        return tagsService.getTagById(id)
    }

    @DeleteMapping("/{id}")
    fun deleteTagById(@PathVariable id: Long) {
        tagsService.deleteTag(id)
    }

    @GetMapping
    fun getAllTags(): List<Tag> {
        return tagsService.getAllTags()
    }
}