package org.supersrsFP.lulzio

import java.io.{ByteArrayOutputStream, PrintStream}

import cats.effect.laws.util.{TestContext, TestInstances}
import cats._
import cats.effect.laws.discipline.SyncTests
import cats.implicits._
import org.scalacheck.{Arbitrary, Cogen, Gen}
import org.typelevel.discipline.scalatest.Discipline
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import org.typelevel.discipline.Laws

import scala.util.control.NonFatal

class JIOLAWS
  extends FunSuite
    with Matchers
    with Checkers
    with Discipline
    with TestInstances {

  implicit def cogenTask[A]: Cogen[JIO[A]] =
    Cogen[Unit].contramap(_ => ()) // YOLO

  implicit def catsEQ[A](implicit E: Eq[A]): Eq[JIO[A]] =
    new Eq[JIO[A]] {

      def eqv(x: JIO[A], y: JIO[A]): Boolean =
        x.attempt.unsafeRunSync() === y.attempt.unsafeRunSync()
    }

  implicit def arbitraryIO[A](implicit A: Arbitrary[A],
    CG: Cogen[A]): Arbitrary[JIO[A]] = {
    import Arbitrary._
    def genPure: Gen[JIO[A]] =
      arbitrary[A].map(JIO.pure[A](_))

    def genFail: Gen[JIO[A]] =
      arbitrary[Throwable].map(JIO.raiseError(_))

    def genBindSuspend: Gen[JIO[A]] =
      arbitrary[A].map(SJIO(_).flatMap(SJIO.pure(_)))

    def genSimpleTask: Gen[JIO[A]] = Gen.frequency(
      1 -> genPure,
      1 -> genFail,
      1 -> genBindSuspend
    )

    def genFlatMap: Gen[JIO[A]] =
      for {
        ioa <- genSimpleTask
        f <- arbitrary[A => JIO[A]]
      } yield ioa.flatMap[A](f)

    def getMapOne: Gen[JIO[A]] =
      for {
        ioa <- genSimpleTask
        f <- arbitrary[A => A]
      } yield ioa.map(f)

    def getMapTwo: Gen[JIO[A]] =
      for {
        ioa <- genSimpleTask
        f1 <- arbitrary[A => A]
        f2 <- arbitrary[A => A]
      } yield ioa.map(f1).map(f2)

    Arbitrary(
      Gen.frequency(
        5 -> genPure,
        1 -> genFail,
        5 -> genBindSuspend,
        5 -> getMapOne,
        5 -> getMapTwo,
        10 -> genFlatMap
      )
    )
  }
  import SJIO.jioSync

  checkAll("Sync[JIO]", SyncTests[JIO].sync[Int, Int, Int])

}
