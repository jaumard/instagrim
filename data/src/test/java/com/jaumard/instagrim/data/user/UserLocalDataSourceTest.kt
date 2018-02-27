package com.jaumard.instagrim.data.user

import com.jaumard.instagrim.data.user.dao.UserDao
import com.jaumard.instagrim.data.user.models.User
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import dagger.Lazy
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class UserLocalDataSourceTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var userDao: UserDao

    private lateinit var userLocalDataSource: UserLocalDataSource

    @Before
    fun setUp() {
        userLocalDataSource = UserLocalDataSource(Lazy { userDao })
    }

    @Test
    fun retrieve() {
        val user = mock<User>()
        given(userDao.get()).willReturn(Single.just(user))
        userLocalDataSource.retrieve().test().assertNoErrors().assertComplete().assertTerminated().assertValue(user)
    }

    @Test
    fun create() {
        val user = mock<User>()
        userLocalDataSource.create(user).test().assertNoErrors().assertComplete().assertTerminated()
        verify(userDao).insert(eq(user))
    }

    @Test
    fun delete() {
        userLocalDataSource.delete().test().assertNoErrors().assertComplete().assertTerminated()
        verify(userDao).delete()
    }

}