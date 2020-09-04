package display

import java.awt.Color

interface Display {

    fun initialise(width: Int, height: Int)

    fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color)

    fun update()

    fun clear()
}
