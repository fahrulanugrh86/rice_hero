package com.example.capstone.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.capstone.api.ApiService
import com.example.capstone.api.WeatherResponse
import com.example.capstone.data.WeatherPreference
import com.example.capstone.database.PestDiseaseDao
import com.example.capstone.model.PlantDisease
import com.example.capstone.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository private constructor(
    private val apiService: ApiService,
    private val pref : WeatherPreference,
){

    private val _currentWeather = MutableLiveData<WeatherResponse>()
    val currentWeather : LiveData<WeatherResponse> = _currentWeather

    fun getCurrentWeather(location : Location) {
        val client = apiService.getCurrentWeather(location.latitude, location.longitude, "9e3120b9fe7fd347f870ba5d971f7b9c")
        client.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    _currentWeather.value = response.body()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("Tag22", call.toString())
            }
        })
    }

    suspend fun saveWeather(weather: Weather) {
        pref.saveWeather(weather)
    }

    fun getWeather() : LiveData<Weather> {
        return pref.getWeather().asLiveData()
    }


    companion object {
        @Volatile
        private var instance : WeatherRepository? = null
        fun getInstance(apiService: ApiService, pref : WeatherPreference) : WeatherRepository =
            instance ?: synchronized(this) {
                instance ?: WeatherRepository(apiService, pref)
            }.also { instance = it }
    }
}