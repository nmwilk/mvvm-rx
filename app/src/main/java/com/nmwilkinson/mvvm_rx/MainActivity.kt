package com.nmwilkinson.mvvm_rx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        subscriptions.add(viewModel().getSubmitButtonStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    submitButton.isEnabled = it
                }, {
                    Toast.makeText(this@MainActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }))

        subscriptions.add(viewModel().getUiState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ state ->
                    progressView.visibility = if (state.working) View.VISIBLE else View.GONE
                    countryField.text = state.country
                }, {
                    throw IllegalStateException("$it")
                }))

        subscriptions.add(valueField.textChanges()
                .subscribe { charSequence -> viewModel().ipTyped(charSequence.toString()) })

        subscriptions.add(submitButton.clicks()
                .subscribe { viewModel().submitClicked(valueField.text.toString()) })

    }

    override fun onPause() {
        super.onPause()

        subscriptions.clear()
    }

    private fun viewModel() = (application as MvvmApp).getViewModel()
}
