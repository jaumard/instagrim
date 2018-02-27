package com.jaumard.instagrim.data.media.models

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
@SuppressLint("ParcelCreator")
data class Media(@PrimaryKey val id: String, val thumbnail: String, val url: String, val likes: Int, val comments: Int, val userLike: Boolean) : Parcelable