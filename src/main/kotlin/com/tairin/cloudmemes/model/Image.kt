package com.tairin.cloudmemes.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tairin.cloudmemes.dto.ImageDTO
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Image(
    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val hash: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null

    @field:CreationTimestamp
    @Column(nullable = false)
    lateinit var created: LocalDateTime

    @JsonIgnore
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "image")
    val imageTags: MutableList<ImageTag> = mutableListOf()

    fun toDto(user: User, url: String): ImageDTO {
        return ImageDTO(
            id = id ?: 0,
            url = url,
            created = created,
            tags = imageTags.filter { it.user.id == user.id }.map { it.tag }
        )
    }
}