package com.ciphertalk.backend_kotlin.handler

import com.fasterxml.jackson.databind.ObjectMapper
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
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val username = session.principal?.name ?: "anonymous"
        logger.info("Received message from User = $username, Session ID = ${session.id}: ${message.payload}")

        try {
            val incomingMessage = objectMapper.readValue(message.payload, ChatMessage::class.java)
            val enrichedMessage = incomingMessage.copy(sender = username) // Enrich message with sender from session

            // For now, echo the message back to the sender
            // In a real app, you would route this to the intended recipient(s)
            // or broadcast to a group/room.
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(ChatMessage(content = "Echo: ${enrichedMessage.content}", sender = "SERVER", type = "ECHO"))))

            // Example: Broadcast to all other connected sessions (simple broadcast)
            // sessions.values.filter { it.isOpen && it.id != session.id }.forEach {
            //     it.sendMessage(TextMessage(objectMapper.writeValueAsString(enrichedMessage)))
            // }

        } catch (e: Exception) {
            logger.error("Error processing message from Session ID = ${session.id}: ${e.message}", e)
            session.sendMessage(TextMessage(objectMapper.writeValueAsString(ChatMessage(content = "Error processing your message: ${e.localizedMessage}", sender = "SERVER", type = "ERROR"))))
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
