import display.Display
import display.DisplayController
import display.web.WebDisplay
import java.awt.Color
import javax.sound.sampled.*
import kotlin.random.Random


const val WIDTH = 35
const val HEIGHT = 35

fun main(args: Array<String>) {
    println("Hello world")
    println(args)

    val microphone : Mic = Mic();

    microphone.startMic()

    val display : Display = WebDisplay()

    val controller = DisplayController(width = WIDTH, height = HEIGHT, display = display)

    controller.initialise()

    while (true) {
        for (x in 0..WIDTH) {
            if (Random.nextBoolean()) {
                controller.setColumn(Random.nextInt(0, WIDTH), Color(Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(0, 256)))
            } else {
                controller.setRow(Random.nextInt(0, WIDTH), Color(Random.nextInt(0, 256), Random.nextInt(0, 256), Random.nextInt(0, 256)))
            }
            controller.update()
        }
        Thread.sleep(1000)
    }





//    while (true) {
//        microphone.getData()
//    }


//    initForLiveMonitor()
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

private fun initForLiveMonitor() {
    val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 2, 4, 44100F, false)
    try {

        //Speaker
        val sourceLine = AudioSystem.getSourceDataLine(format) as SourceDataLine
        sourceLine.open()

        //Microphone
        val targetLine = AudioSystem.getTargetDataLine(format) as TargetDataLine
        targetLine.open()
        val monitorThread: Thread = object : Thread() {
            override fun run() {
                targetLine.start()
                sourceLine.start()
                val data = ByteArray(targetLine.bufferSize / 5)
                var readBytes: Int
                while (true) {
                    readBytes = targetLine.read(data, 0, data.size)
                    sourceLine.write(data, 0, readBytes)
                }
            }
        }
        println("Start LIVE Monitor for 15 seconds")
        monitorThread.start()
        Thread.sleep(15000)
        targetLine.stop()
        targetLine.close()
        println("End LIVE Monitor")
    } catch (lue: LineUnavailableException) {
        lue.printStackTrace()
    } catch (ie: InterruptedException) {
        ie.printStackTrace()
    }
}