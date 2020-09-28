import be.tarsos.dsp.util.fft.FFT
import display.Display
import display.web.WebDisplay
import java.awt.Color
import java.time.Instant
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Line
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt


const val WIDTH = 20
const val HEIGHT = 20

fun main(args: Array<String>) {
    println("Hello world")
    println(args)

    // Current mic caps out around 4000 hz
    // Need to modify visualiser so it can go up to a certain max frequency.)
    // As otherwise visualiser range is only up until about half the width. (rest is just 0 as mic detects 0 amplitude)

    val microphone = Mic();
    microphone.startMic()

    val display: Display = WebDisplay(width = WIDTH, height = HEIGHT)
    display.initialise()

    val handler: Mic.FFTHandler = DisplayHandler(display)
    microphone.subscribeToFFT(handler)

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

private fun calculateFrequency(sample: Int, samples: Int, sampleRate: Int = 44100): Int =
    sample * sampleRate / (samples)

private fun frequencyToBin(frequency: Float, maxFrequency: Float, bins: Int, gamma: Double = 1.5): Int {
    return ((((frequency / maxFrequency).pow(1.0.div(gamma).toFloat())) * (bins - 1))).roundToInt()
}

fun scaleFreqBand(magnitude: Float): Int {
    val C = 15  // Compactness of scale (graph stretched)
    val N = HEIGHT  // Max number height of frequency scale
    val x = magnitude
    return (-((C * N) / (x + C)) + N).roundToInt()
}

private class DisplayHandler(val display: Display) : Mic.FFTHandler {
    var previousTime: Long = 0L
    var cumulativeMagnitudes = FloatArray(WIDTH)
    var samples = 0

    private val translationConstant = 1

    override fun accept(fft: FFT, magnitudes: FloatArray) {
        //for every pixel calculate an magnitude
        //iterate the large array and map to pixels
        val maxFrequency = calculateFrequency(magnitudes.size - 1, magnitudes.size).toFloat()
        var maxBinMagnitude = FloatArray(WIDTH)
        for (i in magnitudes.size / 800 until magnitudes.size) {
            val bin = frequencyToBin((calculateFrequency(i, magnitudes.size)).toFloat(), maxFrequency, WIDTH)
            maxBinMagnitude[bin] = max(maxBinMagnitude[bin], magnitudes[i])
        }
        for (bin in cumulativeMagnitudes.indices) {
            cumulativeMagnitudes[bin] += log2((maxBinMagnitude[bin] + translationConstant).pow(2))
        }
        samples += 1
        if (Instant.now().toEpochMilli() > previousTime + 5) {
            previousTime = Instant.now().toEpochMilli()
            outputDisplay()
            samples = 0
            cumulativeMagnitudes = FloatArray(WIDTH)
        }
    }

    private fun outputDisplay() {
        print("[")
        cumulativeMagnitudes.forEach {
            print(it / samples)
            print(", ")
        }
        print("]")
        println()
        for (x in 0 until WIDTH) {
            val maxLight = scaleFreqBand(cumulativeMagnitudes[x] / samples)
            display.setColumnUpTo(x, maxLight, Color.BLACK)
            display.clearColumnAbove(x, maxLight)
        }
        display.update()
    }
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