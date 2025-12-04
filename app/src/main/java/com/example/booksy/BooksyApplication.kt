package com.example.booksy

import android.app.Application
import com.example.booksy.network.RetrofitClient

class BooksyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}