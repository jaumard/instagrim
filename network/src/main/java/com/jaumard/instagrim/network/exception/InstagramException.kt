package com.jaumard.instagrim.network.exception

class InstagramException(val code: Int, override val message: String?) : RuntimeException() {
}