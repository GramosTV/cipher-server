package com.ciphertalk.backend_kotlin.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "calls")
data class Call(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val callId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    val caller: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callee_id", nullable = false)
    val callee: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: CallStatus = CallStatus.INITIATED,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = true)
    val startedAt: Instant? = null,

    @Column(nullable = true)
    val endedAt: Instant? = null,

    @Column(nullable = false)
    val noiseReductionEnabled: Boolean = false
)

enum class CallStatus {
    INITIATED,
    RINGING,
    ANSWERED,
    ENDED,
    DECLINED,
    MISSED
}
