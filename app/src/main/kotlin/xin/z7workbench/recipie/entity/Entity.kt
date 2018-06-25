package xin.z7workbench.recipie.entity

data class LoginMessage(val nick: String)
data class ServerMessage(val Object: String, val ToUser: String, val FromUser: String,
                         val CreateTime: String, val MsgType: String, val Content: String,
                         val OnlineUser: List<String>?,
                         var fromMyself: Boolean = false)
