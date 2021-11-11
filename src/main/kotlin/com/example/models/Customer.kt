package com.example.models
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

val customerStorage = mutableListOf<Customer>()

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String, val email: String)

object Customers: Table() {
    val id: Column<Int> = integer("id")
    val firstName: Column<String> = varchar("firstName", 50)
    val lastName: Column<String> = varchar("lastName", 50)
    val email: Column<String> = varchar("email", 50)

    override val primaryKey = PrimaryKey(id, name="PK_Customer_ID")
}