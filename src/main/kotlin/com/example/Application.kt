package com.example

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import com.example.routes.registerCustomerRoutes
import com.example.routes.registerOrderRoutes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
    registerCustomerRoutes()
    registerOrderRoutes()
}
