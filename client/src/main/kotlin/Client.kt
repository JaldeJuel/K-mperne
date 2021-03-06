import message.*
import room.GameState
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread


class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true
    private var state : GameState? = null;

    init {
        println("Connected to server at $address on port $port")
    }
    private val writer = ObjectOutputStream(connection.getOutputStream())
    private val reader = ObjectInputStream(connection.getInputStream())


    fun run() {
        thread { read() }
        while (connected) {
            val input = readln()
            if(input.equals("create room")){
                write(ClientCreateRoomMessage("testId"))
            }
            else if(input.equals("join room")){
                write(ClientConnectToRoomMessage("testId"))
            }
            else if(input.equals("move")){
                write(ClientPositionMessage(Position(15,15)))
            } else if(input.equals("leave")){
                write(ClientDisconnectMessage())
            } else if(input.equals("print")){
                println(state)
            }
            if ("exit" in input) {
                connected = false
                reader.close()
                connection.close()
            }
        }

    }

    private fun write(message: GameMessage) {
        writer.writeObject(message)
    }


    private fun read() {
        while (connected ) {
            val obj = reader.readObject()
            state = obj as GameState?
        }
    }
}