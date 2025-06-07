package com.ciphertalk.backend_kotlin.controller

import com.ciphertalk.backend_kotlin.dto.*
import com.ciphertalk.backend_kotlin.service.CallService
import com.ciphertalk.backend_kotlin.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = ["*"])
class CallController(
    private val callService: CallService,
    private val securityUtil: SecurityUtil
) {

    @PostMapping("/initiate")
    fun initiateCall(@RequestBody request: InitiateCallDto): ResponseEntity<CallResponseDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val callResponse = callService.initiateCall(currentUser, request)
            ResponseEntity.status(HttpStatus.CREATED).body(callResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/answer")
    fun answerCall(@RequestBody action: CallActionDto): ResponseEntity<CallResponseDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val callResponse = callService.answerCall(currentUser, action)
            ResponseEntity.ok(callResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/end")
    fun endCall(@RequestBody action: CallActionDto): ResponseEntity<CallResponseDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val callResponse = callService.endCall(currentUser, action)
            ResponseEntity.ok(callResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/active")
    fun getActiveCalls(): ResponseEntity<ActiveCallsDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val activeCalls = callService.getActiveCalls(currentUser)
            ResponseEntity.ok(activeCalls)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/incoming")
    fun getIncomingCalls(): ResponseEntity<ActiveCallsDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val incomingCalls = callService.getIncomingCalls(currentUser)
            ResponseEntity.ok(incomingCalls)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }    @GetMapping("/history")
    fun getCallHistory(): ResponseEntity<List<CallResponseDto>> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val callHistory = callService.getCallHistory(currentUser)
            ResponseEntity.ok(callHistory.calls)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
