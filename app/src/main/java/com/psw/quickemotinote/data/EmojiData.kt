package com.psw.quickemotinote.data

sealed class EmojiData

data class EmojiText(
    var emoji : String?,
    var spec  : Int
    ) : EmojiData()

