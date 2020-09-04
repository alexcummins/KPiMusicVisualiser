package display.web

import kweb.*

class WebDisplay

fun main() {
    Kweb(port = 16097) {
        doc.body.new {
            h1().text("Hello World!")
        }
    }
}
