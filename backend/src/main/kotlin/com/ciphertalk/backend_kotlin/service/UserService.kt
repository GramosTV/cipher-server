package com.ciphertalk.backend_kotlin.service

import com.ciphertalk.backend_kotlin.dto.UserPublicKeyDto
import com.ciphertalk.backend_kotlin.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getUserPublicKey(username: String): String? {
        val user = userRepository.findByUsername(username)
        return user?.publicKey
    }

    fun getAllPublicKeys(): List<UserPublicKeyDto> {
        return userRepository.findAll()
            .filter { it.publicKey != null }
            .map { UserPublicKeyDto(username = it.username, publicKey = it.publicKey!!) }
    }
}
