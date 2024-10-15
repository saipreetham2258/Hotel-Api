package com.example.routes

import com.example.Database.DataBaseConnectivity
import com.example.module.HotelDetails
import io.ktor.server.request.*
import io.ktor.server.routing.*
import com.example.Database.*
import com.example.module.Price
import com.example.module.ResponseToClient
import de.undercouch.bson4jackson.types.ObjectId
import io.ktor.http.*
import io.ktor.server.response.*
import org.litote.kmongo.*
import org.litote.kmongo.util.idValue

fun Route.CustomerRouting() {

    post("/Customer") {
        val customerDetails = call.receive<HotelDetails>()
        val finaltotalprice = customerDetails.price * customerDetails.noOfNights
        customerDetails.price = finaltotalprice
        DataBaseConnectivity.collection.insertOne(customerDetails)

        val response = ResponseToClient(
            name = customerDetails.name,
            totalPrice = finaltotalprice)

        call.respond(status = HttpStatusCode.OK,response)

    }
    get("/customers") {
        val customers = DataBaseConnectivity.collection.find().toList()
        try{
            call.respond(customers)
        }
        catch(e : Exception) {
            call.respond(status = HttpStatusCode.InternalServerError,"Error while retrieving data ${e.message}")
        }
    }
    get("/{id?}") {
        val id = call.parameters["id"]  //this will return the string id from url
        val collect = DataBaseConnectivity.collection
        val id1 : Int? = id?.toInt() //convert string to int
        val customer = collect.findOne(HotelDetails::id eq id1)
        if(customer != null) {
           val totalprice = Price(name = customer.name,
                                  totalPrice = customer.price)

            call.respond(HttpStatusCode.OK,totalprice)
        }
        else {
            call.respondText("Not found user", status = HttpStatusCode.NotFound)
        }



    }
    delete("/delete/{id?}") {
        val id = call.parameters["id"]
        if(id != null) {
            val id1 = id.toInt()
            val collect = DataBaseConnectivity.collection
            val customer = collect.deleteOne(HotelDetails::id eq id1)

                if(customer.deletedCount > 0) {
                    call.respond(HttpStatusCode.OK,"Deleted successfully of id $id1")
                }
                else {
                    call.respond(HttpStatusCode.InternalServerError,"Not deleted")
                }

        }
        else {
            call.respond(HttpStatusCode.BadRequest,"Not found id")
        }
    }
    put("/{id?}") {
        val id = call.parameters["id"]
        if(id != null) {
            val id1 = id.toInt()
            val updated = call.receive<HotelDetails>()
            val result = DataBaseConnectivity.collection.updateOne(HotelDetails::id eq id1,
                    combine( setValue(HotelDetails::name,updated.name),
                             setValue(HotelDetails::noOfNights,updated.noOfNights) )
            )
            if(result.modifiedCount == 0L) {
                call.respond(HttpStatusCode.InternalServerError,"Not updated")
            }
            else {
                call.respond(HttpStatusCode.OK,"Updated")
            }

        }
        else {
            call.respond(HttpStatusCode.BadGateway,"Id not found")
        }
    }
    patch("/{id?}") {
        val id = call.parameters["id"]
        if(id != null) {
            val id1 = id.toInt()
            val updated = call.receive<HotelDetails>()
            val existing = DataBaseConnectivity.collection.findOne(HotelDetails::id eq id1)

            if(existing != null) {
                val merged = existing.copy(
                    name = updated.name ?: existing.name,
                    id = updated.id ?: existing.id,
                    noOfNights = updated.noOfNights ?: existing.noOfNights,
                    price = updated.price ?: existing.price
                )

                if (merged != null) {
                    DataBaseConnectivity.collection.updateOneById(id1,merged)
                    call.respond(HttpStatusCode.OK,"Updated successfully")
                }
                else {
                    call.respond(HttpStatusCode.NotFound,"No merged data found")
                }
            }




        }
    }


}
