package display

import java.awt.Color

abstract class Display(val width: Int, val height: Int) {

    abstract fun initialise()

    abstract fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color)

    abstract fun update()

    abstract fun clear()

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

    open fun setColumnUpTo(xCoordinate: Int, topCoordinate: Int, colour: Color) {
        for (y in 1 until topCoordinate) {
            setPixel(xCoordinate = xCoordinate, yCoordinate = y, colour = colour)
        }
    }

    open fun clearColumnAbove(xCoordinate: Int, topCoordinate: Int) {
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
}
