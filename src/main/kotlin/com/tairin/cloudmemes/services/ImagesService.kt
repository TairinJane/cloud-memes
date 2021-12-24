package com.tairin.cloudmemes.services

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.tairin.cloudmemes.model.Image
import com.tairin.cloudmemes.model.ImageTag
import com.tairin.cloudmemes.model.Tag
import com.tairin.cloudmemes.model.User
import com.tairin.cloudmemes.repositories.ImageTagsRepository
import com.tairin.cloudmemes.repositories.ImagesRepository
import com.tairin.cloudmemes.repositories.TagsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest


interface ImagesService {
    fun getImageUrl(image: Image): String
    fun addImageToUser(imageFile: MultipartFile, tagIds: List<Long>): Image
    fun getUserImages(): List<Image>
    fun findUserImagesByTags(tagIds: List<Long>, isExact: Boolean): List<Image>
    fun getImageById(id: Long): Image
    fun deleteUserImage(id: Long): Boolean
}

@Service
class ImagesServiceImpl : ImagesService {

    @Value("\${gcs-resource-test-bucket}")
    private lateinit var bucketName: String

    @Value("\${spring.cloud.gcp.project-id}")
    private lateinit var projectId: String

    @Autowired
    private lateinit var imagesRepository: ImagesRepository

    @Autowired
    private lateinit var imageTagsRepository: ImageTagsRepository

    @Autowired
    private lateinit var tagsRepository: TagsRepository

    @Autowired
    private lateinit var tagsService: TagsService

    @Autowired
    private lateinit var userService: UserService

    var storage: Storage? = null
        get() {
            if (field == null) field = StorageOptions.newBuilder().setProjectId(projectId).build().service
            return field
        }

    val hasher: MessageDigest = MessageDigest.getInstance("MD5")

    override fun getImageUrl(image: Image): String {
        return "https://storage.googleapis.com/$bucketName/${image.url}"
    }

    fun uploadImage(imageFile: MultipartFile): Image {
        val hash = getImageHash(imageFile)

        val exists = imagesRepository.existsByHash(hash)

        return if (!exists) {
            var image = Image(url = hash, hash = hash)

            image = imagesRepository.save(image)

            val blobId = BlobId.of(bucketName, hash)

            val mimeType = imageFile.contentType
            val blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build()
            storage?.create(blobInfo, imageFile.bytes)

            image
        } else {
            imagesRepository.getImageByHash(hash)
        }
    }

    fun addImageTagsForUser(image: Image, user: User, tags: Iterable<Tag>) {
        tags.forEach { tag ->
            val imageTag = ImageTag(image, tag, user)
            imageTagsRepository.save(imageTag)
        }
    }

    fun deleteImageTagsByUser(image: Image, user: User, tags: Iterable<Tag>) {
        tags.forEach { tag ->
            val imageTag = ImageTag(image, tag, user)
            imageTagsRepository.delete(imageTag)
        }
    }

    @Transactional
    override fun addImageToUser(imageFile: MultipartFile, tagIds: List<Long>): Image {
        val image = uploadImage(imageFile)
        val tags = tagIds.map { tagsService.getTagById(it) }
        val user = userService.getCurrentUser()

        addImageTagsForUser(image, user, tags)

        return imagesRepository.getById(image.id!!)
    }

    override fun getUserImages(): List<Image> {
        val user = userService.getCurrentUser()
        return imagesRepository.getDistinctByImageTagsUser(user)
    }

    override fun findUserImagesByTags(tagIds: List<Long>, isExact: Boolean): List<Image> {
        val user = userService.getCurrentUser()
        return if (isExact) {
            imagesRepository.findAllByUserAndTagIdsExact(user, tagIds, tagIds.size.toLong())
        } else {
            imagesRepository.findDistinctByImageTagsUserAndImageTagsTagIdIn(user, tagIds)
        }
    }

    @Transactional
    override fun deleteUserImage(id: Long): Boolean {
        val user = userService.getCurrentUser()
        val deleted = imageTagsRepository.deleteAllByUserAndImageId(user, id)
        if (deleted == 0L) throw Exception("No image with id = $id found")

        val stillUsed = imageTagsRepository.existsByImageId(id)
        if (!stillUsed) {
            val image = imagesRepository.findByIdOrNull(id) ?: throw Exception("No image with id = $id exists")
            storage?.delete(bucketName, image.url)
            imagesRepository.deleteById(id)
        }
        return true
    }

    fun getImageTags(image: Image): List<Tag> {
        val user = userService.getCurrentUser()
        return image.imageTags.filter { it.user.id == user.id }.map { it.tag }
    }

    fun editImageTags(imageId: Long, newTagIds: List<Long>) {
        val image = imagesRepository.findByIdOrNull(imageId)
            ?: throw NoSuchElementException("No image with id = $imageId found")
        val user = userService.getCurrentUser()
        val tags = tagsRepository.findDistinctByImageTagsImageAndImageTagsUser(image, user)

        val newTags = newTagIds.map { tagsService.getTagById(it) }
        val tagsToDelete = tags.subtract(newTags)
        val tagsToAdd = newTags.subtract(tags)

        deleteImageTagsByUser(image, user, tagsToDelete)
        addImageTagsForUser(image, user, tagsToAdd)
    }

    override fun getImageById(id: Long): Image {
        return imagesRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Image with id = $id doesn't exist")
    }

    private fun getImageHash(imageFile: MultipartFile): String = with(hasher) {
        update(imageFile.bytes)
        val digest = digest()
        val sb = StringBuffer()
        digest.forEach { byte ->
            var s = Integer.toHexString(0xff and byte.toInt())
            s = if (s.length == 1) "0$s" else s
            sb.append(s)
        }
        println("image hash = $sb")
        return sb.toString()
    }
}