package tech.brainco.focuscourse.liveclass.data.models

import java.io.Serializable

data class UdpDataWrapper(val path: String, val data: Any?) : Serializable {
    companion object {
        private const val serialVersionUID = -2875330983578249034L
    }
}

const val PATH_PREFIX = "tech.brainco.focuscourse"

//学生eeg数据
const val PATH_STUDENT_EEG = "$PATH_PREFIX/student/eeg"

//头环led灯开关状态
const val PATH_HEADBAND_LED = "$PATH_PREFIX/headband/led"

