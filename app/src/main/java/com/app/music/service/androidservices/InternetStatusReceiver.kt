package com.app.music.service.androidservices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.app.music.view.login.LOG_TAG

class InternetStatusReceiver:BroadcastReceiver() {
    private var internetCallback: InternetCallback? = null

    companion object{
        fun getInstance() = InternetStatusReceiver()
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val noConnection = intent!!.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        var stat = InternetStatus.ONLINE
        if(noConnection)
            stat=InternetStatus.OFFLINE
        Log.d(LOG_TAG, "inReceiver(): internet status -> $stat")
        internetCallback?.onStatusChanged(stat)
    }

    fun setOnCallbackReceivedListener(internetCallback: InternetCallback) {
        this.internetCallback = internetCallback
    }
}
enum class InternetStatus{
    ONLINE, OFFLINE
}
interface InternetCallback{
    fun onStatusChanged(status: InternetStatus)
}