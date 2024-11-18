package zxc.mrdrag0nxyt.nightChatRestricter.database.impl

import zxc.mrdrag0nxyt.nightChatRestricter.database.DatabaseWorker
import java.sql.Connection

class SQLiteDatabaseWorker : DatabaseWorker {

    override fun initTable(connection: Connection) {
        val sql = """
            CREATE TABLE IF NOT EXISTS 'users' (
                'player' TEXT NOT NULL UNIQUE,
                'need_played_time' INTEGER NOT NULL,
                PRIMARY KEY('player')
            );
        """.trimIndent()

        connection.prepareStatement(sql).executeUpdate()
    }

    override fun addPlayer(connection: Connection, player: String, needPlayedTime: Long) {
        val sql = "INSERT OR REPLACE INTO 'users' (player, need_played_time) VALUES (?, ?)"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, player)
            statement.setLong(2, needPlayedTime)

            statement.executeUpdate()
        }
    }

    override fun getNeedPlayedTime(connection: Connection, player: String): Long? {
        val sql = "SELECT need_played_time FROM 'users' WHERE player = ?"

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, player)

            statement.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    return resultSet.getLong(1)
                }
            }
        }

        return null
    }

}