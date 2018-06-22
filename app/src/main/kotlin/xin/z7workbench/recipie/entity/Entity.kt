package xin.z7workbench.recipie.entity

data class ChatMessage(var content: String, var isRequest: Boolean = false, var isImage: Boolean = false)
