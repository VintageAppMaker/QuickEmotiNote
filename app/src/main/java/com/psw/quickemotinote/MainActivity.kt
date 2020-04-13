package com.psw.quickemotinote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psw.quickemotinote.adapter.emojiAdapter
import com.psw.quickemotinote.data.EmojiData
import com.psw.quickemotinote.data.EmojiText
import com.psw.quickemotinote.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var binder  : ActivityMainBinding
    lateinit var adapter : emojiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpUI()
    }

    private fun setUpUI() {

        Util.sendNotification(applicationContext, Util.getCurrentEmoji(applicationContext))

        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.rcyMain.layoutManager = LinearLayoutManager(this)
        binder.rcyMain.setOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ( !recyclerView.canScrollVertically(1) ){

                }
            }
        })

        emojiAdapter(mutableListOf<EmojiData>(), this)?.let{
            adapter = it
            binder.rcyMain.adapter = adapter
        }

        setData()

        txtTitle.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_input_text, null)
            val dialogText = dialogView.findViewById<EditText>(R.id.editEmoji)

            Util.getClipboard(applicationContext)?.let{
                dialogText.setText(it)
            }
            builder.setView(dialogView)
                .setPositiveButton("O") { dlg, l ->
                    Util.addMyEmoji(applicationContext, dialogText.text.toString(), {setData()})
                }
                .setNegativeButton("X") { dlg, l ->
                }
                .show()
        }

        txtSearch.setOnClickListener {
            var url ="https://www.google.com/search?q=emoji"
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }



    private fun setData() {
        adapter.loadData()
    }
}
