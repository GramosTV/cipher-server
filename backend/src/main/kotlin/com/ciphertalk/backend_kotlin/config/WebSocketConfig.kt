package com.ciphertalk.backend_kotlin.config

import com.ciphertalk.backend_kotlin.handler.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val chatWebSocketHandler: ChatWebSocketHandler) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        // Register the handler for the "/ws/chat" endpoint
        // setAllowedOrigins("*") allows all origins, which is fine for development.
        // For production, you should restrict this to your frontend's domain.
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
            .setAllowedOrigins("*") // TODO: Restrict in production
    }
}
