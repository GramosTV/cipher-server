package com.ciphertalk.backend_kotlin.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contacts")
data class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_user_id", nullable = false)
    val contactUser: User,

    @Column(nullable = true)
    val displayName: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ContactStatus = ContactStatus.PENDING
)

enum class ContactStatus {
    PENDING,
    ACCEPTED,
    BLOCKED
}
