package server

import util.C
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.util.*

/**
 * Created by ordogod on 29.08.2019.
 **/

class ClientThread(private val clientSocket: Socket) : Thread() {

    private val clientIP = Former.clientIP(clientSocket)

    override fun run() {
        try {
            val inReader   = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            val parse = StringTokenizer(inReader.readLine())

            val methodNameReq  = Former.methodName(parse.nextToken())
            val dataNameReq    = Former.dataName(parse.nextToken())
            val contentTypeRes = Former.contentType(dataNameReq)

            if (!C.hasPlayer(clientIP)) {
                if (C.players.size < C.MAX_PLAYERS) {
                    with (PlayerThread(clientSocket, clientIP)) {
                        C.addPlayer(this)
                        start()
                    }
                }
                else if (dataNameReq == "/index.html")
                    Response(clientSocket)
                        .contentType("text/html")
                        .data(C.RES_NO_SLOTS_LEFT)
                        .send(true)
            }

            if (methodNameReq == "GET") {

                Log.i("$clientIP requested \'$dataNameReq\'.")

                val data = when {
                    contentTypeRes != "text/plain" -> {
                        Former.data(File(MainServer.DEFAULT_ROOT, dataNameReq))
                    }
                    else -> Former.data(dataNameReq)
                }

                Response(clientSocket)
                    .contentType(contentTypeRes)
                    .data(data)
                    .send(true)
            }

            if (methodNameReq == "POST") {

                val data = Network.decodeData(inReader)

                Log.i("$clientIP posted \'$data\'.")

                when (data) {
                    "isConnected=true" -> C.findPlayer(clientIP)?.resetTimer()
                }

                Response(clientSocket)
                    .data("Ponyatno".toByteArray())
                    .send(true)
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            if (e !is SocketException) { return }
            e.printStackTrace()
        }
    }
}