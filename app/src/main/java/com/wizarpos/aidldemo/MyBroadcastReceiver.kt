package com.wizarpos.aidldemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver(){
    companion object{
        const val TAG = "MyBroadcastReceiver"
    }
    override fun onReceive(context: Context?,intent: Intent?) {
        Log.d(TAG,"==>onReceive==>")
        intent?.let {
            val action = it.action
            Log.d(TAG,"==>action==>$action")
            if(action == "com.wizarpos.mpay.LIFTING_INTENTION"){
                val data = it.getStringExtra("data")
                /*
                {"AdditionalInfo":"{\"bankMerchantID\":\"100000000000001\",\"bankTerminalID\":\"10000001\"}",
                "AuthCode":"087639","CardBrand":"UNION PAY","CardNum":"622611******8005","CurrencyCode":"458",
                "EmvAid":"A000000333010102","EmvAppName":"PBOC CREDIT","EmvCryptogram":"CBEC94130527821B",
                "EmvTVR":"0000000000","EntryMode":4,"ExpiryDate":"2509","MID":"007916-06-25-59","MerchantName":"GSC",
                "OriTransId":"","RRN":"001740717526","RespCode":"00","RespDesc":"00 APPROVED","TID":"007916-2-165",
                "TraceNum":"150835","TransAmount":"000000000100","TransDate":"20250228","TransResult":true,
                "TransTime":"123940","TransType":"Purchase"}
                 */
                Log.d(TAG,"==>抬杆通知==>")
                Toast.makeText(context,"抬杆通知",Toast.LENGTH_LONG).show()
                Log.d(TAG,"==>data==>$data")
            }
        }
    }

}