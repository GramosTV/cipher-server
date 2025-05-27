package com.ciphertalk.backend_kotlin.controller

import com.ciphertalk.backend_kotlin.dto.UserPublicKeyDto
import com.ciphertalk.backend_kotlin.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(private val userService: UserService) {

    @GetMapping("/{username}/public-key")
    fun getUserPublicKey(@PathVariable username: String): ResponseEntity<UserPublicKeyDto> {
        return try {
            val publicKey = userService.getUserPublicKey(username)
            if (publicKey != null) {
                ResponseEntity.ok(UserPublicKeyDto(username = username, publicKey = publicKey))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/public-keys")
    fun getAllPublicKeys(): ResponseEntity<List<UserPublicKeyDto>> {
        return try {
            val publicKeys = userService.getAllPublicKeys()
            ResponseEntity.ok(publicKeys)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
}
