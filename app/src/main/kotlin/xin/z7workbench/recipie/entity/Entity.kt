package xin.z7workbench.recipie.entity

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

data class Token(val key: String)
data class User(val id: Int, var username: String, var password: String, var email: String?)
data class UserInfo(val id: Int, val user: User?, var gender: Int, var avatar: String,
                    var nickname: String)

data class Recipe(val id: Int, var title: String, var description: String, var content: String,
                  val create_by: UserInfo?, val comment_set: List<Comment>?, val tag: List<Tag>?,
                  val read_count: Int, val like_count: Int, val collect_count: Int)

data class Comment(val id: Int, val recipe: Recipe?, val user: UserInfo?, val like_count: Int,
                   var content: String)

data class Tag(val id: Int, val title: String, val description: String)

data class RecipeStep(val image: String, val description: String)
