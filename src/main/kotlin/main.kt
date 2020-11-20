import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import tech.brainco.focuscourse.liveclass.data.models.PATH_STUDENT_EEG
import tech.brainco.focuscourse.liveclass.data.models.StudentAttentionData
import tech.brainco.focuscourse.liveclass.data.models.UdpDataWrapper
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import kotlin.random.Random

fun main() = Window(title = "Compose for Desktop", size = IntSize(300, 300)) {
    val count = remember { mutableStateOf(0) }
    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value++
                    }) {
                Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
            }
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value = 0
                    }) {
                Text("Reset")
            }
            Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = ::sendUdpData,
            ) {
                Text("开始发送模拟数据")
            }
        }
    }
}

fun sendUdpData() {
    MainScope().launch(Dispatchers.IO) {
        println("MockStudentUdpService, receiveLedStatus")
        try {
            multicastSocket = MulticastSocket(MULTICAST_PORT)
            multicastSocket?.joinGroup(address)
        } catch (e: Exception) {
            println("$e, MockStudentUdpService, Error when join multicast group")
        }
        for (i in 1..55) {
            launch {
                while (isActive) {
                    sendStudentData(index = i, attention = Random.nextDouble(100.0))
                    delay(1000)
                }
            }
        }
    }
}

private var multicastSocket: MulticastSocket? = null
private val address = InetAddress.getByName(MULTICAST_ADDRESS)

private fun sendStudentData(index: Int, attention: Double? = null, connected: Boolean = true) {
    val studentData = StudentAttentionData(
            userName = if (index < 10) "BRAINCO001800$index" else "BRAINCO00180$index",
            nickname = "黑喵警长$index",
            className = "小学四年级培优班",
            attention = attention?.toInt(),
            timeStamp = System.currentTimeMillis(),
            isLowPower = false,
            contacted = true,
            coin = Random.nextInt(5),
            connected = connected,
    )
    sendUdpData(path = PATH_STUDENT_EEG, data = studentData)
}

private fun sendUdpData(path: String, data: Any?) {
    try {
        //wrapper data
        println("StudentUdpService, sendUdpData data = $data")
        val wrapper = UdpDataWrapper(path = path, data = data)
        val serialize = wrapper.serialize()
        val packet = DatagramPacket(serialize, serialize.size, address, MULTICAST_PORT)
        multicastSocket?.send(packet)
    } catch (e: Exception) {
        println("$e, StudentUdpService, Error when send student data via udp multicast")
    }
}

