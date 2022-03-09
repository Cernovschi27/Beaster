package com.app.music.service

import java.util.*

interface IPlayerService {

    fun searchTrack(query: String): Observable
}