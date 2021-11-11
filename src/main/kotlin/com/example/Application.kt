package com.example

import com.example.models.Customers
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.*
import com.example.routes.registerCustomerRoutes
import com.example.routes.registerOrderRoutes
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
    //postgres://lmenvpcz:PVayjejJQtXeeaVFUAW09KbdOSuBQ1c6@fanny.db.elephantsql.com/lmenvpcz
    Database.connect("jdbc:postgresql://fanny.db.elephantsql.com/lmenvpcz", driver = "org.postgresql.Driver",
        user = "lmenvpcz", password = "PVayjejJQtXeeaVFUAW09KbdOSuBQ1c6")

    transaction {
        SchemaUtils.create(Customers)

        Customers.insert {
            it[id] = 1
            it[firstName] = "Bernie"
            it[lastName] = "Sanders"
            it[email] = "bernie@gmail.com"
        }
        Customers.insert {
            it[id] = 2
            it[firstName] = "Julia"
            it[lastName] = "Sanders"
            it[email] = "julia@gmail.com"
        }
    }
    registerCustomerRoutes()
    registerOrderRoutes()
}
