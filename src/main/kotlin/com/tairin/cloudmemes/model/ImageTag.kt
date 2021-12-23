package com.tairin.cloudmemes.model

import javax.persistence.*

@Entity
class ImageTag (
    @ManyToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    val image: Image,

    @ManyToOne
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    val tag: Tag,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null
}