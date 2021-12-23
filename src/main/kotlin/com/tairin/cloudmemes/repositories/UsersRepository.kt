package com.tairin.cloudmemes.repositories

import com.tairin.cloudmemes.model.User
import org.springframework.data.repository.CrudRepository

interface UsersRepository : CrudRepository<User, Long> {
    fun getUserByUsername(name: String): User
    fun getUserByEmail(email: String): User
    fun existsByUsername(username: String): Boolean
}