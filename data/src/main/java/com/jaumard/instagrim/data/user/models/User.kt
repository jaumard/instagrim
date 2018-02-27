package com.jaumard.instagrim.data.user.models

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
@SuppressLint("ParcelCreator")
data class User(val token: String, @PrimaryKey val id: String, val userName: String, val fullName: String, val avatar: String?) : Parcelable