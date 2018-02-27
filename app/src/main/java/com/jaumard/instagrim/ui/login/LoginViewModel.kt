package com.jaumard.instagrim.ui.login

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.ui.login.navigation.GalleryAccessor
import com.jaumard.instagrim.ui.login.navigation.LoginNavigator
import com.jaumard.instagrim.utils.RxSchedulerProvider
import dagger.Lazy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val userRepository: Lazy<UserRepository>,
                                         private val rxSchedulerProvider: RxSchedulerProvider,
                                         private val loginNavigator: LoginNavigator) : ViewModel() {
    private val bag = CompositeDisposable()
    val isLoading = ObservableBoolean()
    val user = ObservableField<User>()
    val galleryAccessor: GalleryAccessor by lazy { loginNavigator }

    override fun onCleared() {
        super.onCleared()
        bag.dispose()
    }

    fun login() {
        loginNavigator.loginFromInstagram()
    }

    fun manageInstagramCode(code: String?, error: String?, errorMessage: String?) {
        if (error != null) {
            loginNavigator.showMessage(errorMessage!!)
        } else if (code != null) {
            loginFromCode(code)
        }
    }

    private fun loginFromCode(code: String) {
        userRepository.get().retrieve(code)
                .doOnSubscribe { isLoading.set(true) }
                .doOnDispose { isLoading.set(false) }
                .subscribeOn(rxSchedulerProvider.io())
                .observeOn(rxSchedulerProvider.main())
                .subscribeBy(onSuccess = {
                    isLoading.set(false)
                    user.set(it)
                }, onError = {
                    Timber.e(it)
                    isLoading.set(false)
                    loginNavigator.showGenericError()
                })
                .addTo(bag)
    }

    fun checkUserConnexion() {
        userRepository.get().retrieve()
                .doOnSubscribe { isLoading.set(true) }
                .doOnDispose { isLoading.set(false) }
                .subscribeOn(rxSchedulerProvider.io())
                .observeOn(rxSchedulerProvider.main())
                .subscribeBy(onSuccess = {
                    isLoading.set(false)
                    user.set(it)
                }, onError = {
                    isLoading.set(false)
                    if (it !is UnAuthorizedException) {
                        Timber.e(it)
                        loginNavigator.showGenericError()
                    }
                })
                .addTo(bag)
    }
}