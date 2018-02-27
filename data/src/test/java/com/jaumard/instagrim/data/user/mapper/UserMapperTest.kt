package com.jaumard.instagrim.data.user.mapper

import com.jaumard.instagrim.network.user.dto.LoginResponse
import com.jaumard.instagrim.network.user.dto.UserResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {
    @Test
    fun mapFromDto() {
        val userMapper = UserMapper()

        val user = userMapper.mapFromDto(LoginResponse("token", UserResponse("id", "userName", "fullName", "avatar")))
        assertEquals("token", user.token)
        assertEquals("id", user.id)
        assertEquals("userName", user.userName)
        assertEquals("fullName", user.fullName)
        assertEquals("avatar", user.avatar)
    }

}