package com.kocket.webkocketus.internal

import com.kocket.webkocketus.public.WebKocketusLogger
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.NSObject
import platform.posix.memcpy

internal actual class PlatformSocket actual constructor(url: String, private val logger: WebKocketusLogger) {
    private val socketEndpoint = NSURL.URLWithString(url)!!
    private var webSocket: NSURLSessionWebSocketTask? = null


    actual fun openSocket(listener: PlatformSocketListener) {
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    logger.verbose("Socket did opened")
                    listener.onOpen()
                }
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    logger.verbose("Socket did closed")
                    listener.onClosed(didCloseWithCode.toInt(), reason.toString())
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )

        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)
        listenMessages(listener)
        webSocket?.resume()
    }

    private fun listenMessages(listener: PlatformSocketListener) {
        webSocket?.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    logger.verbose(message = "Did receive error", error = Throwable(nsError.description))
                    listener.onFailure(Throwable(nsError.description))
                }
                message != null -> {
                    logger.verbose(message = "Did receive message: ${message.string}")
                    message.string?.encodeToByteArray()?.let {
                        listener.onMessage(msgBytes = it)
                    }
                    message.data?.toByteArray()?.let {
                        listener.onMessage(msgBytes = it)
                    }
                }
            }
            listenMessages(listener)
        }
    }
    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.cancelWithCloseCode(code.toLong(), null)
        webSocket = null
    }

    actual fun sendBytes(bytes: ByteArray, callback: ((Throwable?) -> Unit)?) {
        val data = bytes.toNSData()
        val dataMessage = NSURLSessionWebSocketMessage(data)
        webSocket?.sendMessage(dataMessage) { err ->
            err?.let { error ->
                val throwable = Throwable(error.description)
                logger.error("Did receive error on bytes message sent", throwable)
                callback?.let { it(throwable) }
            }
        }
    }

    actual fun sendString(string: String, callback: ((Throwable?) -> Unit)?) {
        val message = NSURLSessionWebSocketMessage(string)
        webSocket?.sendMessage(message = message) { err ->
            err?.let { error ->
                val throwable = Throwable(error.description)
                logger.error("Did receive error on string message sent", throwable)
                callback?.let { it(throwable) }
            }
        }
    }

    private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }

    private fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
    }
}