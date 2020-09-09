package display

import java.awt.Color

class DisplayController(private val width: Int, private val height: Int, private val display: Display) {

    fun initialise() {
        display.initialise(width, height)
    }

    fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color) {
        display.setPixel(xCoordinate, yCoordinate, colour)
    }

    fun setRow(yCoordinate: Int, colour: Color) {
        for (x in 1 until width) {
            setPixel(xCoordinate = x, yCoordinate = yCoordinate, colour = colour)
        }
    }

    fun setColumn(xCoordinate: Int, colour: Color) {
        for (y in 1 until height) {
            setPixel(xCoordinate = xCoordinate, yCoordinate = y, colour = colour)
        }
    }

    fun setColumnUpTo(xCoordinate: Int, topCoordinate: Int, colour: Color) {
        for (y in 1 until topCoordinate) {
            setPixel(xCoordinate = xCoordinate, yCoordinate = y, colour = colour)
        }
    }

    fun clearColumnAbove(xCoordinate: Int, topCoordinate: Int) {
        for (y in topCoordinate until height) {
            // CHANGE THIS LATER?
            setPixel(xCoordinate = xCoordinate, yCoordinate = y, colour = Color.WHITE)
        }
    }

    fun setBorder(colour: Color) {
        setColumn(0, colour)
        setColumn(height - 1, colour)
        setRow(0, colour)
        setRow(height - 1, colour)
    }

    fun setAll(colour: Color) {
        for (y in 1 until height) {
            setRow(y, colour)
        }
    }

    fun update() {
        display.update()
    }

    fun clear() {
        display.clear()
    }

}