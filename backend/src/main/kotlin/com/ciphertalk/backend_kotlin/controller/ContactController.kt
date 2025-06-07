package com.ciphertalk.backend_kotlin.controller

import com.ciphertalk.backend_kotlin.dto.*
import com.ciphertalk.backend_kotlin.service.ContactService
import com.ciphertalk.backend_kotlin.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = ["*"])
class ContactController(
    private val contactService: ContactService,
    private val securityUtil: SecurityUtil
) {

    @PostMapping("/request")
    fun sendContactRequest(@RequestBody request: ContactRequestDto): ResponseEntity<ContactResponseDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val contactResponse = contactService.sendContactRequest(currentUser, request)
            ResponseEntity.status(HttpStatus.CREATED).body(contactResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/respond")
    fun respondToContactRequest(@RequestBody action: ContactActionDto): ResponseEntity<ContactResponseDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val contactResponse = contactService.respondToContactRequest(currentUser, action)
            ResponseEntity.ok(contactResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping
    fun getContacts(): ResponseEntity<ContactListDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val contacts = contactService.getContacts(currentUser)
            ResponseEntity.ok(contacts)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/pending")
    fun getPendingRequests(): ResponseEntity<ContactListDto> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            val pendingRequests = contactService.getPendingRequests(currentUser)
            ResponseEntity.ok(pendingRequests)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("/{contactId}")
    fun removeContact(@PathVariable contactId: Long): ResponseEntity<Void> {
        return try {
            val currentUser = securityUtil.getCurrentUser()
            contactService.removeContact(currentUser, contactId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
