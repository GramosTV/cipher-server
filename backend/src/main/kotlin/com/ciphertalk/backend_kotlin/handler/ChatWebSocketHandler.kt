package com.ciphertalk.backend_kotlin.handler

import com.ciphertalk.backend_kotlin.dto.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

// Basic DTO for incoming/outgoing messages
// You'll likely want to expand this with more fields like sender, recipient, timestamp, type (e.g., direct, group), etc.
data class ChatMessage(
    val content: String,
    val sender: String? = null, // Can be derived from authenticated session
    val type: String = "TEXT" // Example: TEXT, IMAGE_URL, FILE_INFO
)

@Component
class ChatWebSocketHandler(private val objectMapper: ObjectMapper) : TextWebSocketHandler() {

    private val logger = LoggerFactory.getLogger(ChatWebSocketHandler::class.java)
    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap() // Stores session.id to session

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session
        val username = session.principal?.name ?: "anonymous"
        logger.info("WebSocket connection established: Session ID = ${session.id}, User = $username")
        // Optionally, send a welcome message or connection confirmation
        // session.sendMessage(TextMessage(objectMapper.writeValueAsString(ChatMessage(content = "Welcome $username!", type = "SYSTEM"))))
    }    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val username = session.principal?.name ?: "anonymous"
        logger.info("Received message from User = $username, Session ID = ${session.id}: ${message.payload}")

        try {
            // Try to parse as different message types
            val messagePayload = message.payload
            
            // Determine message type by parsing JSON
            val messageType = try {
                val tempMap = objectMapper.readValue<Map<String, Any>>(messagePayload)
                tempMap["type"] as? String ?: "TEXT"
            } catch (e: Exception) {
                "TEXT"
            }
              when (messageType) {
                "SECURE" -> handleSecureMessage(session, messagePayload, username)
                "KEY_EXCHANGE" -> handleKeyExchange(session, messagePayload, username)
                "CALL_SIGNAL" -> handleCallSignaling(session, messagePayload, username)
                "CALL_OFFER", "CALL_ANSWER", "ICE_CANDIDATE" -> handleCallSignaling(session, messagePayload, username)
                else -> handleRegularMessage(session, messagePayload, username)
            }

        } catch (e: Exception) {
            logger.error("Error processing message from Session ID = ${session.id}: ${e.message}", e)
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(ChatMessage(content = "Error processing your message: ${e.localizedMessage}", sender = "SERVER", type = "ERROR"))))
        }
    }
    
    private fun handleRegularMessage(session: WebSocketSession, messagePayload: String, username: String) {
        val incomingMessage = objectMapper.readValue<ChatMessage>(messagePayload)
        val enrichedMessage = incomingMessage.copy(sender = username)

        // Broadcast to all other connected sessions
        sessions.values.filter { it.isOpen && it.id != session.id }.forEach { targetSession ->
            try {
                targetSession.sendMessage(TextMessage(objectMapper.writeValueAsString(enrichedMessage)))
            } catch (e: Exception) {
                logger.error("Failed to send message to session ${targetSession.id}: ${e.message}")
            }
        }
    }
    
    private fun handleSecureMessage(session: WebSocketSession, messagePayload: String, username: String) {
        val secureMessage = objectMapper.readValue<SecureMessageDto>(messagePayload)
        val enrichedMessage = secureMessage.copy(sender = username)
        
        logger.info("Handling secure message from $username")
        
        // Forward encrypted message to all other sessions (they will decrypt with their private keys)
        sessions.values.filter { it.isOpen && it.id != session.id }.forEach { targetSession ->
            try {
                targetSession.sendMessage(TextMessage(objectMapper.writeValueAsString(enrichedMessage)))
            } catch (e: Exception) {
                logger.error("Failed to send secure message to session ${targetSession.id}: ${e.message}")
            }
        }
    }
      private fun handleKeyExchange(session: WebSocketSession, messagePayload: String, username: String) {
        val keyExchangeMessage = objectMapper.readValue<KeyExchangeMessageDto>(messagePayload)
        val enrichedMessage = keyExchangeMessage.copy(sender = username)
        
        logger.info("Handling key exchange from $username")
        
        // Forward key exchange to all other sessions
        sessions.values.filter { it.isOpen && it.id != session.id }.forEach { targetSession ->
            try {
                targetSession.sendMessage(TextMessage(objectMapper.writeValueAsString(enrichedMessage)))
            } catch (e: Exception) {
                logger.error("Failed to send key exchange to session ${targetSession.id}: ${e.message}")
            }
        }
    }
    
    private fun handleCallSignaling(session: WebSocketSession, messagePayload: String, username: String) {
        try {
            val signalingMessage = objectMapper.readValue<CallSignalingDto>(messagePayload)
            val enrichedMessage = signalingMessage.copy()
            
            logger.info("Handling call signaling from $username for call ${signalingMessage.callId}")
            
            // Forward call signaling to specific recipient or all sessions for now
            // In a production system, you'd want to send only to the specific recipient
            sessions.values.filter { it.isOpen && it.id != session.id }.forEach { targetSession ->
                try {
                    targetSession.sendMessage(TextMessage(objectMapper.writeValueAsString(enrichedMessage)))
                } catch (e: Exception) {
                    logger.error("Failed to send call signaling to session ${targetSession.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            logger.error("Error handling call signaling: ${e.message}")
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        val username = session.principal?.name ?: "anonymous"
        logger.error("WebSocket transport error for User = $username, Session ID = ${session.id}: ${exception.message}", exception)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
        val username = session.principal?.name ?: "anonymous"
        logger.info("WebSocket connection closed for User = $username, Session ID = ${session.id}, Status = $status")
    }
}
