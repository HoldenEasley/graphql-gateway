package cloud.holden.springapp.query

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class MathQuery : Query {
  fun add(array: IntArray) = array.sum()

  suspend fun mulitply(array: IntArray) = coroutineScope {
    withContext(Dispatchers.Default) { array.fold(1L) { a, b -> a * b } }
  }

  suspend fun factorial(int: BigInteger) = coroutineScope {
    withContext(Dispatchers.Default) { factorial(int, BigInteger.ONE) }
  }

  suspend fun fibonacci(int: BigInteger) = coroutineScope {
    withContext(Dispatchers.Default) {
      fibonacci(
        int,
        BigInteger.ZERO,
        BigInteger.ONE
      )
    }
  }

  suspend fun lucas(int: BigInteger) = coroutineScope {
    withContext(Dispatchers.Default) {
      fibonacci(
        int,
        BigInteger.valueOf(2L),
        BigInteger.valueOf(1L)
      )
    }
  }

  private tailrec fun fibonacci(n: BigInteger, a: BigInteger, b: BigInteger): BigInteger {
    return if (n == BigInteger.ZERO) a else fibonacci(n - BigInteger.ONE, b, a + b)
  }

  private tailrec fun factorial(n: BigInteger, run: BigInteger = BigInteger.ONE): BigInteger = when {
    n == BigInteger.ONE -> run
    else -> factorial(n - BigInteger.ONE, run * n)
  }

}