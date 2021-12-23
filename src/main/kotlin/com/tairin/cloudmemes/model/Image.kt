package com.tairin.cloudmemes.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
    private val imageTags: MutableList<ImageTag> = mutableListOf()
}