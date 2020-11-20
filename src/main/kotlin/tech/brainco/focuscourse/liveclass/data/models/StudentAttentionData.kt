package tech.brainco.focuscourse.liveclass.data.models

import DATA_EXPIRED_TIME
import java.io.Serializable

data class StudentAttentionData(
    val userName: String,
    val nickname: String,
    val className: String,
    val attention: Int?,
    val timeStamp: Long,
    val isLowPower: Boolean,
    val contacted: Boolean,
    val connected: Boolean,
    val coin: Int,
) : Serializable {

    val isExpired
        get() = System.currentTimeMillis() - timeStamp > DATA_EXPIRED_TIME

    companion object {
        private const val serialVersionUID = 5666417755144475748L
    }

    override fun toString(): String {
        return "StudentAttentionData(userName='$userName', nickname='$nickname', className='$className', attention=$attention, timeStamp=$timeStamp, isLowPower=$isLowPower, contacted=$contacted, connected=$connected, coin=$coin)"
    }

}