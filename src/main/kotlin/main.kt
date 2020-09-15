import be.tarsos.dsp.util.PitchConverter
import be.tarsos.dsp.util.fft.FFT
import display.Display
import display.web.WebDisplay
import java.awt.Color
import javax.sound.sampled.*
import kotlin.math.max
import kotlin.math.roundToInt


const val WIDTH = 50
const val HEIGHT = 50

fun main(args: Array<String>) {
    println("Hello world")
    println(args)

    val microphone: Mic = Mic();

    microphone.startMic()

    val display: Display = WebDisplay(width = WIDTH, height = HEIGHT)

    display.initialise()

    val minFrequency = 50.0 // Hz
    val maxFrequency = 11000.0 // Hz

    val fftHandler: Mic.HandleFFT = Mic.HandleFFT { fft: FFT, amplitudes: FloatArray ->
        var maxAmplitude = 0.0F
        //for every pixel calculate an amplitude
        val pixeledAmplitudes = FloatArray(WIDTH)
        //iterate the large array and map to pixels
        for (i in amplitudes.size / 800 until amplitudes.size) {
            // sort out frequency to bin to make even out
            val pixelX = frequencyToBin((i * 44100 / (amplitudes.size)).toDouble())
            pixeledAmplitudes[pixelX] += amplitudes[i]
            maxAmplitude = max(pixeledAmplitudes[pixelX], maxAmplitude)
        }
        print("[")
        pixeledAmplitudes.forEach {
            print(it)
            print(", ")
        }
        print("]")
        println()
        for (x in 0 until WIDTH) {
            val maxLight = scaleFreqBand(pixeledAmplitudes[x])
            display.setColumnUpTo(x, maxLight, Color.BLACK)
            display.clearColumnAbove(x, maxLight)
        }
        display.update()
    }

    microphone.subscribeToFFT(fftHandler)

//    while (true) {
//        for (x in 0..WIDTH) {
//            if (Random.nextBoolean()) {
//                controller.setColumn(
//                    Random.nextInt(0, WIDTH), Color(
//                        Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(
//                            0,
//                            256
//                        )
//                    )
//                )
//            } else {
//                controller.setRow(
//                    Random.nextInt(0, WIDTH), Color(
//                        Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(
//                            0,
//                            256
//                        )
//                    )
//                )
//            }
//            controller.update()
//        }
//        Thread.sleep(1000)
//    }

}

private fun frequencyToBin(frequency: Double): Int {
    val minFrequency = 50.0 // Hz
    val maxFrequency = 11000.0 // Hz
    var bin = 0
    if (frequency != 0.0 && frequency > minFrequency && frequency < maxFrequency) {
        var binEstimate = 0.0
        val minCent = PitchConverter.hertzToAbsoluteCent(minFrequency)
        val maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency)
        val absCent = PitchConverter.hertzToAbsoluteCent(frequency)
        binEstimate = ((absCent - minCent) / (maxCent - minCent)) * WIDTH
        if (binEstimate > 700) {
            println(binEstimate.toString() + "")
        }
        bin = WIDTH - 1 - binEstimate.toInt()
        if (bin == -1) {
            println("HI")
        }
    }
    return bin
}

fun scaleFreqBand(amplitude: Float): Int {
    val C = 1  // Compactness of scale (graph stretched)
    val N = HEIGHT  // Max number height of frequency scale
    val x = amplitude
    return (-((C * N) / (x + C)) + N).roundToInt()
}

private fun printMixers() {
    //Enumerates all available microphones
    val mixerInfos = AudioSystem.getMixerInfo()
    for (info in mixerInfos) {
        val m = AudioSystem.getMixer(info)
        val lineInfos = m.targetLineInfo
        if (lineInfos.size >= 1 && lineInfos[0].lineClass == TargetDataLine::class.java) {
            //Only prints out info is it is a Microphone
            println("Line Name: " + info.name) //The name of the AudioDevice
            println("Line Description: " + info.description) //The type of audio device
            for (lineInfo in lineInfos) {
                println("\t---$lineInfo")
                var line: Line
                line = try {
                    m.getLine(lineInfo)
                } catch (e: LineUnavailableException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    return
                }
                println("\t-----$line")
            }
        }
    }
}
