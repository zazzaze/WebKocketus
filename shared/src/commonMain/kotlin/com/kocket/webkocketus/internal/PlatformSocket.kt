package com.kocket.webkocketus.internal

import com.kocket.webkocketus.public.WebKocketusLogger

internal expect class PlatformSocket(
    url: String,
    logger: WebKocketusLogger
) {
    fun openSocket(listener: PlatformSocketListener)
    fun closeSocket(code: Int, reason: String)
    fun sendBytes(bytes: ByteArray, callback: ((Throwable?) -> Unit)?)
    fun sendString(string: String, callback: ((Throwable?) -> Unit)?)
}

interface PlatformSocketListener {
    fun onOpen()
    fun onFailure(t: Throwable)
    fun onMessage(msgBytes: ByteArray)
    fun onClosing(code: Int, reason: String)
    fun onClosed(code: Int, reason: String)
}