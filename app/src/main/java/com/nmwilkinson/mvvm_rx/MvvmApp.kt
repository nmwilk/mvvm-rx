package com.nmwilkinson.mvvm_rx

import android.app.Application

class MvvmApp : Application() {
    private lateinit var viewModel: ViewModel

    override fun onCreate() {
        super.onCreate()

        viewModel = ViewModel(Api())
    }

    fun getViewModel() = viewModel
}