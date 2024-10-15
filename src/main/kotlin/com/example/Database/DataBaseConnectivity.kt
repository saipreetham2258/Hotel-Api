package com.example.Database

import com.example.module.HotelDetails
import com.mongodb.client.MongoCollection
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object DataBaseConnectivity {
        val string = "mongodb://localhost:27017"
        val client = KMongo.createClient(string)
        val dataBase = client.getDatabase("KotlinDataBase")
        val collection  = dataBase.getCollection<HotelDetails>()
}