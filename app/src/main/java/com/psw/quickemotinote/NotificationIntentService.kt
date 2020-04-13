package com.psw.quickemotinote

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.item_emoti_list.view.*

class NotificationIntentService : IntentService("notificationIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        when (intent!!.action) {
            "next" -> {
                val msg = Util.getNextEmoji(this)
                if (msg.isNullOrBlank()) return

                Util.setClipboard(this, msg)
                val leftHandler = Handler(Looper.getMainLooper())
                leftHandler.post {
                    showCustom(msg)
                    Util.sendNotification(this, msg)
                }
            }
            "prev" -> {
                val msg = Util.getPrevEmoji(this)
                if (msg.isNullOrBlank()) return

                Util.setClipboard(this, msg)
                val rightHandler = Handler(Looper.getMainLooper())
                rightHandler.post {
                    showCustom(msg)
                    Util.sendNotification(this, msg)
                }
            }
        }
    }

    private fun showCustom( s : String) {
        val layoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.custom_emoti_toast, null)

        // layout 안의 txtMessage
        layout.txtDescription.text = s

        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }
}