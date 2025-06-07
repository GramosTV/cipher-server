package com.ciphertalk.backend_kotlin.service

import com.ciphertalk.backend_kotlin.dto.*
import com.ciphertalk.backend_kotlin.model.Contact
import com.ciphertalk.backend_kotlin.model.ContactStatus
import com.ciphertalk.backend_kotlin.model.User
import com.ciphertalk.backend_kotlin.repository.ContactRepository
import com.ciphertalk.backend_kotlin.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class ContactService(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository
) {

    fun sendContactRequest(currentUser: User, request: ContactRequestDto): ContactResponseDto {
        val contactUser = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("User not found: ${request.username}")

        if (currentUser.id == contactUser.id) {
            throw IllegalArgumentException("Cannot add yourself as a contact")
        }

        // Check if contact already exists
        if (contactRepository.existsByUserAndContactUser(currentUser, contactUser)) {
            throw IllegalArgumentException("Contact already exists")
        }

        val contact = Contact(
            user = currentUser,
            contactUser = contactUser,
            displayName = request.displayName,
            status = ContactStatus.PENDING
        )

        val savedContact = contactRepository.save(contact)
        return mapToContactResponseDto(savedContact)
    }

    fun respondToContactRequest(currentUser: User, action: ContactActionDto): ContactResponseDto {
        val contact = contactRepository.findById(action.contactId)
            .orElseThrow { IllegalArgumentException("Contact request not found") }

        if (contact.contactUser.id != currentUser.id) {
            throw IllegalArgumentException("Not authorized to respond to this contact request")
        }

        if (contact.status != ContactStatus.PENDING) {
            throw IllegalArgumentException("Contact request is not pending")
        }

        val updatedStatus = when (action.action.lowercase()) {
            "accept" -> ContactStatus.ACCEPTED
            "decline", "reject" -> {
                contactRepository.delete(contact)
                return mapToContactResponseDto(contact.copy(status = ContactStatus.PENDING))
            }
            "block" -> ContactStatus.BLOCKED
            else -> throw IllegalArgumentException("Invalid action: ${action.action}")
        }

        val updatedContact = contact.copy(status = updatedStatus)
        val savedContact = contactRepository.save(updatedContact)
        
        return mapToContactResponseDto(savedContact)
    }

    fun getContacts(currentUser: User): ContactListDto {
        val contacts = contactRepository.findAcceptedContactsForUser(currentUser)
        val contactDtos = contacts.map { contact ->
            mapToContactResponseDto(contact, currentUser)
        }
        return ContactListDto(contactDtos)
    }

    fun getPendingRequests(currentUser: User): ContactListDto {
        val pendingRequests = contactRepository.findPendingContactRequestsForUser(currentUser)
        val requestDtos = pendingRequests.map { contact ->
            mapToContactResponseDto(contact, currentUser)
        }
        return ContactListDto(requestDtos)
    }

    fun removeContact(currentUser: User, contactId: Long) {
        val contact = contactRepository.findById(contactId)
            .orElseThrow { IllegalArgumentException("Contact not found") }

        if (contact.user.id != currentUser.id && contact.contactUser.id != currentUser.id) {
            throw IllegalArgumentException("Not authorized to remove this contact")
        }

        contactRepository.delete(contact)
    }

    private fun mapToContactResponseDto(contact: Contact, currentUser: User? = null): ContactResponseDto {
        val otherUser = if (currentUser != null && contact.user.id == currentUser.id) {
            contact.contactUser
        } else {
            contact.user
        }

        return ContactResponseDto(
            id = contact.id!!,
            username = otherUser.username,
            displayName = contact.displayName ?: otherUser.username,
            status = contact.status.name,
            createdAt = DateTimeFormatter.ISO_INSTANT.format(contact.createdAt)
        )
    }
}
