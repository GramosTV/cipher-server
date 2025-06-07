package com.ciphertalk.backend_kotlin.repository

import com.ciphertalk.backend_kotlin.model.Call
import com.ciphertalk.backend_kotlin.model.CallStatus
import com.ciphertalk.backend_kotlin.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CallRepository : JpaRepository<Call, Long> {
    
    fun findByCallId(callId: String): Call?
    
    @Query("SELECT c FROM Call c WHERE (c.caller = :user OR c.callee = :user) AND c.status IN ('INITIATED', 'RINGING', 'ANSWERED')")
    fun findActiveCallsForUser(@Param("user") user: User): List<Call>
    
    @Query("SELECT c FROM Call c WHERE c.callee = :user AND c.status = 'INITIATED'")
    fun findIncomingCallsForUser(@Param("user") user: User): List<Call>
    
    @Query("SELECT c FROM Call c WHERE c.caller = :user AND c.status IN ('INITIATED', 'RINGING')")
    fun findOutgoingCallsForUser(@Param("user") user: User): List<Call>
    
    @Query("SELECT c FROM Call c WHERE (c.caller = :user OR c.callee = :user) ORDER BY c.createdAt DESC")
    fun findCallHistoryForUser(@Param("user") user: User): List<Call>
    
    fun existsByCallId(callId: String): Boolean
}
