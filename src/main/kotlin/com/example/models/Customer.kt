package com.example.models
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

val customerStorage = mutableListOf<Customer>()

@Serializable
data class Customer(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
    ) {
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}

object Customers: Table() {
    val id: Column<Int> = integer("id")
    val firstName: Column<String> = varchar("firstName", 50)
    val lastName: Column<String> = varchar("lastName", 50)
    val email: Column<String> = varchar("email", 50)
    val password: Column<String> = varchar("password", 200)

    override val primaryKey = PrimaryKey(id, name="PK_Customer_ID")

    fun toCustomer(row: ResultRow): Customer =
        Customer(
            id = row[Customers.id],
            firstName = row[Customers.firstName],
            lastName = row[Customers.lastName],
            email = row[Customers.email],
            password = row[Customers.password]
        )
}