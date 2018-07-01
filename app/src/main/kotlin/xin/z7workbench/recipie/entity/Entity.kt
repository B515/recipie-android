package xin.z7workbench.recipie.entity

data class LoginMessage(val nick: String)

data class ServerMessage(val Object: String, val ToUser: String, val FromUser: String,
                         val CreateTime: String, val MsgType: String, val Content: String,
                         val OnlineUser: List<String>?)

data class FileInfoMessage(val Object: String, val ToUser: String, val FromUser: String,
                           val CreateTime: String, val MsgType: String, val FileName: String,
                           val FileSize: Int, val MsgID: Int)

data class FileMessage(val MsgType: String, val MsgID: Int, val Content: String)

data class AuthRequestMessage(val Op: String, val Username: String, val Password: String,
                              val Nickname: String)

data class AuthResultMessage(val Result: Int)

data class SystemMessage(val Op: String, val MsgType: String = "system", val Result: Boolean = false)

data class SystemProfileMessage(val Op: String, var Nickname: String, var Sex: Int,
                                val MsgType: String = "system", val Result: Boolean = false)

data class SystemFollowMessage(val Op: String, val User: String, val MsgType: String = "system",
                               val Result: Boolean = false)

data class SystemFollowingMessage(val Op: String, val UserList: List<String>, val MsgType: String = "system",
                                  val Result: Boolean = false)
