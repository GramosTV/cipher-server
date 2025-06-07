package com.ciphertalk.backend_kotlin.util

import com.ciphertalk.backend_kotlin.model.User
import com.ciphertalk.backend_kotlin.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class SecurityUtil(private val userRepository: UserRepository) {
    
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as UserDetails
        return userRepository.findByUsername(userDetails.username)
            ?: throw IllegalStateException("Current user not found in database")
    }
    
    fun getCurrentUsername(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as UserDetails
        return userDetails.username
    }
}
