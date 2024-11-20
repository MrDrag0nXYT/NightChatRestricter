package zxc.mrdrag0nxyt.nightChatRestricter.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.DatabaseConfigEntity
import zxc.mrdrag0nxyt.nightChatRestricter.config.DatabaseType
import zxc.mrdrag0nxyt.nightChatRestricter.database.impl.MySQLDatabaseWorker
import zxc.mrdrag0nxyt.nightChatRestricter.database.impl.SQLiteDatabaseWorker
import java.io.File
import java.sql.Connection
import java.sql.SQLException

class DatabaseManager(
    private val plugin: NightChatRestricter,
    databaseType: DatabaseType,
    databaseConfig: DatabaseConfigEntity?
) {

    private var datasource: HikariDataSource? = null
    var databaseWorker: DatabaseWorker? = null
        private set

    init {
        updateConnection(databaseType, databaseConfig)
    }

    fun updateConnection(databaseType: DatabaseType, databaseConfig: DatabaseConfigEntity?) {
        closeConnection()
        val hikariConfig = HikariConfig()

        val dbTypeFallback = if (databaseConfig == null) DatabaseType.SQLITE else databaseType

        when (dbTypeFallback) {
            DatabaseType.MYSQL -> {
                hikariConfig.apply {
                    jdbcUrl = "jdbc:mysql://${databaseConfig?.host}:${databaseConfig?.port}/${databaseConfig?.database}"
                    username = databaseConfig?.username
                    password = databaseConfig?.password
                }
                databaseWorker = MySQLDatabaseWorker()
            }

            else -> {
                hikariConfig.apply {
                    jdbcUrl = "jdbc:sqlite:${plugin.dataFolder}${File.separator}players.db"
                    driverClassName = "org.sqlite.JDBC"
                }
                databaseWorker = SQLiteDatabaseWorker()
            }
        }

        hikariConfig.maximumPoolSize = 10
        datasource = HikariDataSource(hikariConfig)
    }

    @Throws(SQLException::class)
    fun getConnection(): Connection? {
        return datasource?.connection
    }

    fun closeConnection() {
        datasource?.close()
        datasource = null
    }
}