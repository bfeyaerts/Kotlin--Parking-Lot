package parking

data class Car(val registration: String, val color: String)

const val PATTERN_COLOR = "(?<color>\\w+)"
const val PATTERN_REGISTRATION = "(?<registration>(?:\\w|-)+)"

var parkingLot: Array<Car?>? = null
enum class Command(private val pattern: String, private val processor: (MatchResult) -> Unit = {}) {
    CREATE("create (?<size>\\d+)", {
        val size = it.groups.get("size")!!.value.toInt()
        parkingLot = Array<Car?>(size) { null }
        println("Created a parking lot with $size spots.")
    }),
    STATUS("status", {
        if (parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            var empty = true
            for (i in 0 until parking.parkingLot!!.size) {
                if (parkingLot!![i] != null) {
                    val car = parkingLot!![i]!!
                    println("${i + 1} ${car.registration} ${car.color}")
                    empty = false
                }
            }
            if (empty) {
                println("Parking lot is empty.")
            }
        }
    }),
    PARK("park $PATTERN_REGISTRATION $PATTERN_COLOR", {
        if (parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            val registration = it.groups.get("registration")!!.value
            val color = it.groups.get("color")!!.value

            var position = -1
            for (i in 0 until parking.parkingLot!!.size) {
                if (parkingLot!![i] == null) {
                    position = i
                    break
                }
            }

            if (position < 0) {
                println("Sorry, the parking lot is full.")
            } else {
                parkingLot!![position] = Car(registration, color)
                println("$color car parked in spot ${position + 1}.")
            }
        }
    }),
    LEAVE("leave (?<number>\\d+)", {
        if (parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            val number = it.groups.get("number")!!.value.toInt()
            if (parkingLot!![number - 1] == null)
                println("There is no car in spot $number.")
            else {
                parkingLot!![number - 1] = null
                println("Spot $number is free.")
            }
        }
    }),
    EXIT("exit"),
    REG_BY_COLOR("reg_by_color $PATTERN_COLOR", {
        if (parking.parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            val color = it.groups.get("color")!!.value
            val cars = parking.parkingLot?.filterNotNull()
                ?.filter { it.color.uppercase() == color.uppercase() }
                ?.map { it.registration } ?: emptyList()

            println(if (cars.isEmpty()) {
                "No cars with color $color were found."
            } else {
                cars.joinToString()
            })
        }
    }),
    SPOT_BY_COLOR("spot_by_color $PATTERN_COLOR", {
        if (parking.parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            val color = it.groups.get("color")!!.value
            val cars = mutableListOf<Int>()
            for (i in 0 until parking.parkingLot!!.size) {
                if (parkingLot!![i] != null && parkingLot!![i]!!.color.uppercase() == color.uppercase()) {
                    cars += i+1
                }
            }
            println(if (cars.isEmpty()) {
                "No cars with color $color were found."
            } else {
                cars.joinToString()
            })
        }
    }),
    SPOT_BY_REG("spot_by_reg $PATTERN_REGISTRATION", {
        if (parking.parkingLot == null) {
            println("Sorry, a parking lot has not been created.")
        } else {
            val registration = it.groups.get("registration")!!.value
            val cars = mutableListOf<Int>()
            for (i in 0 until parking.parkingLot!!.size) {
                if (parkingLot!![i] != null && parkingLot!![i]!!.registration == registration) {
                    cars += i+1
                }
            }
            println(if (cars.isEmpty()) {
                "No cars with registration number $registration were found."
            } else {
                cars.joinToString()
            })
        }
    }),
    ;
    private val regex = pattern.toRegex()

    fun matches(string: String): Boolean {
        return regex.matches(string)
    }

    fun process(string: String) {
        val matchResult = regex.find(string)
        processor.invoke(matchResult!!)
    }
}

fun main() {
    while (true) {
        val line = readLine()!!
        for (command in Command.values()) {
            if (command.matches(line)) {
                if (command == Command.EXIT)
                    return
                command.process(line)
                break
            }
        }
    }
}
