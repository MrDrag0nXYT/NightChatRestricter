package zxc.mrdrag0nxyt.nightChatRestricter.database

import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.SQLException

interface DatabaseWorker {

    @Throws(SQLException::class)
    fun initTable(connection: Connection)

    @Throws(SQLException::class)
    fun addPlayer(connection: Connection, player: Player)

    @Throws(SQLException::class)
    fun isPlayerExistByName(connection: Connection, playerName: String): Boolean

    @Throws(SQLException::class)
    fun isPlayerExistByUUID(connection: Connection, uuid: String): Boolean

}