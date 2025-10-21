package com.plaglefleau.clashofclansmanage.database

import com.plaglefleau.clashofclansmanage.Credential
import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement
import java.util.*


class Connector {
    private var connection: Connection? = null
    private val lock = Any()

    private val logger = KotlinLogging.logger {}
    fun connect() {
        synchronized(lock) {
            // Close existing connection if present
            if (connection != null && connection!!.isClosed) {
                connection!!.close()
            }

            val props = Properties()
            props.setProperty("user", Credential.DATABASE_USERNAME)
            props.setProperty("password", Credential.DATABASE_PASSWORD)
            try {
                connection = DriverManager.getConnection(Credential.DATABASE_URL, props)
            } catch (e: Exception) {
                logger.error { "Error connecting to database: ${e.message}" }
            }
        }
    }

    private fun ensureConnection() {
        synchronized(lock) {
            if (connection == null || connection!!.isClosed) {
                connect()
            }
        }
    }

    fun getStatement(): Statement {
        ensureConnection()
        return connection!!.createStatement()
    }

    fun getPreparedStatement(sql: String): PreparedStatement {
        ensureConnection()
        return connection!!.prepareStatement(sql)
    }

    fun disconnect() {
        synchronized(lock) {
            connection?.let {
                if (!it.isClosed) {
                    it.close()
                }
            }
            connection = null
        }
    }
}