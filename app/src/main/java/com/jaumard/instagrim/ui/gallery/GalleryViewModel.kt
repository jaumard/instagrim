package com.jaumard.instagrim.ui.gallery

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.media.MediaRepository
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.ui.gallery.navigation.FullScreenAccessor
import com.jaumard.instagrim.ui.gallery.navigation.GalleryNavigator
import com.jaumard.instagrim.ui.gallery.navigation.MediaDataListener
import com.jaumard.instagrim.utils.RxSchedulerProvider
import dagger.Lazy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class GalleryViewModel @Inject constructor(private val userRepository: Lazy<UserRepository>,
                                           private val mediaRepository: MediaRepository,
                                           private val rxSchedulerProvider: RxSchedulerProvider,
                                           private val galleryNavigator: GalleryNavigator) : ViewModel(), MediaDataListener {

    private val bag = CompositeDisposable()
    private var nextId: String? = null
    val fullScreenAccessor: FullScreenAccessor by lazy { galleryNavigator }
    val isLoading = ObservableBoolean()
    val media: ObservableField<List<Media>> = ObservableField(ArrayList())
    val user = ObservableField<User?>()

    init {
        user.addOnPropertyChangedCallback(object : android.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(p0: android.databinding.Observable?, p1: Int) {
                refresh()
            }
        })
    }

    override fun getDataCount(): Int {
        return media.get().size
    }

    override fun isLoading(): Boolean {
        return isLoading.get()
    }

    override fun onLoadMore() {
        if (nextId != null) {
            loadUserMedia()
        }
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }

    fun refresh() {
        nextId = null
        media.set(ArrayList())
        loadUserMedia()
    }

    private fun loadUserMedia() {
        mediaRepository.retrieve(nextId)
                .doOnSubscribe { isLoading.set(true) }
                .subscribeOn(rxSchedulerProvider.io())
                .observeOn(rxSchedulerProvider.main())
                .subscribeBy(onError = {
                    Timber.e(it)
                    isLoading.set(false)
                    if (it is UnAuthorizedException) {
                        galleryNavigator.goToLogin()
                        galleryNavigator.showMessage(R.string.error_logged_out)
                    } else {
                        galleryNavigator.showError(it)
                    }
                }, onNext = {
                    isLoading.set(false)
                    nextId = it.nextMediaId
                    media.set(it.media)
                })
                .addTo(bag)
    }

    fun logout() {
        userRepository.get().logout()
                .subscribeOn(rxSchedulerProvider.io())
                .observeOn(rxSchedulerProvider.main())
                .subscribeBy(onError = {
                    Timber.e(it)
                    galleryNavigator.showError(it)
                }, onComplete = {
                    galleryNavigator.goToLogin()
                })
                .addTo(bag)

    }
}