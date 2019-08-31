package server

import util.Log
import java.io.*
import java.net.ServerSocket
import java.io.File
import kotlin.system.exitProcess

/**
 * Created by ordogod on 26.08.2019.
 **/

object MainServer : Runnable {

    val DEFAULT_PORT = 8080
    val DEFAULT_ROOT = defaultRoot()

    private var port: Int = DEFAULT_PORT

    fun init(port: Int): MainServer = apply {
        MainServer.port = port
    }

    override fun run() {
        val serverSocket: ServerSocket

        try {
            serverSocket = ServerSocket(port)
            Log.s("Server successfully started at port $port.")

            while (true)
                ServerThread(serverSocket.accept()).start()
        }
        catch (e: Exception) {
            if (e is IOException) Log.f("Can not start server at port $port.")
            e.printStackTrace()
            exitProcess(0)
        }
    }

    private fun defaultRoot() =
        when (System.getProperty("os.name")) {
            "Windows 10" -> File("D:\\dev\\java\\projects\\web_strategy\\src\\main\\resources")
            "Linux"      -> File("/root/game/src/main/resources")
            else         -> File(".")
        }

    fun onDataArrived(data: String, fromIP: String) {
        when (data) {
            "isConnected=true" -> {
                println("data $data arrived from $fromIP")
            }
        }
    }
}