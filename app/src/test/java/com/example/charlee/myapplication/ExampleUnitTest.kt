package com.example.charlee.myapplication

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  companion object {
    // 局部刷新上下文
    var lastHttpResponse: HttpResponse? = null
  }

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)

    runBlocking {
      printlnWithThreadName("runBlocking")
      flow {
        delay(100)
        emit("fuck")
        printlnWithThreadName("flow")
      }.flowOn(IO).collect {
        println(it)
        printlnWithThreadName("collect")
      }
    }
    printlnWithThreadName("out")
  }

  @Test
  fun testNetwork() {
    val customerDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    runBlocking {
      buildHttpRequest()
        .flatMapConcat { sendRequest(it).flowOn(IO) }
        .flatMapConcat {
          buildHttpResponse(it).flowOn(IO).combine(buildKwaiVideoResponse(it).flowOn(IO).buffer()) { a, b ->
            Result(a, b)
          }
        }
        .flatMapConcat {
            combine(saveResponseToCache(it.httpResponse).flowOn(IO),
              htmlRender(it.httpResponse).flowOn(customerDispatcher),
              playVideo(it.kwaiVideoResponse).flowOn(IO)) { a, b, c ->
              lastHttpResponse = it.httpResponse
              a && b && c
            }
        }.collect {
          if (it) {
            printlnWithThreadName("network ok!!!")
          }
        }
    }
  }

  private fun buildHttpRequest(): Flow<HttpRequest> {
    return flow {
      printlnWithThreadName("buildHttpRequest")
      emit(
        HttpRequest(
          "get www.baidu.com/index.html http/1.1",
          mapOf(Pair("Connection", "keep-alive")), ""
        )
      )
    }
  }

  private fun sendRequest(request: HttpRequest): Flow<InputStream> {
    return flow {
      printlnWithThreadName("sendRequest")
      println("build collection by socket")
      delay(1000)
      println("server collected")
      delay(1000)
      println("request send to server")
      delay(1000)
      println("get response from server")
      val inputStream =
        "HTTP/1.1 200 OK\r\nContent-Type:text/plain\r\n\r\n<html>welcome to baidu<\\htm>".byteInputStream()
      emit(inputStream)
    }
  }

  private fun buildHttpResponse(inputStream: InputStream): Flow<HttpResponse> {
    return flow {
      printlnWithThreadName("buildHttpResponse")
      println("read server response")
      delay(1000)
      val lines = inputStream.bufferedReader().readLines()
      val header = lines[1].split(":")
      val response = HttpResponse(lines[0], mapOf(Pair(header[0], header[1])), lines[3])
      emit(response)
    }
  }

  private fun buildKwaiVideoResponse(inputStream: InputStream): Flow<KwaiVideoResponse> {
    return flow {
      printlnWithThreadName("buildKwaiVideoResponse")
      for (i in 1..5) {
        delay(1000)
        printlnWithThreadName("get 1M KwaiVideoData")
        emit(KwaiVideoResponse(byteArrayOf()))
      }
    }
  }

  private fun htmlRender(httpResponse: HttpResponse): Flow<Boolean> {
    return flow {
      if (lastHttpResponse == httpResponse) return@flow

      printlnWithThreadName("htmlRender")
      val responseBody = httpResponse.responseBody
      println("render html by browser")
      delay(2000)
      println("html showed $responseBody")
      emit(true)
    }
  }

  private fun playVideo(kwaiVideoResponse: KwaiVideoResponse): Flow<Boolean> {
    return flow {
      printlnWithThreadName("playVideo")
      delay(2000)
      printlnWithThreadName("video decode and show")
      emit(true)
    }
  }

  private fun saveResponseToCache(httpResponse: HttpResponse): Flow<Boolean> {
    return flow {
      if (lastHttpResponse == httpResponse) return@flow
      printlnWithThreadName("saveResponseToCache")
      delay(1000)
      printlnWithThreadName("httpResponse saved to disk")
      emit(true)
    }
  }

  private fun printlnWithThreadName(printString: String) {
    println("${SimpleDateFormat("hh:mm:ss").format(Date())} $printString, thread name:${Thread.currentThread().name}")
  }

  data class HttpRequest(
    val requestLine: String,
    val headers: Map<String, String>,
    val requestBody: String
  )

  data class HttpResponse(
    val requestLine: String,
    val headers: Map<String, String>,
    val responseBody: String
  )

  data class KwaiVideoResponse(val videoData: ByteArray)

  data class Result(val httpResponse: HttpResponse, val kwaiVideoResponse: KwaiVideoResponse)
}
