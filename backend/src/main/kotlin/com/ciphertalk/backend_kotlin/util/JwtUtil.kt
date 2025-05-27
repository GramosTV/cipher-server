package com.ciphertalk.backend_kotlin.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration.ms}")
    private lateinit var expirationMs: String

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        // Add any custom claims if needed
        // claims["userId"] = ... (if you have a custom UserDetails implementation with ID)
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Date()
        val expirationTime = Date(now.time + expirationMs.toLong())

        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(now)
            .expiration(expirationTime)
            .signWith(secretKey, Jwts.SIG.HS512) // Using HS512 algorithm
            .compact()
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
    }

    fun extractUsername(token: String): String? {
        return extractAllClaims(token).subject
    }

    fun extractExpiration(token: String): Date? {
        return extractAllClaims(token).expiration
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token)?.before(Date()) ?: true
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }
}
