import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import be.tarsos.dsp.util.fft.FFT
import be.tarsos.dsp.util.fft.HannWindow
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine

class Mic {
    private val sampleRate = 44100f // 0 - 22050Hz
    private val bufferSize = 1024 * 8
    private val overlap = 768 * 4

    private val FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 2, 4, sampleRate, false)
    private val TARGET_DATA_LINE: TargetDataLine = AudioSystem.getTargetDataLine(FORMAT) as TargetDataLine

    private val fftHandlers: MutableList<FFTHandler> = mutableListOf()

    private lateinit var dispatcher: AudioDispatcher

    private val FFT_PROCESSOR: AudioProcessor = object : AudioProcessor {
        var fft = FFT(bufferSize * 2, HannWindow())
        var amplitudes = FloatArray(bufferSize / 2)
        override fun processingFinished() {
            // TODO Auto-generated method stub
        }

        override fun process(audioEvent: AudioEvent): Boolean {
            val audioFloatBuffer = audioEvent.floatBuffer
            val transformBuffer = FloatArray(TARGET_DATA_LINE.bufferSize * 2)
            System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.size)
            fft.forwardTransform(transformBuffer)
            fft.modulus(transformBuffer, amplitudes)
            fftHandlers.forEach { it.accept(fft, amplitudes) }
            return true
        }
    }

    fun startMic() {
        TARGET_DATA_LINE.open(FORMAT, bufferSize)
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

    fun subscribeToFFT(handler: FFTHandler) {
        fftHandlers.add(handler)
    }

    interface FFTHandler {
        fun accept(fft: FFT, magnitudes: FloatArray)
    }
}