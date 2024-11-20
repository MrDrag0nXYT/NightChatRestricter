package zxc.mrdrag0nxyt.nightChatRestricter.database.impl

import org.bukkit.entity.Player
import zxc.mrdrag0nxyt.nightChatRestricter.database.DatabaseWorker
import java.sql.Connection

class MySQLDatabaseWorker : DatabaseWorker {

    override fun initTable(connection: Connection) {
        val sql = """
            CREATE TABLE IF NOT EXISTS players
            (
                playername VARCHAR(25) UNIQUE NOT NULL,
                uuid VARCHAR(36) UNIQUE NOT NULL
            );
        """.trimIndent()

        connection.prepareStatement(sql).apply { executeUpdate() }
    }

    override fun addPlayer(connection: Connection, player: Player) {
        val sql = """
            INSERT INTO players (playername, uuid) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE playername = VALUES(playername), uuid = VALUES(uuid);
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, player.name)
            statement.setString(2, player.uniqueId.toString())

            statement.executeUpdate()
        }
    }

    override fun isPlayerExistByName(connection: Connection, playerName: String): Boolean {
        val sql = "SELECT COUNT(*) FROM players WHERE playername = ?;"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, playerName)

            statement.executeQuery().use { resultSet ->
                return resultSet.next() && resultSet.getInt(1) > 0
            }
        }
    }

    override fun isPlayerExistByUUID(connection: Connection, uuid: String): Boolean {
        val sql = "SELECT COUNT(*) FROM players WHERE uuid = ?;"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, uuid)

            statement.executeQuery().use { resultSet ->
                return resultSet.next() && resultSet.getInt(1) > 0
            }
        }
    }

}