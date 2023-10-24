package com.example.blueleaf

import com.google.gson.annotations.SerializedName
import java.io.Serial
import java.io.Serializable

data class Plant (
    val description: String,
    val difficulty: String,
    val humidity: String,
    val image: String,
    val name: String,
    val name_eng: String,
    val sunlight: String,
    val temperature: String,
    val water: String
) : Serializable