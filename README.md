# TerrariaServerUpdater

Rewrite of my music visualiser program for the Raspberry Pi. 
Previous visualiser would only analyse .mp4 files, whereas this one will visualiser live audio input from a mic.
Currently only tested output to web display, and need to implement rpi_ws281x library once I have access to my LEDs and RPI back at Uni.
Designed so the display is modular so I can easily plug in the LED display and just implement the interface and it will work the same as I have designed.

Need to make sure code is optimised enough to run on RPI.

## Requirements
(TODO)
* Kotlin
* JDK

## Installation

(TODO)
* Runs on JVM

## Usage

When run will use your main audio input and output display to http://0.0.0.0:16097

# References

* Window functions (https://en.wikipedia.org/wiki/Window_function)
* Techniques to improve audio visualization (https://dlbeer.co.nz/articles/fftvis.html)

## License
[MIT](https://choosealicense.com/licenses/mit/)
