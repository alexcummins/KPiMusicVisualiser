package display.web

import display.Display
import kweb.CanvasElement
import kweb.Kweb
import kweb.canvas
import kweb.new
import kweb.state.KVar
import kweb.state.render
import java.awt.Color

const val CUBE_PIXEL_SIZE: Int = 20

class WebDisplay(width: Int, height: Int) : Display(width, height) {

    private val counter = KVar(0)
    private val id = KVar("")

    private var js = KVar("")
    private var rectsToDraw: MutableList<Rectangle> = mutableListOf()

    private lateinit var canvas: CanvasElement

    override fun initialise() {
        Kweb(port = 16097) {
            doc.body.new {
                val canvas = canvas(width * CUBE_PIXEL_SIZE, height * CUBE_PIXEL_SIZE)
                id.value = canvas.id!!
                render(js) {
                    canvas.execute(js.value)
                }
            }
        }
    }

    override fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color) {
        rectsToDraw.add(
            Rectangle(
                xCoordinate = xCoordinate * CUBE_PIXEL_SIZE,
                yCoordinate = yCoordinate * CUBE_PIXEL_SIZE,
                width = CUBE_PIXEL_SIZE,
                height = CUBE_PIXEL_SIZE,
                colour = colour
            )
        )
    }

    override fun setColumnUpTo(xCoordinate: Int, topCoordinate: Int, colour: Color) {
        rectsToDraw.add(
            Rectangle(
                xCoordinate = xCoordinate * CUBE_PIXEL_SIZE,
                yCoordinate = height * CUBE_PIXEL_SIZE,
                width = CUBE_PIXEL_SIZE,
                height = -(CUBE_PIXEL_SIZE * topCoordinate),
                colour = colour
            )
        )
    }

    override fun clearColumnAbove(xCoordinate: Int, topCoordinate: Int) {
        rectsToDraw.add(
            Rectangle(
                xCoordinate = xCoordinate * CUBE_PIXEL_SIZE,
                yCoordinate = CUBE_PIXEL_SIZE * (height - topCoordinate),
                width = CUBE_PIXEL_SIZE,
                height = -(CUBE_PIXEL_SIZE * (height - topCoordinate)),
                colour = Color.white
            )
        )
    }

    override fun update() {
        if (id.value != "") {
            val stringBuilder = StringBuilder()
            stringBuilder.append("var c = document.getElementById(\"${id.value}\");\n")
            stringBuilder.append("var ctx = c.getContext(\"2d\");\n")
            rectsToDraw.forEach {
                stringBuilder.append(
                    "ctx.fillStyle = \"${
                        String.format("#%02x%02x%02x", it.colour.red, it.colour.green, it.colour.blue)
                    }\";\n"
                )
                stringBuilder.append("ctx.fillRect(${it.xCoordinate}, ${it.yCoordinate}, ${it.width}, ${it.height});\n")
            }
            rectsToDraw.clear()
            js.value = stringBuilder.toString()
        }
    }

    override fun clear() {
        if (id.value != "") {
            js.value = "var c = document.getElementById(\"${id.value}\");\n" +
                    "var ctx = c.getContext(\"2d\");\n" +
                    "ctx.clearRect(0, 0, ${this.width}, ${this.height});"
        }
    }
}
