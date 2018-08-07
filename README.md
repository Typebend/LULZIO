# LULZIO: THE GREATEST IO MONAD

[![Build Status](https://travis-ci.org/jmcardon/LULZIO.svg?branch=master)](https://travis-ci.org/jmcardon/LULZIO)

Have you ever wanted to to write purely functional programs?

Have you ever wanted to not be bogged down by things such as "Asynchrony",
"Concurrency" or "Parallel programs"?

Have you ever thought scalaz people were mean and smelled a bit rancid?

Have you ever thought cats are freeloaders and dogs are objectively superior creatures that
at least serve a purpose?

Have you ever thought "These FP people all live in the ivory tower I need something PRAGMATIC
AND SUPER SERIOUS!"?

If so, introducing...

## LULZIO

### THE ONLY IO MONAD THAT LETS YOU NEVER WORRY ABOUT CONCURRENCY

.. _because it doesn't have concurrency_

## FEATURES

- COMPLETELY CROSS PLATFORM HOLY SHT
- SO PRINCIPLED IT CUTS YOUR BUGS IN HALF, LEAVING YOUR PROGRAMS BALANCED,
AS ALL THINGS SHOULD BE.
- BLAZING FAST. SO FAST ACTUALLY IT LEAVES YOU UNSATISFIED WHILE IT GOES OUT FOR A SMOKE.
- YOUR PROGRAMS COMPOSE NOW SO YOU CAN WRITE `.handleError(_ => LULZIOIO(println("shit")))` EVERYWHERE
LIKE YOU'RE ALREADY USED TO
- COMPLETELY REFERENTIALLY TRANSPARENT. REFACTOR WITHOUT FEAR, BUT CONTINUE
WRITING PROGRAMS THAT READ AND COMPOSE LIKE THEY'RE IMPERATIVE PROGRAMS BECAUSE FP IN SCALA
ISN'T ABOUT COMPOSITION IT'S ABOUT PRETENDING YOUR GARBAGE CODE IS FINE AFTER YOU CHUCK IT IN IO.
- WRITE ORTHOGONAL PRIMITIVES, WHATEVER THE FUCK THAT MEANS. MAYBE IT MEANS WHEN YOU MAKE LIKE, A 
T IN THE SAND AND WONDER IF THE PRIMITIVE PEOPLES BEFORE YOU WONDERED ABOUT WHAT RIGHT
ANGLES WERE.
- COMPLETE INTEROP WITH ~~DISCOUNT MONADIO~~ CATS-EFFECT `SYNC`. PASSES ALL `SYNC` ~~LMAO PROPERTY TESTS~~ LAWS.



### ARE YOU A ~~DISCOUNT HASKELL~~ SCALA GUY?

NO F'ING PROBLEM

```scala

object ENTERPRISE {
  def main(args: Array[String]): Unit = {
    import org.supersrsFP.lulzio.LULZIO

    def superSeriousEnterpriseMethodSignature(n: BigInt, l: LULZIO[BigInt]): LULZIO[BigInt] =
      l.flatMap(i => if(n <= 0) LULZIO.pure(i) else superSeriousEnterpriseMethodSignature(n-1, LULZIO.delay {
        val cachedMult = n*i
        println(s"MEGA SRS ENTERPRISE FACTORIAL $cachedMult")
        cachedMult
      }))

    val myProgram: LULZIO[BigInt] =
      for {
        o <- LULZIO.pure(10000)
        n <- superSeriousEnterpriseMethodSignature(o, LULZIO.pure(o))
      } yield n

    // At the end of the world or lunch or whatever
    println(myProgram.unsafeRunSync())
  }
}
```


### ARE YOU A JAVA GUY

FULL CROSS PLATFORM SUPPORT SO YOU CAN HAVE IO MONAD ON
ANDROIDS N SHIT

```java
import org.supersrsFP.jio.JIO;

import java.math.BigInteger;

public class JIOMain {
    private static JIO<BigInteger> enterpriseFactorial(BigInteger i, JIO<BigInteger> j){
        return j.flatMap(k -> {
            if(i.compareTo(BigInteger.ZERO) <= 0) return JIO.pure(k);
            else return enterpriseFactorial(i.subtract(BigInteger.valueOf(1)), JIO.delay(() -> {
                BigInteger mult = i.multiply(k);
                System.out.println("ENTERPRISE FACTORIAL CALC " + mult);
                return mult;
            }));
        });
    }
    public static void main(String[] args) throws Throwable {
        System.out.println(JIO.pure(100)
                .flatMap(i -> enterpriseFactorial(BigInteger.valueOf(i), JIO.pure(BigInteger.valueOf(1))))
                .unsafeRunSync());
    }
}
```

## LULZIO IS THE GREATEST OF IO MONADS. JUST LOOK AT THESE BENCH RESULTS
```
[info] Benchmark                                                           (depth)   Mode  Cnt      Score      Error  Units
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.catsDeepAttempt               1000  thrpt    3  14124.150 ± 2398.464  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.futureDeepAttempt             1000  thrpt    3   4571.254 ± 1136.555  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.jioDeepAttempt                1000  thrpt    3  5 BILLION ± 1643.094  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.lulzDeepAttempt               1000  thrpt    3  5 BILLION ±  810.932  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.monixDeepAttempt              1000  thrpt    3  13687.391 ±  716.555  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.scalazDeepAttempt             1000  thrpt    3  53748.552 ± 3312.606  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.scalazDeepAttemptBaseline     1000  thrpt    3  15489.390 ±  492.409  ops/s
[info] supersrsFP.lulzio.IODeepAttemptBenchmark.thunkDeepAttempt              1000  thrpt    3  10438.052 ± 1238.573  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.catsDeepFlatMap                 20  thrpt    3   1646.821 ±  389.195  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.futureDeepFlatMap               20  thrpt    3     48.282 ±   68.365  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.javaDeepFlatMap                 20  thrpt    3  5 BILLION ±   45.315  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.lulzDeepFlatMap                 20  thrpt    3  5 BILLION ±  459.516  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.monixDeepFlatMap                20  thrpt    3   1491.573 ±  304.076  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.scalazDeepFlatMap               20  thrpt    3   1656.152 ±  519.860  ops/s
[info] supersrsFP.lulzio.IODeepFlatMapBenchmark.thunkDeepFlatMap                20  thrpt    3   1753.555 ±  350.969  ops/s

```

## USE ZIO TODAY AND THROW YOUR WORRIES AWAY

(..into justifying how you should've been using haskell the whole time anyway)
