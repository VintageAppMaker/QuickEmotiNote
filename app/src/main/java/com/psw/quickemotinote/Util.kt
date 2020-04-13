package com.psw.quickemotinote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.ClipboardManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.psw.quickemotinote.adapter.emojiAdapter
import com.psw.quickemotinote.data.EmojiData
import com.psw.quickemotinote.data.EmojiText
import java.io.*

object Util {
    private val delimeter: String = "[끝]"
    var nIndex = 0

    fun addMyEmoji(ctx : Context, s : String, fnOk : () -> Unit = {} ){
        var sData = ReadMyEmoticons(ctx)
        sData.add(0, EmojiText(s, emojiAdapter.TYPE_TWO))
        SaveMyEmojiFromList(ctx, sData)

        fnOk()
    }

    fun WriteMyEmoticonsText(ctx : Context, s : String){
        val filename = "myemoticons.txt"
        val `os` = ctx.openFileOutput(filename, Context.MODE_PRIVATE)
        `os`.write(s.toByteArray())
        `os`.close()
    }

    fun SaveMyEmojiFromList(ctx: Context, lst : MutableList<EmojiData>){
        var s = ""
        lst.forEach{ nItem ->
            ( nItem as EmojiText ).apply{
                if ( spec == emojiAdapter.TYPE_TWO ){
                    s = s + delimeter + emoji + "\n"
                }
            }
        }
        WriteMyEmoticonsText(ctx, s)
    }

    fun ReadMyEmoticons(ctx : Context) : MutableList<EmojiData>{
        val buffer = StringBuffer()
        var data: String? = null
        var fis: FileInputStream? = null
        try {
            fis = ctx.openFileInput("myemoticons.txt")
            val iReader = BufferedReader(InputStreamReader(fis))
            data = iReader.readLine()
            while (data != null) {
                buffer.append(data)
                buffer.append("\n")
                data = iReader.readLine()
            }

            iReader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val sDelimeter = delimeter

        var m = mutableListOf<String>()
        buffer!!.split(sDelimeter).forEach { if (!it.isNullOrBlank()) m.add(it) }

        var mEmoticons = mutableListOf<EmojiData>()
        m.forEach {
            mEmoticons.add ( EmojiText(it, emojiAdapter.TYPE_TWO) )
        }

        return mEmoticons
    }

    fun getEmoticons(ctx : Context, type : Int) : MutableList<EmojiData>{

        val filename = if (type == 0 ) "emoticons.txt" else "emoticons2.txt"

        val `is` = ctx.assets.open(filename)
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()

        val sText = String(buffer)
        val sDelimeter = if (type == 0) "\n" else delimeter

        var m = mutableListOf<String>()
        sText.split(sDelimeter).forEach { m.add(it) }

        var mEmoticons = mutableListOf<EmojiData>()
        m.forEach {
            if (!it.isNullOrBlank())
                mEmoticons.add ( EmojiText(it, emojiAdapter.TYPE_ONE) )
        }

        return mEmoticons
    }

    fun getCurrentEmoji(ctx : Context) : String {
        var m = getEmojiList(ctx)

        if (nIndex >= m.size) return ""
        return (m.get(nIndex) as EmojiText).emoji ?: ""
    }

    fun getPrevEmoji(ctx : Context) : String {
        var m = getEmojiList(ctx)

        nIndex--
        if (nIndex < 0 ) nIndex = 0
        m.forEachIndexed { index, emojiData ->
            if (index == nIndex){
                return (emojiData as EmojiText).emoji ?: ""
            }
        }

        return ""
    }

    fun getNextEmoji(ctx : Context) : String {
        var m = getEmojiList(ctx)

        nIndex++
        val nCount = m.count()
        if (nIndex > nCount - 1 ) nIndex = 0
        m.forEachIndexed { index, emojiData ->
            if (index == nIndex){
                return (emojiData as EmojiText).emoji ?: ""
            }
        }

        return ""
    }

    // 전체 이모지 리스트를 가져온다.
    fun getEmojiList(ctx: Context): MutableList<EmojiData> {

        var m = ReadMyEmoticons(ctx)
        getEmoticons(ctx, 1).forEach {
            m.add(it)
        }
        getEmoticons(ctx, 0).forEach {
            m.add(it)
        }

        return m
    }

    fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    fun getClipboard(context: Context) : String  {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            return clipboard.text.toString()
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            return clipboard.primaryClip?.getItemAt(0)?.text.toString()
        }

        return ""
    }

    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)
            channel.setSound(null, null)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(ctx : Context, s : String){

        val NOTIFICATION_ID = 1001;
        createNotificationChannel(ctx, NotificationManagerCompat.IMPORTANCE_LOW, false,
            ctx.getString(R.string.app_name), "Emoji Notification")

        val channelId = "${ctx.packageName}-${ctx.getString(R.string.app_name)}"
        val title = ""
        val content = s

        val intent = Intent(ctx, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(ctx, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(ctx, NotificationIntentService::class.java)
        nextIntent.action = "next"

        val prevIntent = Intent(ctx, NotificationIntentService::class.java)
        prevIntent.action = "prev"

        val builder = NotificationCompat.Builder(ctx, channelId)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
        builder.setLargeIcon(
            BitmapFactory.decodeResource(ctx.resources, R.drawable.emoji))

        builder.addAction(NotificationCompat.Action(
            R.drawable.ic_back,"prev", PendingIntent.getService(ctx, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)))

        builder.addAction(NotificationCompat.Action(
            R.drawable.ic_forward,"next", PendingIntent.getService(ctx, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)))

        builder.priority = NotificationCompat.PRIORITY_LOW
        builder.setAutoCancel(true)
        builder.setSound(null)
        builder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(ctx)
        notificationManager.notify(NOTIFICATION_ID, builder.build())


    }
}