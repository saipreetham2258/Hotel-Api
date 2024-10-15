package com.example.module
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class HotelDetails(val name : String,
                        val id : Int,
                        val noOfNights : Int,
                        var price : Double)

@Serializable
data class ResponseToClient(val name : String,
                    val totalPrice : Double)

@Serializable
data class Price(val name : String,
                 val totalPrice : Double)
