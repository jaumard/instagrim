package com.jaumard.instagrim.ui.login

import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.ui.login.navigation.LoginNavigator
import com.jaumard.instagrim.utils.RxSchedulerProvider
import com.nhaarman.mockito_kotlin.*
import dagger.Lazy
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness
import retrofit2.HttpException
import utils.TrampolineSchedulerRule

class LoginViewModelTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @get:Rule
    internal val rxRule = TrampolineSchedulerRule()

    @Mock
    internal lateinit var rxSchedulerProvider: RxSchedulerProvider

    @Mock
    internal lateinit var userRepository: UserRepository

    @Mock
    internal lateinit var loginNavigator: LoginNavigator

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(Lazy { userRepository }, rxSchedulerProvider, loginNavigator)
    }

    @Test
    fun login() {
        loginViewModel.login()
        verify(loginNavigator).loginFromInstagram()
    }

    @Test
    fun manageInstagramCode() {
        val user: User = mock()
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        given(userRepository.retrieve(eq("test"))).willReturn(Single.just(user))
        val code = "test"
        loginViewModel.manageInstagramCode(code, null, null)

        ioScheduler.triggerActions()
        assertTrue(loginViewModel.isLoading.get())
        mainScheduler.triggerActions()
        assertSame(user, loginViewModel.user.get())
        assertFalse(loginViewModel.isLoading.get())
    }

    @Test
    fun manageInstagramCodeNetworkError() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        given(userRepository.retrieve(eq("test"))).willReturn(Single.error(mock<HttpException>()))
        val code = "test"
        loginViewModel.manageInstagramCode(code, null, null)

        ioScheduler.triggerActions()
        assertTrue(loginViewModel.isLoading.get())
        mainScheduler.triggerActions()
        assertNull(loginViewModel.user.get())
        assertFalse(loginViewModel.isLoading.get())
        verify(loginNavigator).showGenericError()
    }

    @Test
    fun manageInstagramCodeError() {
        val error = "error"
        val errorMessage = "message"
        loginViewModel.manageInstagramCode(null, error, errorMessage)
        verifyZeroInteractions(userRepository)
        verify(loginNavigator).showMessage(eq(errorMessage))
    }

    @Test
    fun checkUserConnexion() {
        val user = mock<User>()
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        given(userRepository.retrieve(isNull())).willReturn(Single.just(user))
        loginViewModel.checkUserConnexion()

        ioScheduler.triggerActions()
        assertTrue(loginViewModel.isLoading.get())
        mainScheduler.triggerActions()
        assertFalse(loginViewModel.isLoading.get())
        assertSame(user, loginViewModel.user.get())
    }

    @Test
    fun checkUserConnexionUnAuthorized() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        given(userRepository.retrieve(isNull())).willReturn(Single.error(mock<UnAuthorizedException>()))
        loginViewModel.checkUserConnexion()

        ioScheduler.triggerActions()
        assertTrue(loginViewModel.isLoading.get())
        mainScheduler.triggerActions()
        assertFalse(loginViewModel.isLoading.get())
        assertNull(loginViewModel.user.get())
        verifyZeroInteractions(loginNavigator)
    }

    @Test
    fun checkUserConnexionHttpError() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        given(userRepository.retrieve(isNull())).willReturn(Single.error(mock<HttpException>()))
        loginViewModel.checkUserConnexion()

        ioScheduler.triggerActions()
        assertTrue(loginViewModel.isLoading.get())
        mainScheduler.triggerActions()
        assertFalse(loginViewModel.isLoading.get())
        assertNull(loginViewModel.user.get())
        verify(loginNavigator).showGenericError()
    }

}