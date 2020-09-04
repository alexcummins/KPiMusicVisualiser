import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine

class Mic() {
    private val FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 2, 4, 44100F, false)
    private val TARGET_DATA_LINE : TargetDataLine = AudioSystem.getTargetDataLine(FORMAT) as TargetDataLine

    fun startMic() {
        TARGET_DATA_LINE.open()
        TARGET_DATA_LINE.start()
    }

    fun getData(): Int {
        val data = ByteArray(TARGET_DATA_LINE.bufferSize / 5)
        return TARGET_DATA_LINE.read(data, 0, data.size)
    }
}