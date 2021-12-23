package com.tairin.cloudmemes.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Tag(
    @Column(nullable = false)
    var value: String,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val author: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long? = null

    @JsonIgnore
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "tag")
    private val imageTags: MutableList<ImageTag> = mutableListOf()
}