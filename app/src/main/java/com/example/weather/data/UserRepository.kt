package com.example.weather.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData : LiveData<List<User>> = userDao.readALLData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

}