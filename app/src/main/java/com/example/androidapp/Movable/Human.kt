open class Human(
    var fullName: String,
    var age: Int,
    override var currentSpeed: Double
) : Movable {
    override var x: Double = 0.0
    override var y: Double = 0.0

    override fun move() {
        val direction = Math.random() * 2 * Math.PI
        val stepLength = Math.random() * currentSpeed
        val deltaX = stepLength * kotlin.math.cos(direction)
        val deltaY = stepLength * kotlin.math.sin(direction)
        x += deltaX
        y += deltaY
        println("$fullName переместился в точку (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }
}