package com.jaumard.instagrim.data

import android.arch.persistence.room.EmptyResultSetException
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jaumard.instagrim.data.media.dao.MediaDao
import com.jaumard.instagrim.data.media.models.Media

import com.jaumard.instagrim.data.user.dao.UserDao
import com.jaumard.instagrim.data.user.models.User
import junit.framework.Assert.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseFunctionalTest {
    private lateinit var userDao: UserDao
    private lateinit var mediaDao: MediaDao
    private lateinit var mDb: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = mDb.userDao()
        mediaDao = mDb.mediaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        mDb.close()
    }

    @Test
    fun user() {
        val user = User("token", "id", "username", "fullname", "avatar")
        userDao.insert(user)

        val dbUser = userDao.get().test().assertNoErrors().assertComplete().values()[0]
        assertEquals(user, dbUser)

        userDao.delete()
        userDao.get().test().assertError(EmptyResultSetException::class.java).assertTerminated()

    }

    @Test
    fun media() {
        val media1 = Media("id1", "thumb", "url", 1, 2, true)
        val media2 = Media("id2", "thumb", "url", 1, 2, true)
        val mediaDuplicate = Media("id1", "thumbDup", "urlDup", 1, 2, true)
        mediaDao.insert(media1, media2, mediaDuplicate)

        val media = mediaDao.getAll().test().assertNoErrors().assertComplete().values()[0]
        assertEquals(2, media.size)
        assertEquals(media2, media[0])
        assertEquals(mediaDuplicate, media[1])

        mediaDao.deleteAll()
        assertEquals(0, mediaDao.getAll().test().assertNoErrors().assertComplete().values()[0].size)
    }

}
