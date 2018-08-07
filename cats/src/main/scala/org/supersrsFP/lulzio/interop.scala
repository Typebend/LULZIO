package org.supersrsFP.lulzio

import cats.effect.Sync
import org.supersrsFP.lulzio.LULZIO.OHSHIT

object interop {
  implicit val superSRSSync: Sync[LULZIO] = new Sync[LULZIO] {
    def suspend[A](thunk: => LULZIO[A]): LULZIO[A] = LULZIO.suspend(thunk)

    override def delay[A](thunk: => A): LULZIO[A] = LULZIO(thunk)

    def raiseError[A](e: Throwable): LULZIO[A] = OHSHIT(e)

    def handleErrorWith[A](fa: LULZIO[A])(
      f: Throwable => LULZIO[A]): LULZIO[A] =
      fa.handleErrorWith(f)

    def flatMap[A, B](fa: LULZIO[A])(f: A => LULZIO[B]): LULZIO[B] =
      fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => LULZIO[Either[A, B]]): LULZIO[B] =
      f(a).flatMap {
        case Left(l)  => tailRecM(l)(f)
        case Right(r) => LULZIO(r)
      }

    def pure[A](x: A): LULZIO[A] = LULZIO.pure(x)
  }
}
