package xin.z7workbench.recipie.entity

data class ChatMessage(var content: String, var time: String, var isRequest: Boolean = false, var isImage: Boolean = false)

data class LoginMessage(var nick: String)
data class ServerMessage(val Object: String, val ToUser: String, val FromUser: String,
                         val CreateTime: String, val MsgType: String, val Content: String,
                         val OnlineUser: List<String>?)