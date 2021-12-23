package com.tairin.cloudmemes.model

import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "user")
    private val imageTags: MutableList<ImageTag> = mutableListOf()
}