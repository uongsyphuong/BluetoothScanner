package com.usphuong.bluetoothscanner.data.model

data class User(
    val address: Address,
    val avatar: String,
    val company: Company,
    val email: String,
    val id: Int,
    val name: String,
    val phone: String,
    val username: String,
    val website: String
)

data class Address(
    val building: String,
    val city: String,
    val geo: Geo,
    val street: String,
    val unit: String,
    val zipcode: String
) {
    override fun toString(): String {
        return "$street, $unit $building, $city $zipcode"
    }
}

data class Geo(
    val lat: String,
    val lng: String
)

data class Company(
    val name: String
)
