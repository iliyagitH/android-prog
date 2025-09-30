import kotlin.concurrent.thread

fun main() {
    val humans = listOf(
        Human("Иванов Иван Иванович", 25, 1.5),
        Human("Петров Петр Петрович", 30, 2.0),
        Human("Сидорова Анна Михайловна", 28, 1.8),
        Human("Козлов Дмитрий Сергеевич", 35, 1.2)
    )

    val driver = Driver(
        "Смирнов Алексей Викторович",
        32,
        3.0,
        "легковой автомобиль",
        "AB123456"
    )

    val simulationTime = 10

    println("=== НАЧАЛО СИМУЛЯЦИИ ===")
    println("Время: $simulationTime секунд")

    driver.honk()

    for (second in 1..simulationTime) {
        println("\nСекунда $second:")

        val threads = mutableListOf<Thread>()

        humans.forEach { human ->
            val thread = thread {
                human.move()
            }
            threads.add(thread)
        }

        val driverThread = thread {
            driver.move()
        }
        threads.add(driverThread)

        threads.forEach { it.join() }

        Thread.sleep(1000)
    }

    println("\n=== СИМУЛЯЦИЯ ЗАВЕРШЕНА ===")

    println("\nФинальные позиции:")
    humans.forEach { human ->
        println("${human.fullName}: (${"%.2f".format(human.x)}, ${"%.2f".format(human.y)})")
    }
    println("${driver.fullName} (водитель): (${"%.2f".format(driver.x)}, ${"%.2f".format(driver.y)})")
}