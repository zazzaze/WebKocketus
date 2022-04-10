package com.kocket.webkocketus.public

import com.kocket.webkocketus.internal.PlatformSocket
import com.kocket.webkocketus.internal.PlatformSocketListener
import com.kocket.webkocketus.internal.WebKocketusLoggerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Public web socket object for handle socket state, received message data and send messages by
 * web socket.
 *
 * Each fun must be called from a single coroutine scope to avoid getting error of mutable frozen
 * object.
 */
public class WebKocketus(
    /**
     * The URI for which the connection will take place. Must contains wss scheme.
     */
    uri: String,
    /**
     * Coroutine scope from which provides flow messages.
     */
    private val scope: CoroutineScope,
    /**
     * Logger for log information about socket working.
     *
     * By default, the wrapper is used over kermit.Logger
     */
    logger: WebKocketusLogger = WebKocketusLoggerImpl(tag = "webkocketus")
) {

    private val platformSocket = PlatformSocket(url = uri, logger = logger)

    private val _stateSharedFlow = MutableStateFlow(SocketState.CLOSED)

    /**
     * Flow for handle socket state changing.
     */
    val socketStateFlow: StateFlow<SocketState> = _stateSharedFlow

    private val _messagesFlow = MutableSharedFlow<ByteArray>()

    /**
     * Flow for handle messages as byte array.
     */
    val messageFlow: SharedFlow<ByteArray> = _messagesFlow

    /**
     * Call to connect to web socket by specified URI.
     *
     * This fun can throw exception if you will try to connect when socket already connected
     */
    fun connect() {
        platformSocket.openSocket(listener = socketListener)
        scope.launch {
            _stateSharedFlow.emit(value = SocketState.CONNECTING)
        }
    }

    /**
     * Call to disconnect from web socket.
     */
    fun disconnect() {
        platformSocket.closeSocket(code = SocketCloseCode.NORMAL.code, "Client initiated the closure")
    }

    /**
     * Will send bytes by web socket.
     * @param bytes array of bytes to send by web socket
     */
    fun sendBytesMessage(bytes: ByteArray) {
        platformSocket.sendBytes(bytes = bytes, callback = null)
    }

    /**
     * Will send bytes by web socket.
     * @param bytes array of bytes to send by web socket
     * @param callback transmits an error received during sending a message via web socket
     */
    fun sendBytesMessage(bytes: ByteArray, callback: (Throwable?) -> Unit) {
        platformSocket.sendBytes(bytes = bytes, callback = callback)
    }

    /**
     * Will send text message by web socket.
     * @param string content of message.
     */
    fun sendStringMessage(string: String) {
        platformSocket.sendString(string = string, callback = null)
    }

    /**
     * Will send text message by web socket.
     * @param string content of message.
     * @param callback transmits an error received during sending a message via web socket
     */
    fun sendStringMessage(string: String, callback: (Throwable?) -> Unit) {
        platformSocket.sendString(string = string, callback = callback)
    }

    private val socketListener: PlatformSocketListener = object : PlatformSocketListener {
        override fun onOpen() {
            scope.launch {
                _stateSharedFlow.emit(SocketState.CONNECTED)
            }
        }
        override fun onFailure(t: Throwable) {
            scope.launch {
                _stateSharedFlow.emit(SocketState.CLOSED)
            }
        }

        override fun onMessage(msgBytes: ByteArray) {
            scope.launch {
                _messagesFlow.emit(msgBytes)
            }
        }
        override fun onClosing(code: Int, reason: String) {
            scope.launch {
                _stateSharedFlow.emit(SocketState.CLOSING)
            }
        }
        override fun onClosed(code: Int, reason: String) {
            scope.launch {
                _stateSharedFlow.emit(SocketState.CLOSED)
            }
        }
    }

    /**
     * Codes for close socket connection.
     */
    private enum class SocketCloseCode(val code: Int) {
        /**
         * Normal close code, for disconnect initiated by client.
         */
        NORMAL(1000)
    }
}