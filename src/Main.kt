fun main() {
    val movables: List<Movable> = listOf(
        Human("Иванов Иван Иванович", 25, 1.5),
        Human("Петров Петр Петрович", 30, 2.0),
        Human("Сидорова Анна Михайловна", 28, 1.8)
    )

    val simulationTime = 10

    println("=== НАЧАЛО СИМУЛЯЦИИ RANDOM WALK ===")
    println("Время: $simulationTime секунд")
    println("Участников: ${movables.size}")
    println("=".repeat(40))

    for (second in 1..simulationTime) {
        println("\n⏱️  Секунда $second:")

        movables.forEach { movable ->
            movable.move()
        }

        Thread.sleep(1000)
    }

    println("\n=== СИМУЛЯЦИЯ ЗАВЕРШЕНА ===")

    println("\nФинальные позиции:")
    movables.forEach { movable ->
        if (movable is Human) {
            println("${movable.fullName}: (${"%.2f".format(movable.x)}, ${"%.2f".format(movable.y)})")
        }
    }
}