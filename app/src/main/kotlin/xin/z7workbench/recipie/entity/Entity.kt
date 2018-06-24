package xin.z7workbench.recipie.entity

data class LoginMessage(val nick: String)

data class ServerMessage(val Object: String, val ToUser: String, val FromUser: String,
                         val CreateTime: String, val MsgType: String, val Content: String,
                         val OnlineUser: List<String>?,
                         var fromMyself: Boolean = false)

data class FileInfoMessage(val Object: String, val ToUser: String, val FromUser: String,
                           val CreateTime: String, val MsgType: String, val FileName: String,
                           val FileSize: Long, val MsgID: Int)

data class FileMessage(val MsgType: String, val MsgID: Int, val Content: String)
