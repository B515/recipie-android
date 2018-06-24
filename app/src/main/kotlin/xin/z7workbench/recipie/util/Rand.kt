package xin.z7workbench.recipie.util

import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun Random.nextInt(range: IntRange): Int {
    return range.start + nextInt(range.last - range.start)
}

fun rand(range: IntRange): Int {
    return ThreadLocalRandom.current().nextInt(range)
}

