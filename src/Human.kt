class Human(
    var fullName: String,
    var age: Int,
    var currentSpeed: Double
) {
    var x: Double = 0.0
    var y: Double = 0.0

    // Метод движения - Random Walk модель
    fun move() {
        // 1. Генерируем случайное направление (0 до 2π радиан)
        val direction = Math.random() * 2 * Math.PI

        // 2. Генерируем случайную длину шага (0 до currentSpeed)
        val stepLength = Math.random() * currentSpeed

        // 3. Вычисляем изменение координат по формулам:
        // Δx = step × cos(θ)
        // Δy = step × sin(θ)
        val deltaX = stepLength * kotlin.math.cos(direction)
        val deltaY = stepLength * kotlin.math.sin(direction)

        // 4. Обновляем координаты
        x += deltaX
        y += deltaY

        // 5. Выводим информацию о движении
        println("$fullName переместился в точку (${"%.2f".format(x)}, ${"%.2f".format(y)})")
    }

    // Альтернативная реализация Gauss-Markov модели (дополнительно)
    fun moveGaussMarkov(previousDirection: Double, alpha: Double = 0.8): Double {
        // 1. Генерируем случайное отклонение
        val randomDeviation = (Math.random() - 0.5) * Math.PI / 4

        // 2. Вычисляем новое направление с памятью о предыдущем
        val newDirection = alpha * previousDirection + (1 - alpha) * randomDeviation

        // 3. Нормализуем направление в диапазон [0, 2π]
        val normalizedDirection = (newDirection + 2 * Math.PI) % (2 * Math.PI)

        // 4. Длина шага (может быть постоянной или случайной)
        val stepLength = currentSpeed * 0.8 + Math.random() * currentSpeed * 0.4

        // 5. Обновляем координаты
        x += stepLength * kotlin.math.cos(normalizedDirection)
        y += stepLength * kotlin.math.sin(normalizedDirection)

        println("$fullName (Gauss-Markov) -> (${"%.2f".format(x)}, ${"%.2f".format(y)})")

        return normalizedDirection
    }
}