package com.modules

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import io.ktor.server.auth.*

fun Application.configureTemplating() {
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
}
