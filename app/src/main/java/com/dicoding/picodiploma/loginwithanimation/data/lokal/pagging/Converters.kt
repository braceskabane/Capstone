package com.dicoding.picodiploma.loginwithanimation.data.lokal.pagging

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String?> {
        val listType = object : TypeToken<List<String?>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String?>?): String {
        return gson.toJson(list)
    }
}