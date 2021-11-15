package com.example.routes

import com.example.models.Customer
import com.example.models.Customers
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.register() {
    post("/register") {
        val customerCredential = call.receive<Customer>()

        transaction {
            Customers.insert {
                it[id] = customerCredential.id
                it[firstName] = customerCredential.firstName
                it[lastName] = customerCredential.lastName
                it[email] = customerCredential.email
                it[password] = customerCredential.hashedPassword()
            }
        }
        call.respond(customerCredential)
    }
}

@Serializable
data class LoginCredential (val email: String, val password: String) {
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}

fun Route.login() {
    post("/login") {
        val customerCredential = call.receive<LoginCredential>()
        val email = customerCredential.email.lowercase()
        val password = customerCredential.password
        val tokenManager = TokenManager()

        val customer = transaction {
            Customers.select{ Customers.email eq email }.map { Customers.toCustomer(it) }
        }.firstOrNull()

        if (customer == null) {
            call.respondText( "Wrong Email or password", status = HttpStatusCode.BadRequest )
        } else {
            if (!BCrypt.checkpw(password, customer.password)) {
                call.respondText( "Wrong Email or password", status = HttpStatusCode.BadRequest )
            } else {
                val token = tokenManager.generateJWTToken(customer)
                call.respond(token)
            }
        }
    }
}

fun Route.protectedRoute() {
    authenticate {
        get("/me") {
            val principal = call.principal<JWTPrincipal>()

            val email = principal!!.payload.getClaim("email").asString()
            val id = principal!!.payload.getClaim("userId").asInt()

            call.respondText("Hello, $email with id: $id")

        }
    }
}

fun Application.registerAuthRoutes() {
    routing {
        register()
        login()
        protectedRoute()
    }
}