package com.nmwilkinson.mvvm_rx

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class ViewModel(val api: Api) {
    private val ipRegex = Regex("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
    private val ipAddress: BehaviorSubject<String> = BehaviorSubject.create()
    private val ipSubmitted: BehaviorSubject<String> = BehaviorSubject.create()
    private val uiState: BehaviorSubject<State> = BehaviorSubject.create()

    fun ipTyped(value: String) {
        if (value.isNotEmpty()) {
            ipAddress.onNext(value)
            uiState.onNext(State(false, false, ""))
        }
    }

    fun getUiState(): Observable<State> {
        return Observable.merge(
                ipSubmitted.flatMap { api.ipDetails(it).toObservable() }.map { State(false, true, it) },
                uiState)
                .startWith(State())
    }

    fun getSubmitButtonStatus(): Observable<Boolean> {
        return Observable.combineLatest(ipAddress.map { ip -> validateIp(ip) },
                uiState.map { !it.working }, BiFunction { t1: Boolean, t2: Boolean -> t1 && t2 })
                .startWith(false)
    }

    fun submitClicked(ip: String) {
        uiState.onNext(State(true, false, ""))
        ipSubmitted.onNext(ip)
    }

    fun validateIp(value: String): Boolean {
        return ipRegex.matches(value)
    }

    data class State(val working: Boolean = false, val validIp: Boolean = false, val country: String = "")
}
