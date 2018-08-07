package org.supersrsFP.jio

import cats.effect.Sync
import org.supersrsFP.jio.JIO.DelayJIO

import scala.util.control.NonFatal

object interop {


  implicit val jioSync: Sync[JIO] = new Sync[JIO] {
    override def suspend[A](thunk: => JIO[A]): JIO[A] =
      try thunk
      catch {
        case NonFatal(e) => JIO.raiseError(e)
      }

    override def delay[A](thunk: => A): JIO[A] = new DelayJIO[A](() => thunk)

    override def raiseError[A](e: Throwable): JIO[A] = JIO.raiseError(e)

    override def handleErrorWith[A](fa: JIO[A])(
      f: Throwable => JIO[A]): JIO[A] =
      fa.handleErrorWith(f)

    override def pure[A](x: A): JIO[A] = JIO.pure(x)

    override def flatMap[A, B](fa: JIO[A])(f: A => JIO[B]): JIO[B] =
      fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => JIO[Either[A, B]]): JIO[B] =
      f(a).flatMap {
        case Right(r) => JIO.pure(r)
        case Left(l)  => tailRecM(l)(f)
      }

    override def attempt[A](fa: JIO[A]): JIO[Either[Throwable, A]] =
      fa.attempt()
  }

}
