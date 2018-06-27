package xin.z7workbench.recipie.entity

data class LoginMessage(val nick: String)

data class ServerMessage(val Object: String, val ToUser: String, val FromUser: String,
                         val CreateTime: String, val MsgType: String, val Content: String,
                         val OnlineUser: List<String>?)

data class FileInfoMessage(val Object: String, val ToUser: String, val FromUser: String,
                           val CreateTime: String, val MsgType: String, val FileName: String,
                           val FileSize: Int, val MsgID: Int)

data class FileMessage(val MsgType: String, val MsgID: Int, val Content: String)

data class SystemAuthRequestMessage(val MsgType: String, val Op: String, val Result: Boolean,
                             val Username: String, val Password: String, val Nickname: String)

data class SystemAuthResultMessage(val Result: Boolean)

data class SystemRequestMessage(val MsgType: String, val Op: String)

data class SystemResultMessage(val MsgType: String, val Op: String, val Result: Boolean)

data class SystemProfileMessage(val MsgType: String, val Op: String, val Result: Boolean,
                                var Nickname: String, var Sex: Int)
