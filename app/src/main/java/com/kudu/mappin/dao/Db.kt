package com.kudu.mappin.dao

import java.sql.Connection
import java.sql.DriverManager

class Db {
    private var connection: Connection? = null

    private val host = "63.250.41.89"

    private val database = "Agrosilos"
    private val port = 5432
    private val user = "postgres"
    private val pass = "uUP38oWm1g3bM"
    private var url = "jdbc:postgresql://%s:%d/%s"
    private var status = false


    fun main(args: Array<String>) {
        url = String.format(url, host, port, database)
        connect()
        //this.disconnect();
        println("connection status:$status")
    }

    private fun connect() {
        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver")
                connection = DriverManager.getConnection(url, user, pass)
                status = true
                println("connected:$status")
            } catch (e: Exception) {
                status = false
                print(e.message)
                e.printStackTrace()
            }
        }
        thread.start()
        try {
            thread.join()
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
    }

    fun getExtraConnection(): Connection? {
        var c: Connection? = null
        try {
            Class.forName("org.postgresql.Driver")
            c = DriverManager.getConnection(url, user, pass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return c
    }
}