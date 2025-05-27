package com.ciphertalk.backend_kotlin.repository

import com.ciphertalk.backend_kotlin.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    // You can add more custom query methods here if needed
    // For example: fun findByPublicKey(publicKey: String): User?
}
