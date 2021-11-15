package com.example.routes

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import com.example.models.Customer
import com.example.models.Customers
import com.example.models.customerStorage
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.customerRouting() {
    route("/customer") {
        println("[GET] - 'All Customers'")
        get {
            val customers = transaction {
                Customers.selectAll().map { Customers.toCustomer(it) }
            }
            call.respond(customers)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or wrong id",
                status = HttpStatusCode.BadRequest
            )

            val customers = transaction {
                Customers.select { Customers.id eq id.toInt() }.map{Customers.toCustomer(it)}
            }

            call.respond(customers)
        }
        post {
            val customer = call.receive<Customer>()

            transaction {
                Customers.insert {
                    it[Customers.id] = customer.id
                    it[Customers.firstName] = customer.firstName
                    it[Customers.lastName] = customer.lastName
                    it[Customers.email] = customer.email
                }
            }
            call.respond(customer)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val customer = transaction {
                Customers.deleteWhere { Customers.id eq id.toInt() }
            }

            if (customer != 0) {
                call.respondText("Customer Removed Successfully", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("No customer found with id: $id", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}