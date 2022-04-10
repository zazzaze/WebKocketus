package com.kocket.webkocketus.public

/**
 * This object describes web socket state at current time.
 */
enum class SocketState {
    /**
     * When web socket is connected, it can send and receive messages.
     */
    CONNECTED,

    /**
     * When web socket is connecting, it cannot send and receive messages.
     */
    CONNECTING,

    /**
     * When web socket is start closing, it cannot send and receive messages.
     */
    CLOSING,

    /**
     * When web socket is closed, it cannot send and receive messages.
     */
    CLOSED
}