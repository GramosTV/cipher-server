package com.ciphertalk.backend_kotlin.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

// Basic chat message structure
data class ChatMessageDto(
    val content: String,
    val sender: String? = null,
    val type: String = "TEXT",
    val timestamp: LocalDateTime = LocalDateTime.now()
)

// Encrypted message structure for end-to-end encryption
data class EncryptedMessageDto(
    val ciphertext: String,
    val iv: String
)

// Secure message with encryption and signature
data class SecureMessageDto(
    @JsonProperty("encryptedContent")
    val encryptedContent: EncryptedMessageDto,
    @JsonProperty("encryptedAESKey")
    val encryptedAESKey: String, // Encrypted with recipient's public key
    @JsonProperty("signature")
    val signature: String,       // Digital signature for integrity
    @JsonProperty("senderPublicKey")
    val senderPublicKey: String, // Sender's public key for verification
    @JsonProperty("timestamp")
    val timestamp: Long,
    @JsonProperty("sender")
    val sender: String? = null,
    @JsonProperty("type")
    val type: String = "SECURE"
)

// Key exchange message for establishing secure communication
data class KeyExchangeMessageDto(
    @JsonProperty("senderPublicKey")
    val senderPublicKey: String,
    @JsonProperty("encryptedSessionKey")
    val encryptedSessionKey: String,
    @JsonProperty("signature")
    val signature: String,
    @JsonProperty("timestamp")
    val timestamp: Long,
    @JsonProperty("sender")
    val sender: String? = null,
    @JsonProperty("type")
    val type: String = "KEY_EXCHANGE"
)

// User public key response for secure communication setup
data class UserPublicKeyDto(
    val username: String,
    val publicKey: String
)
