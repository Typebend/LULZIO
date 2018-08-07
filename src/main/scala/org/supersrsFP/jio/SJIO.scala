package org.supersrsFP.jio


import org.supersrsFP.jio.JIO.{DelayJIO, PureJIO}

import scala.util.control.NonFatal

/** Scala interface to the most politically incorrect
  * IO monad.
  *
  */
object SJIO {

  def apply[A](a: => A): JIO[A] =
    new DelayJIO(() => a)

  def delay[A](a: => A): JIO[A] =
    apply(a)

  def raiseError[A](t: Throwable): JIO[A] =
    JIO.raiseError(t)

  def pure[A](a: A): JIO[A] =
    new PureJIO(a)

  def suspend[A](thunk: => JIO[A]): JIO[A] =
    try thunk
    catch {
      case NonFatal(e) => JIO.raiseError(e)
    }


}
