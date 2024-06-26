package com.example.seesaw.model

import kotlin.collections.HashMap

class ChatModel (val users: HashMap<String, Boolean> = HashMap(), val comments : HashMap<String, Comment> = HashMap())
{
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null, val readUsers :HashMap<String,Boolean>? = HashMap())
}

