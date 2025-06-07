package com.ciphertalk.backend_kotlin.repository

import com.ciphertalk.backend_kotlin.model.Contact
import com.ciphertalk.backend_kotlin.model.ContactStatus
import com.ciphertalk.backend_kotlin.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : JpaRepository<Contact, Long> {
    
    fun findByUserAndContactUser(user: User, contactUser: User): Contact?
    
    fun findByUserAndStatus(user: User, status: ContactStatus): List<Contact>
    
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND c.status = :status")
    fun findContactsByUserAndStatus(@Param("user") user: User, @Param("status") status: ContactStatus): List<Contact>
    
    @Query("SELECT c FROM Contact c WHERE (c.user = :user OR c.contactUser = :user) AND c.status = 'ACCEPTED'")
    fun findAcceptedContactsForUser(@Param("user") user: User): List<Contact>
    
    @Query("SELECT c FROM Contact c WHERE c.contactUser = :user AND c.status = 'PENDING'")
    fun findPendingContactRequestsForUser(@Param("user") user: User): List<Contact>
    
    fun existsByUserAndContactUser(user: User, contactUser: User): Boolean
}
