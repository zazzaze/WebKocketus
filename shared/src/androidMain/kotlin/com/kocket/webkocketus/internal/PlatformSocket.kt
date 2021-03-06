package com.kocket.webkocketus.internal

import com.kocket.webkocketus.public.WebKocketusLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString.Companion.decodeBase64

internal actual class PlatformSocket actual constructor(url: String, private val logger: WebKocketusLogger) {
    private val socketEndpoint = url
    private var webSocket: WebSocket? = null
    actual fun openSocket(listener: PlatformSocketListener) {
        val socketRequest = Request.Builder().url(socketEndpoint).build()
        val webClient = OkHttpClient().newBuilder().build()
        webSocket = webClient.newWebSocket(
            socketRequest,
            object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) = listener.onOpen()
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) = listener.onFailure(t)
                override fun onMessage(webSocket: WebSocket, text: String) = listener.onMessage(ByteArray(1))
                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) = listener.onClosing(code, reason)
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = listener.onClosed(code, reason)
            }
        )
    }
    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.close(code, reason)
        webSocket = null
    }

    actual fun sendBytes(bytes: ByteArray, callback: ((Throwable?) -> Unit)?) {
        bytes.decodeToString().decodeBase64()?.let {
            webSocket?.send(bytes = it)
        }?: logger.verbose("cannot decode ByteArray to ByteString")
    }

    actual fun sendString(string: String, callback: ((Throwable?) -> Unit)?) {
        webSocket?.send(text = string)
    }
}