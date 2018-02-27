package com.jaumard.instagrim.utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RxSchedulerProvider @Inject constructor() {

    fun io() = Schedulers.io()
    fun main() = AndroidSchedulers.mainThread()!!

}