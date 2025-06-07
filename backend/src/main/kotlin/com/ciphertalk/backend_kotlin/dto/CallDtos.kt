package com.ciphertalk.backend_kotlin.dto

// Call DTOs
data class InitiateCallDto(
    val calleeUsername: String,
    val noiseReductionEnabled: Boolean = false
)

data class CallResponseDto(
    val callId: String,
    val caller: String,
    val callee: String,
    val status: String,
    val noiseReductionEnabled: Boolean,
    val createdAt: String,
    val startedAt: String? = null,
    val endedAt: String? = null
)

data class CallActionDto(
    val callId: String,
    val action: String // "answer", "decline", "end"
)

data class CallSignalingDto(
    val callId: String,
    val type: String, // "offer", "answer", "ice-candidate"
    val data: String // SDP or ICE candidate data
)

data class ActiveCallsDto(
    val calls: List<CallResponseDto>
)
