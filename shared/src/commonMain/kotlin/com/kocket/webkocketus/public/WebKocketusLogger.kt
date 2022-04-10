package com.kocket.webkocketus.public

interface WebKocketusLogger {
    /**
     * Most detailed level of logging
     * @param message message to log
     */
    fun verbose(message: String)

    /**
     * Most detailed level of logging
     * @param message message which will printed in log
     * @param error error which will described in log
     */
    fun verbose(message: String, error: Throwable)

    /**
     * Describes information for debug
     * @param message message which will printed in log
     */
    fun debug(message: String)

    /**
     * Describes information for debug
     * @param message message which will printed in log
     * @param error error which will described in log
     */
    fun debug(message: String, error: Throwable)

    /**
     * Describes information which will actual for business processes
     * @param message message which will printed in log
     */
    fun warning(message: String)

    /**
     * Describes information which will actual for business processes
     * @param message message which will printed in log
     * @param error error which will described in log
     */
    fun warning(message: String, error: Throwable)

    /**
     * Describes information about unexpected errors
     * @param message message which will printed in log
     */
    fun error(message: String)

    /**
     * Describes information about unexpected errors
     * @param message message which will printed in log
     * @param error error which will described in log
     */
    fun error(message: String, error: Throwable)

    /**
     * Describes information about fatal errors
     * @param message message which will printed in log
     */
    fun fatal(message: String)

    /**
     * Describes information about fatal errors
     * @param message message which will printed in log
     * @param error error which will described in log
     */
    fun fatal(message: String, error: Throwable)
}