package com.app.music.exceptions

class LoginException(message: String = "Could not login because of wrong credentials"): Exception(message)