package com.jaumard.instagrim.data.user

import com.jaumard.instagrim.data.user.mapper.UserMapper
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.auth.InstagramUserNetworkDataSource
import com.jaumard.instagrim.network.user.dto.LoginResponse
import com.nhaarman.mockito_kotlin.*
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class UserRepositoryTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var userNetworkDataSource: InstagramUserNetworkDataSource
    @Mock
    internal lateinit var userLocalDataSource: UserLocalDataSource
    @Mock
    internal lateinit var userMapper: UserMapper

    private lateinit var userRepository: UserRepository
    @Before
    fun setUp() {
        userRepository = UserRepository(Lazy { userNetworkDataSource }, Lazy { userLocalDataSource }, Lazy { userMapper })
    }

    @Test
    fun retrieveWithCache() {
        val user = mock<User>()
        val userResponse = mock<LoginResponse>()
        val code = "code"
        //first call useless just to put user into the repostiory cache
        given(userNetworkDataSource.login(eq(code))).willReturn(Single.just(userResponse))
        given(userMapper.mapFromDto(eq(userResponse))).willReturn(user)
        //FIXME should be eq(user) not any
        given(userLocalDataSource.create(any())).willReturn(Completable.complete())
        userRepository.retrieve(code).test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)

        //With or without code now we should retrieve user from cache
        userRepository.retrieve(code).test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)
        userRepository.retrieve().test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)
        verifyNoMoreInteractions(userLocalDataSource)
        verifyNoMoreInteractions(userNetworkDataSource)
        verifyNoMoreInteractions(userMapper)
    }

    @Test
    fun retrieveWithCode() {
        val user = mock<User>()
        val userResponse = mock<LoginResponse>()
        val code = "code"
        given(userNetworkDataSource.login(eq(code))).willReturn(Single.just(userResponse))
        given(userMapper.mapFromDto(eq(userResponse))).willReturn(user)
        //FIXME should be eq(user) not any
        given(userLocalDataSource.create(any())).willReturn(Completable.complete())
        userRepository.retrieve(code).test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)
    }

    @Test
    fun retrieveWithoutCode() {
        val user = mock<User>()
        given(userLocalDataSource.retrieve()).willReturn(Single.just(user))
        userRepository.retrieve().test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)
    }

    @Test
    fun logout() {
        given(userLocalDataSource.delete()).willReturn(Completable.complete())
        userRepository.logout().test().assertNoErrors().assertComplete().assertTerminated()
    }

}