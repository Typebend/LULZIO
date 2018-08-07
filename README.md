# LULZIO: THE GREATEST IO MONAD

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
- COMPLETE INTEROP WITH ~~DISCOUNT MONADIO~~ CATS-EFFECT `SYNC`



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


## USE ZIO TODAY AND THROW YOUR WORRIES AWAY

(..into justifying how you should've been using haskell the whole time anyway)