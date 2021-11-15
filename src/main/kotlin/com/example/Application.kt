package com.example

import com.example.cron.MainCron
import com.example.models.Customers
import com.example.routes.registerAuthRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.*
import com.example.routes.registerCustomerRoutes
import com.example.routes.registerOrderRoutes
import com.example.utils.TokenManager
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) = runBlocking {
    val tokenManager = TokenManager()
    val cronRunner = MainCron()
    install(Authentication) {
        jwt {
            verifier(tokenManager.verifyJWTToken())
            validate { jwtCredential ->
                if(jwtCredential.payload.getClaim("email").asString().isNotEmpty()) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }

    Database.connect("jdbc:postgresql://fanny.db.elephantsql.com/lmenvpcz", driver = "org.postgresql.Driver",
        user = "lmenvpcz", password = "PVayjejJQtXeeaVFUAW09KbdOSuBQ1c6")


    transaction {
        SchemaUtils.create(Customers)
    }



    registerCustomerRoutes()
    registerOrderRoutes()
    registerAuthRoutes()

    launch { cronRunner.orderIncrementor((2)) }
}
