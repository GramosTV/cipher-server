package com.ciphertalk.backend_kotlin.dto

// Request DTOs
data class RegisterRequest(
    val username: String,
    val password: String, // Plain text password, will be hashed in the service
    val publicKey: String? = null // Optional public key at registration
)

data class LoginRequest(
    val username: String,
    val password: String
)

// Response DTOs
data class AuthResponse(
    val token: String,
    val username: String,
    val message: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val publicKey: String?
)
