package com.ciphertalk.backend_kotlin.service

import com.ciphertalk.backend_kotlin.dto.LoginRequest
import com.ciphertalk.backend_kotlin.dto.RegisterRequest
import com.ciphertalk.backend_kotlin.dto.AuthResponse
import com.ciphertalk.backend_kotlin.dto.UserResponse
import com.ciphertalk.backend_kotlin.model.User
import com.ciphertalk.backend_kotlin.repository.UserRepository
import com.ciphertalk.backend_kotlin.util.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        return org.springframework.security.core.userdetails.User(user.username, user.passwordHash, emptyList()) // No authorities for now
    }
}

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsServiceImpl
) {

    @Transactional
    fun register(registerRequest: RegisterRequest): UserResponse {
        if (userRepository.findByUsername(registerRequest.username) != null) {
            throw IllegalArgumentException("Username '${registerRequest.username}' is already taken")
        }

        val user = User(
            username = registerRequest.username,
            passwordHash = passwordEncoder.encode(registerRequest.password),
            publicKey = registerRequest.publicKey
        )
        val savedUser = userRepository.save(user)
        return UserResponse(id = savedUser.id!!, username = savedUser.username, publicKey = savedUser.publicKey)
    }

    fun login(loginRequest: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
        val token = jwtUtil.generateToken(userDetails)
        return AuthResponse(token = token, username = userDetails.username, message = "Login successful")
    }
}
