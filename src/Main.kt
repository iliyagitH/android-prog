fun main() {
    val humans = listOf(
        Human("Иванов Иван Иванович", 25, 1.5),
        Human("Петров Петр Петрович", 30, 2.0),
        Human("Сидорова Анна Михайловна", 28, 1.8)
    )

    val simulationTime = 10

    println("=== НАЧАЛО СИМУЛЯЦИИ RANDOM WALK ===")
    println("Время: $simulationTime секунд")
    println("Участников: ${humans.size}")
    println("=".repeat(40))

    for (second in 1..simulationTime) {
        println("\n⏱️  Секунда $second:")

        humans.forEach { human ->
            human.move()
        }

        Thread.sleep(1000)
    }

    println("\n=== СИМУЛЯЦИЯ ЗАВЕРШЕНА ===")

    println("\nФинальные позиции:")
    humans.forEach { human ->
        println("${human.fullName}: (${"%.2f".format(human.x)}, ${"%.2f".format(human.y)})")
    }
}