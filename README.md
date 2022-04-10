# WebKocketus
<img src="webkocketus-logo.png" width="200px" height="200px"/>

[![Kotlin](https://img.shields.io/badge/kotlin-1.6.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
![WEbSockets](https://img.shields.io/badge/Network-WebSockets-green)
![KMM](https://img.shields.io/badge/KMM-Android%20%26%20iOS-yellow)

WebKocketus is the library for work with WebSockets on Android or iOS. Under the hood it uses NSURLSessionWebSocketTask for iOS and okhttp3.WebSocket for Android

## Features
Now WebKocketus allows you to:
 -  send messages as String or ByteArray. 
 - receive messages as ByteArray using Coroutines Flow

 ## Limitations
 WebKocketus works only with URI `wss` scheme, like `wss://localhost:8080`

 ## What`s new
 You can research changes in [Changelog file](Changelog.md)
