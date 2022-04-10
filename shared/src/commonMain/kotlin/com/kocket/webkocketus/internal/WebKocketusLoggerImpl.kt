package com.kocket.webkocketus.internal

import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import com.kocket.webkocketus.public.WebKocketusLogger

internal class WebKocketusLoggerImpl(tag: String): WebKocketusLogger {
    private val logger = Logger(config = LoggerConfig.default, tag = tag)

    override fun verbose(message: String) {
        logger.v(message = message)
    }

    override fun verbose(message: String, error: Throwable) {
        logger.v(message = message, throwable = error)
    }

    override fun debug(message: String) {
        logger.d(message = message)
    }

    override fun debug(message: String, error: Throwable) {
        logger.d(message = message, throwable = error)
    }

    override fun warning(message: String) {
        logger.w(message = message)
    }

    override fun warning(message: String, error: Throwable) {
        logger.w(message = message, throwable = error)
    }

    override fun error(message: String) {
        logger.e(message = message)
    }

    override fun error(message: String, error: Throwable) {
        logger.e(message = message, throwable = error)
    }

    override fun fatal(message: String) {
        logger.a(message = message)
    }

    override fun fatal(message: String, error: Throwable) {
        logger.a(message = message, throwable = error)
    }
}