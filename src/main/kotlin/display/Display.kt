package display

import java.awt.Color

abstract class Display(private val width: Int, private val height: Int) {

    abstract fun initialise()

    abstract fun setPixel(xCoordinate: Int, yCoordinate: Int, colour: Color)

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

    abstract fun update()

    abstract fun clear()

}