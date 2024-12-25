package com.example.timely1.models

data class Entry(
    val id: Int,
    val name: String,
    val secondName: String,
    val thirdName: String,
    val number: String,
    val date:String,
    val time: String,
    val price: Double,
    val additional:String,
    var isDone:String
)
