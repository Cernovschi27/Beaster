package com.app.music.service.androidservices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.app.music.view.login.LOG_TAG

class PhoneCallReceiver: BroadcastReceiver() {
    private var phonecallCallback: PhoneCallCallback? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(LOG_TAG, "inReceiver(): phone call")
        val state =
            intent?.getStringExtra(TelephonyManager.EXTRA_STATE)


        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            phonecallCallback?.onIncomingPhoneCall()
        else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            phonecallCallback?.onPhoneCallFinished()
    }

    fun setOnCallbackReceivedListener(phoneCallCallback: PhoneCallCallback) {
        this.phonecallCallback = phoneCallCallback
    }
}



interface PhoneCallCallback{
    fun onIncomingPhoneCall()
    fun onPhoneCallFinished()
}