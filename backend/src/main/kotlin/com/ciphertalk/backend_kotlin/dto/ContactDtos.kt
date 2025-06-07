package com.ciphertalk.backend_kotlin.dto

import com.ciphertalk.backend_kotlin.model.ContactStatus

// Contact DTOs
data class ContactRequestDto(
    val username: String,
    val displayName: String? = null
)

data class ContactResponseDto(
    val id: Long,
    val username: String,
    val displayName: String?,
    val status: String,
    val createdAt: String
)

data class ContactActionDto(
    val contactId: Long,
    val action: String // "accept", "decline", "block"
)

data class ContactListDto(
    val contacts: List<ContactResponseDto>
)
