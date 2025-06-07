package com.ciphertalk.backend_kotlin.service

import com.ciphertalk.backend_kotlin.dto.*
import com.ciphertalk.backend_kotlin.model.Call
import com.ciphertalk.backend_kotlin.model.CallStatus
import com.ciphertalk.backend_kotlin.model.User
import com.ciphertalk.backend_kotlin.repository.CallRepository
import com.ciphertalk.backend_kotlin.repository.UserRepository
import com.ciphertalk.backend_kotlin.repository.ContactRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Service
@Transactional
class CallService(
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository
) {

    fun initiateCall(caller: User, request: InitiateCallDto): CallResponseDto {
        val callee = userRepository.findByUsername(request.calleeUsername)
            ?: throw IllegalArgumentException("User not found: ${request.calleeUsername}")

        if (caller.id == callee.id) {
            throw IllegalArgumentException("Cannot call yourself")
        }

        // Check if users are contacts
        val areContacts = contactRepository.findAcceptedContactsForUser(caller)
            .any { contact -> 
                contact.contactUser.id == callee.id || contact.user.id == callee.id 
            }
        
        if (!areContacts) {
            throw IllegalArgumentException("Can only call contacts")
        }

        // Check if there's already an active call between these users
        val existingActiveCalls = callRepository.findActiveCallsForUser(caller)
            .filter { call -> 
                (call.caller.id == callee.id && call.callee.id == caller.id) ||
                (call.caller.id == caller.id && call.callee.id == callee.id)
            }
        
        if (existingActiveCalls.isNotEmpty()) {
            throw IllegalArgumentException("There's already an active call between these users")
        }

        val callId = UUID.randomUUID().toString()
        
        val call = Call(
            callId = callId,
            caller = caller,
            callee = callee,
            status = CallStatus.INITIATED,
            noiseReductionEnabled = request.noiseReductionEnabled
        )

        val savedCall = callRepository.save(call)
        return mapToCallResponseDto(savedCall)
    }

    fun answerCall(currentUser: User, action: CallActionDto): CallResponseDto {
        val call = callRepository.findByCallId(action.callId)
            ?: throw IllegalArgumentException("Call not found")

        if (call.callee.id != currentUser.id) {
            throw IllegalArgumentException("Not authorized to answer this call")
        }

        if (call.status != CallStatus.INITIATED && call.status != CallStatus.RINGING) {
            throw IllegalArgumentException("Call cannot be answered in current state: ${call.status}")
        }

        val updatedStatus = when (action.action.lowercase()) {
            "answer" -> CallStatus.ANSWERED
            "decline" -> CallStatus.DECLINED
            else -> throw IllegalArgumentException("Invalid action for answering call: ${action.action}")
        }

        val updatedCall = call.copy(
            status = updatedStatus,
            startedAt = if (updatedStatus == CallStatus.ANSWERED) Instant.now() else null,
            endedAt = if (updatedStatus == CallStatus.DECLINED) Instant.now() else null
        )

        val savedCall = callRepository.save(updatedCall)
        return mapToCallResponseDto(savedCall)
    }

    fun endCall(currentUser: User, action: CallActionDto): CallResponseDto {
        val call = callRepository.findByCallId(action.callId)
            ?: throw IllegalArgumentException("Call not found")

        if (call.caller.id != currentUser.id && call.callee.id != currentUser.id) {
            throw IllegalArgumentException("Not authorized to end this call")
        }

        if (call.status == CallStatus.ENDED || call.status == CallStatus.DECLINED) {
            throw IllegalArgumentException("Call is already ended")
        }

        val updatedCall = call.copy(
            status = CallStatus.ENDED,
            endedAt = Instant.now()
        )

        val savedCall = callRepository.save(updatedCall)
        return mapToCallResponseDto(savedCall)
    }

    fun getActiveCalls(currentUser: User): ActiveCallsDto {
        val activeCalls = callRepository.findActiveCallsForUser(currentUser)
        val callDtos = activeCalls.map { call -> mapToCallResponseDto(call) }
        return ActiveCallsDto(callDtos)
    }

    fun getIncomingCalls(currentUser: User): ActiveCallsDto {
        val incomingCalls = callRepository.findIncomingCallsForUser(currentUser)
        val callDtos = incomingCalls.map { call -> mapToCallResponseDto(call) }
        return ActiveCallsDto(callDtos)
    }

    fun getCallHistory(currentUser: User): ActiveCallsDto {
        val callHistory = callRepository.findCallHistoryForUser(currentUser)
        val callDtos = callHistory.map { call -> mapToCallResponseDto(call) }
        return ActiveCallsDto(callDtos)
    }

    fun updateCallStatus(callId: String, status: CallStatus): CallResponseDto? {
        val call = callRepository.findByCallId(callId) ?: return null
        
        val updatedCall = call.copy(
            status = status,
            startedAt = if (status == CallStatus.ANSWERED && call.startedAt == null) Instant.now() else call.startedAt,
            endedAt = if (status == CallStatus.ENDED || status == CallStatus.DECLINED) Instant.now() else call.endedAt
        )

        val savedCall = callRepository.save(updatedCall)
        return mapToCallResponseDto(savedCall)
    }

    private fun mapToCallResponseDto(call: Call): CallResponseDto {
        return CallResponseDto(
            callId = call.callId,
            caller = call.caller.username,
            callee = call.callee.username,
            status = call.status.name,
            noiseReductionEnabled = call.noiseReductionEnabled,            createdAt = DateTimeFormatter.ISO_INSTANT.format(call.createdAt),
            startedAt = call.startedAt?.let { DateTimeFormatter.ISO_INSTANT.format(it) },
            endedAt = call.endedAt?.let { DateTimeFormatter.ISO_INSTANT.format(it) }
        )
    }
}
