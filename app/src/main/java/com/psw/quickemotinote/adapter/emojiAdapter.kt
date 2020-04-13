package com.psw.quickemotinote.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.psw.quickemotinote.R
import com.psw.quickemotinote.Util
import com.psw.quickemotinote.data.EmojiData
import com.psw.quickemotinote.data.EmojiText
import kotlinx.android.synthetic.main.item_emoti_list.view.txtDescription
import kotlinx.android.synthetic.main.item_emoti_list2.view.*


class emojiAdapter(val items : List<EmojiData>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mItems  : MutableList <EmojiData> = items.toMutableList()
    var act     : Activity = context as Activity
    var adapter = this

    fun loadData(){
        clearItems()
        mItems = Util.getEmojiList(context)
        notifyDataSetChanged()

        // 리스트를 읽은 것은 초기화를 의미함
        Util.nIndex = 0
        Util.sendNotification(context, Util.getCurrentEmoji(context))
    }

    fun deleteItemByIndex(n : Int){
        mItems.removeAt(n)
        Util.SaveMyEmojiFromList(context, mItems)
        loadData()
    }

    fun clearItems(){
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            TYPE_ONE   -> {emojiHolder(LayoutInflater.from(context).inflate(R.layout.item_emoti_list, parent, false))}
            TYPE_TWO   -> {emojiHolder2(LayoutInflater.from(context).inflate(R.layout.item_emoti_list2, parent, false))}
            else -> {emojiHolder(LayoutInflater.from(context).inflate(R.layout.item_emoti_list, parent, false))}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType){
            TYPE_ONE -> { (holder as emojiHolder).apply {
                var item = mItems.get(position) as EmojiText
                bind(context,  item)
            }}

            TYPE_TWO -> { (holder as emojiHolder2).apply {
                var item = mItems.get(position) as EmojiText
                bind(context,  item, adapter, position)
            }}
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (mItems.get(position)){
            is EmojiText -> {
                val item = mItems.get(position) as EmojiText
                item.spec
            }
            else    -> {TYPE_ONE}
        }
    }

    companion object {
        const val TYPE_ONE   = 0
        const val TYPE_TWO   = 1
    }
}

class emojiHolder (view: View) : RecyclerView.ViewHolder(view) {
    var txtEmoji : TextView = view.txtDescription

    fun bind(context : Context, item : EmojiText){
        //txtName.text = item.emoji
        txtEmoji.text  = item.emoji
        txtEmoji.apply{
            setOnClickListener {
                // clipboard로 복사하기
                Toast.makeText(context, txtDescription.text, Toast.LENGTH_LONG).show()
                Util.setClipboard(context, txtDescription.text.toString())
            }
        }
    }
}

class emojiHolder2 (view: View) : RecyclerView.ViewHolder(view) {
    var txtEmoji : TextView = view.txtDescription

    var imgAdd         : ImageView = view.ImageAdd
    var imgDelete      : ImageView = view.ImageDelete

    fun bind(context : Context, item : EmojiText, adt : emojiAdapter, nIndex : Int){
        //txtName.text = item.emoji
        txtEmoji.text  = item.emoji
        txtEmoji.apply{
            setOnClickListener {
                // clipboard로 복사하기
                Toast.makeText(context, txtDescription.text, Toast.LENGTH_LONG).show()
                Util.setClipboard(context, txtDescription.text.toString())
            }
        }

        imgAdd.setOnClickListener {
            showAddDialog(context, adt)

        }

        imgDelete.setOnClickListener {
            askDialog(context, adt){
                adt.deleteItemByIndex(nIndex)
            }
        }
    }

    private fun askDialog(
        context: Context,
        adt: emojiAdapter,
        onOk : () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        val dialogView = adt.act.layoutInflater.inflate(R.layout.dialog_yes_or_no, null)
        builder.setView(dialogView)
            .setPositiveButton("O") { dlg, l ->
                onOk()
            }
            .setNegativeButton("X") { dlg, l ->
            }
            .show()
    }

    private fun showAddDialog(
        context: Context,
        adt: emojiAdapter
    ) {
        val builder = AlertDialog.Builder(context)
        val dialogView = adt.act.layoutInflater.inflate(R.layout.dialog_input_text, null)
        val dialogText = dialogView.findViewById<EditText>(R.id.editEmoji)

        dialogText.setText(txtEmoji.text)

        builder.setView(dialogView)
            .setPositiveButton("O") { dlg, l ->
                Util.addMyEmoji(context, dialogText.text.toString(), { adt.loadData() })
            }
            .setNegativeButton("X") { dlg, l ->
            }
            .show()
    }
}

