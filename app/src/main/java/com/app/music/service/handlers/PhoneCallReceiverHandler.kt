package com.app.music.service.handlers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.music.service.androidservices.PhoneCallCallback
import com.app.music.service.androidservices.PhoneCallReceiver
import com.app.music.view.login.LOG_TAG


object PhoneCallReceiverHandler {
        private const val requestCode = 0
        private val phonecallReceiver = PhoneCallReceiver()
        private val _mutableIsCalling = MutableLiveData<Boolean>()
        val isCalling : LiveData<Boolean> = _mutableIsCalling

        fun registerPhonecallReceiver(context: Context, activity: Activity) {
            Log.d(LOG_TAG,"Phone call registered")
            phonecallReceiver.setOnCallbackReceivedListener(object: PhoneCallCallback {
                override fun onIncomingPhoneCall() {
                    Log.d(LOG_TAG,"incoming call")
                    Toast.makeText(context, "Music stopped because a phone call is incoming",
                        Toast.LENGTH_LONG).show()
                    _mutableIsCalling.value = true
                }

                override fun onPhoneCallFinished() {
                    Log.d(LOG_TAG,"call finished")
                    Toast.makeText(context, "Music resumed because the phone call was finished",
                        Toast.LENGTH_LONG).show()
                    _mutableIsCalling.value = false
                }

            })
            requestPhoneStatePermission(activity)

            activity.registerReceiver(
                phonecallReceiver,
                IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            )

        }

        fun unregisterPhoneCallReceiver(activity: Activity){
            activity.unregisterReceiver(phonecallReceiver)
        }

        private fun requestPhoneStatePermission(activity: Activity) {
            val permissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )

            ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
            )
        }
}