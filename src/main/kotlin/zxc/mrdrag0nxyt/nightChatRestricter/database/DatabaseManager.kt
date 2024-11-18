package zxc.mrdrag0nxyt.nightChatRestricter.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.configuration.file.YamlConfiguration
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.database.impl.SQLiteDatabaseWorker
import java.io.File
import java.sql.Connection
import java.sql.SQLException

class DatabaseManager(
    private val plugin: NightChatRestricter,
    private val config: Config
) {

    private var dataSource: HikariDataSource? = null
    var databaseWorker: DatabaseWorker? = null
        private set

    @Throws(SQLException::class)
    fun getConnection(): Connection? {
        return dataSource?.connection
    }

    init {
        val pluginConfig = config.yamlConfiguration
        updateConnection()
    }

    private fun updateConnection() {
        closeConnection()

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:sqlite:${plugin.dataFolder}${File.separator}database.db"

        databaseWorker = SQLiteDatabaseWorker()
        dataSource = HikariDataSource(hikariConfig)
    }

    fun closeConnection() {
        dataSource?.close()
    }

}