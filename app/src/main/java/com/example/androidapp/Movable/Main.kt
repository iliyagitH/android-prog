fun main() {
    val humans = listOf(
        Human("Иванов Иван", 25, 1.5),
        Human("Петров Пётр", 30, 2.0)
    )

    val driver = Driver("Сидоров Алексей", 35, 3.0, "автомобиль", "AB1234CD")

    val movables: List<Movable> = humans + driver

    println("=== СИМУЛЯЦИЯ ДВИЖЕНИЯ ===")

    val threads = movables.map { movable ->
        Thread {
            repeat(5) {
                movable.move()
                Thread.sleep(500)
            }
        }
    }


    threads.forEach { it.start() }

    threads.forEach { it.join() }

    if (driver is Driver) {
        driver.honk()
    }

    println("\n=== СИМУЛЯЦИЯ ЗАВЕРШЕНА ===")
}
