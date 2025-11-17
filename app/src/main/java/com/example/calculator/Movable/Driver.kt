class Driver(
    fullName: String,
    age: Int,
    currentSpeed: Double,
    var vehicleType: String,
    var licenseNumber: String
) : Human(fullName, age, currentSpeed) {

    override fun move() {
        val direction = 0.0
        val stepLength = currentSpeed * 0.5 + Math.random() * currentSpeed * 0.5
        val deltaX = stepLength * kotlin.math.cos(direction)
        val deltaY = stepLength * kotlin.math.sin(direction)
        x += deltaX
        y += deltaY
        println("🚗 $fullName (водитель $vehicleType) движется прямо в точку (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }

    fun honk() {
        println("🚗 $fullName сигналит: Би-бип!")
    }
}
