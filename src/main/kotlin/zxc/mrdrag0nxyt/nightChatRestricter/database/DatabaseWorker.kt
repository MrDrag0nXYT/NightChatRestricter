package zxc.mrdrag0nxyt.nightChatRestricter.database

import java.sql.Connection
import java.sql.SQLException

interface DatabaseWorker {

    @Throws(SQLException::class)
    fun initTable(connection: Connection)

    @Throws(SQLException::class)
    fun addPlayer(connection: Connection, player: String, needPlayedTime: Long)

    @Throws(SQLException::class)
    fun getNeedPlayedTime(connection: Connection, player: String): Long?

}