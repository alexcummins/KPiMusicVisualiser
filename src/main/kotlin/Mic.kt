import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import be.tarsos.dsp.util.fft.FFT
import java.awt.Color
import java.time.Instant
import java.time.LocalDateTime
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine

class Mic() {
    private val sampleRate = 44100f
    private val bufferSize = 1024 * 4
    private val overlap = 768 * 4

    private val FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 2, 4, 44100F, false)
    private val TARGET_DATA_LINE: TargetDataLine = AudioSystem.getTargetDataLine(FORMAT) as TargetDataLine

    private val fftHandlers: MutableList<HandleFFT> = mutableListOf()

    private lateinit var dispatcher: AudioDispatcher

    private val FFT_PROCESSOR: AudioProcessor = object : AudioProcessor {
        var previousTime : Long = 0L
        var fft = FFT(TARGET_DATA_LINE.bufferSize)
        var amplitudes = FloatArray(TARGET_DATA_LINE.bufferSize / 2)
        override fun processingFinished() {
            // TODO Auto-generated method stub
        }

        override fun process(audioEvent: AudioEvent): Boolean {
            if (Instant.now().toEpochMilli() > previousTime + 200 ) {
                previousTime = Instant.now().toEpochMilli()

//                println("Not Throttled")
            } else {
//                println("Throttled")
                return false
            }
            val audioFloatBuffer = audioEvent.floatBuffer
            val transformbuffer = FloatArray(TARGET_DATA_LINE.bufferSize * 2)
            System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.size)
            fft.forwardTransform(transformbuffer)
            fft.modulus(transformbuffer, amplitudes)
            fftHandlers.forEach { it.accept(fft, amplitudes) }
            return true
        }
    }

    fun startMic() {
        TARGET_DATA_LINE.open()
        TARGET_DATA_LINE.start()
        val stream = AudioInputStream(TARGET_DATA_LINE)
        val audioStream = JVMAudioInputStream(stream)
        // create a new dispatcher
        dispatcher = AudioDispatcher(
            audioStream, bufferSize,
            overlap
        )

        // add a processor, handle pitch event.
        dispatcher.addAudioProcessor(FFT_PROCESSOR)

        // run the dispatcher (on a new thread).
        Thread(dispatcher, "Audio dispatching").start()
    }

    fun subscribeToFFT(handler: HandleFFT) {
        fftHandlers.add(handler)
    }

    fun interface HandleFFT {
        fun accept(fft: FFT, amplitudes: FloatArray)
    }
}