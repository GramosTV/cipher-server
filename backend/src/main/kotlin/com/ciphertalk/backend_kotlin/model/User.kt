package com.ciphertalk.backend_kotlin.model

import jakarta.persistence.*

@Entity
@Table(name = "users") // Explicitly naming the table
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val passwordHash: String,

    // Placeholder for public key - can be a String or byte array depending on format
    // For simplicity, using String for now. Consider Base64 encoding if it's binary.
    @Lob // For potentially large string data
    @Column(nullable = true) // Assuming public key might not be set immediately
    val publicKey: String? = null,

    // Timestamps for tracking
    // @Column(nullable = false, updatable = false)
    // val createdAt: java.time.Instant = java.time.Instant.now(),

    // @Column(nullable = false)
    // var updatedAt: java.time.Instant = java.time.Instant.now()
)
// Note: For createdAt and updatedAt, you might want to use @CreationTimestamp and @UpdateTimestamp
// from Hibernate, or handle them manually/via JPA lifecycle callbacks if preferred.
// For simplicity in this initial step, they are commented out.
