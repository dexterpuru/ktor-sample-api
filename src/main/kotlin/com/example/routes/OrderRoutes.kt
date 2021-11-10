package com.example.routes

import com.example.models.orderStorage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.response.*

const val ENDPOINT: String = "/order"

fun Route.listOrdersRoute() {
    get("$ENDPOINT") {
        if(orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        }
    }
}

fun Route.getOrderRoute() {
    get("$ENDPOINT/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute() {
    get("$ENDPOINT/{id}/total") {
        val id = call.parameters["id"]
            ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.map { it.price * it.amount }.sum()
        call.respond(total)
    }
}

fun Application.registerOrderRoutes() {
    routing {
        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()
    }
}