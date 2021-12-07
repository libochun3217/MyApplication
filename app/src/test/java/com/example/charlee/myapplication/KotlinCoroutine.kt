package com.example.charlee.myapplication

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import rx.Observable
import java.io.File

class KotlinCoroutine {
    @Test
    fun testAsync() {
        runBlocking {
            val name = async { getNameFromServer() }
            println("my name is ${name.await()}")
        }
    }

    @Test
    fun testLambda() {
        val text = "This reference is designed for you to easily learn Kotlin in a matter of hours. Start with the basic syntax, then proceed to more advanced topics. While reading, you can try out the examples in the online IDE.\n" +
                "\n" +
                "Once you get an idea of what Kotlin looks like, try yourself in solving some Kotlin Koans - interactive programming exercises. If you are not sure how to solve a Koan, or you're looking for a more elegant solution, check out Kotlin idioms.\n" +
                "\n"
        Observable.from(text.toCharArray().asIterable()).filter { !it.isWhitespace() }.groupBy { it }.subscribe {
                o -> o.count().subscribe{
            println("${o.key} -> $it")
        }
        }
    }

    private suspend fun getNameFromServer(): String {
        delay(1000)
        return "charlee"
    }
}