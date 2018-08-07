//package org.supersrsFP.lulzio
//
//import java.io.{ByteArrayOutputStream, PrintStream}
//
//import cats.effect.laws.util.{TestContext, TestInstances}
//import cats._
//import cats.effect.laws.discipline.SyncTests
//import cats.implicits._
//import org.scalacheck.{Arbitrary, Cogen, Gen}
//import org.typelevel.discipline.scalatest.Discipline
//import org.scalatest.prop.Checkers
//import org.scalatest.{FunSuite, Matchers}
//import org.typelevel.discipline.Laws
//
//import scala.util.control.NonFatal
//
//class WowSuchLAWS
//    extends FunSuite
//    with Matchers
//    with Checkers
//    with Discipline
//    with TestInstances {
//
//  implicit def cogenTask[A]: Cogen[LULZIO[A]] =
//    Cogen[Unit].contramap(_ => ()) // YOLO
//
//  implicit def catsEQ[A](implicit E: Eq[A]): Eq[LULZIO[A]] =
//    new Eq[LULZIO[A]] {
//
//      def eqv(x: LULZIO[A], y: LULZIO[A]): Boolean =
//        x.attempt.unsafeRunKek() === y.attempt.unsafeRunKek()
//    }
//
//  implicit def arbitraryIO[A](implicit A: Arbitrary[A],
//                              CG: Cogen[A]): Arbitrary[LULZIO[A]] = {
//    import Arbitrary._
//    def genPure: Gen[LULZIO[A]] =
//      arbitrary[A].map(LULZIO.pure)
//
//    def genFail: Gen[LULZIO[A]] =
//      arbitrary[Throwable].map(LULZIO.raiseError)
//
//    def genBindSuspend: Gen[LULZIO[A]] =
//      arbitrary[A].map(LULZIO.delay(_).flatMap(LULZIO(_)))
//
//    def genSimpleTask: Gen[LULZIO[A]] = Gen.frequency(
//      1 -> genPure,
//      1 -> genFail,
//      1 -> genBindSuspend
//    )
//
//    def genFlatMap: Gen[LULZIO[A]] =
//      for {
//        ioa <- genSimpleTask
//        f <- arbitrary[A => LULZIO[A]]
//      } yield ioa.flatMap(f)
//
//    def getMapOne: Gen[LULZIO[A]] =
//      for {
//        ioa <- genSimpleTask
//        f <- arbitrary[A => A]
//      } yield ioa.map(f)
//
//    def getMapTwo: Gen[LULZIO[A]] =
//      for {
//        ioa <- genSimpleTask
//        f1 <- arbitrary[A => A]
//        f2 <- arbitrary[A => A]
//      } yield ioa.map(f1).map(f2)
//
//    Arbitrary(
//      Gen.frequency(
//        5 -> genPure,
//        1 -> genFail,
//        5 -> genBindSuspend,
//        5 -> getMapOne,
//        5 -> getMapTwo,
//        10 -> genFlatMap
//      )
//    )
//  }
//
//  checkAll("Sync[LULZIO]", SyncTests[LULZIO].sync[Int, Int, Int])
//
//}
