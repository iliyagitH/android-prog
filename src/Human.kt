class Human(
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

    fun moveGaussMarkov(previousDirection: Double, alpha: Double = 0.8): Double {
        val randomDeviation = (Math.random() - 0.5) * Math.PI / 4
        val newDirection = alpha * previousDirection + (1 - alpha) * randomDeviation
        val normalizedDirection = (newDirection + 2 * Math.PI) % (2 * Math.PI)
        val stepLength = currentSpeed * 0.8 + Math.random() * currentSpeed * 0.4
        x += stepLength * kotlin.math.cos(normalizedDirection)
        y += stepLength * kotlin.math.sin(normalizedDirection)
        println("$fullName (Gauss-Markov) -> (${"%.2f".format(x)}, ${"%.2f".format(y)})")
        return normalizedDirection
    }
}