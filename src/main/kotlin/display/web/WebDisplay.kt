package display.web

import display.Display
import kweb.*
import kweb.state.KVar
import kweb.state.render
import java.awt.Color
import kotlin.properties.Delegates

const val CUBE_PIXEL_SIZE : Int = 20

class WebDisplay : Display {

    val counter = KVar(0)
    var js = KVar("")
    val id = KVar("")
    private lateinit var canvas: CanvasElement
    private var width by Delegates.notNull<Int>()
    private var height by Delegates.notNull<Int>()

    override fun initialise(widthNum: Int, heightNum: Int) {
        this.width = widthNum * CUBE_PIXEL_SIZE
        this.height = heightNum * CUBE_PIXEL_SIZE
        Kweb(port = 16097) {
            doc.body.new {
                val canvas = canvas(width, height)
                id.value = canvas.id!!
                render(js) {
                    canvas.execute(js.value)
                }
            }
        }
    }

    override fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color) {
        if (id.value != "") {
            js.value = "var c = document.getElementById(\"${id.value}\");\n" +
                    "var ctx = c.getContext(\"2d\");\n" +
                    "ctx.fillStyle = \"${String.format("#%02x%02x%02x", colour.red, colour.green, colour.blue)}\";" +
                    "ctx.fillRect(${xCoordinate * CUBE_PIXEL_SIZE}, ${this.height - (yCoordinate * CUBE_PIXEL_SIZE)}, $CUBE_PIXEL_SIZE, $CUBE_PIXEL_SIZE);"
        }
    }


    override fun update() {
    }

    override fun clear() {
        if (id.value != "") {
            js.value = "var c = document.getElementById(\"${id.value}\");\n" +
                    "var ctx = c.getContext(\"2d\");\n" +
                    "ctx.clearRect(0, 0, ${this.width}, ${this.height});"
        }
    }
}
