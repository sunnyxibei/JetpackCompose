import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.InternalTextApi
import androidx.compose.ui.text.input.TextFieldValue
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

@InternalTextApi
fun main() = Window(title = "专注课堂UDP Mock", size = IntSize(800, 600)) {
    val textPrefix = remember { mutableStateOf(TextFieldValue("BRAINCO00180")) }
    val textNum = remember { mutableStateOf(TextFieldValue("45")) }
    MaterialTheme {
        Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(height = 24.dp))
            Text("模拟学生账号前缀(e.g. BRAINCO00180)")
            Spacer(modifier = Modifier.height(height = 12.dp))
            OutlinedTextField(value = textPrefix.value, onValueChange = { textPrefix.value = it })
            Spacer(modifier = Modifier.height(height = 12.dp))
            Text("模拟学生数量(1-99)")
            Spacer(modifier = Modifier.height(height = 12.dp))
            OutlinedTextField(value = textNum.value, onValueChange = { textNum.value = it })
            Spacer(modifier = Modifier.height(height = 36.dp))
            Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        isUdpRunning = true
                        println("开始发送模拟数据")
                        sendUdpData(
                                studentPrefix = textPrefix.value.text,
                                studentNum = textNum.value.text.toInt(),
                        )
                    },
            ) {
                Text("开始发送模拟数据")
            }
            Spacer(modifier = Modifier.height(height = 24.dp))
            Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        isUdpRunning = false
                        println("停止发送数据")
                    },
            ) {
                Text("停止发送数据")
            }
            Spacer(modifier = Modifier.height(height = 24.dp))
        }
    }
}

private var isUdpRunning = false
private var multicastSocket: MulticastSocket? = null
private val address = InetAddress.getByName(MULTICAST_ADDRESS)

fun sendUdpData(studentNum: Int, studentPrefix: String) {
    MainScope().launch(Dispatchers.IO) {
        println("MockStudentUdpService, receiveLedStatus")
        try {
            multicastSocket = MulticastSocket(MULTICAST_PORT)
            multicastSocket?.joinGroup(address)
        } catch (e: Exception) {
            println("$e, MockStudentUdpService, Error when join multicast group")
        }
        for (i in 1..studentNum) {
            launch {
                while (isActive && isUdpRunning) {
                    sendStudentData(studentPrefix = studentPrefix, index = i, attention = Random.nextDouble(100.0))
                    delay(1000)
                }
            }
        }
    }
}

private fun sendStudentData(studentPrefix: String, index: Int, attention: Double? = null, connected: Boolean = true) {
    val studentData = StudentAttentionData(
            userName = if (index < 10) "$studentPrefix$index" else "$studentPrefix$index",
            nickname = "黑喵警长$index",
            className = "小学四年级培优班",
            attention = attention?.toInt(),
            timeStamp = System.currentTimeMillis(),
            isLowPower = false,
            contacted = Random.nextBoolean(),
            coin = Random.nextInt(5),
            connected = Random.nextBoolean(),
    )
    try {
        //wrapper data
        println("StudentUdpService, sendUdpData data = $studentData")
        val wrapper = UdpDataWrapper(path = PATH_STUDENT_EEG, data = studentData)
        val serialize = wrapper.serialize()
        val packet = DatagramPacket(serialize, serialize.size, address, MULTICAST_PORT)
        multicastSocket?.send(packet)
    } catch (e: Exception) {
        println("$e, StudentUdpService, Error when send student data via udp multicast")
    }
}
