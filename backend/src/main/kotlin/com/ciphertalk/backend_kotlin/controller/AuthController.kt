package com.ciphertalk.backend_kotlin.controller

import com.ciphertalk.backend_kotlin.dto.AuthResponse
import com.ciphertalk.backend_kotlin.dto.LoginRequest
import com.ciphertalk.backend_kotlin.dto.RegisterRequest
import com.ciphertalk.backend_kotlin.dto.UserResponse
import com.ciphertalk.backend_kotlin.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<UserResponse> {
        val userResponse = authService.register(registerRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        val authResponse = authService.login(loginRequest)
        return ResponseEntity.ok(authResponse)
    }
}
