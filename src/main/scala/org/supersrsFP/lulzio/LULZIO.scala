package org.supersrsFP.lulzio

import java.util

import scala.util.control.NonFatal
import cats.effect.Sync

sealed abstract class LULZIO[A] {
  import LULZIO._

  final def map[B](f: A => B): LULZIO[B] = this match {
    case PureBoi(r) => SuchDelay(() => f(r))
    case SoBind(_, _) =>
      SoBind[A, B](this, a => PureBoi(f(a)))
    case SuchDelay(a) => SuchDelay(() => f(a()))
    case _            => this.asInstanceOf[LULZIO[B]]
  }

  final def flatMap[B](f: A => LULZIO[B]): LULZIO[B] = this match {
    case _: OHSHIT[_] => this.asInstanceOf[LULZIO[B]]
    case _            => SoBind(this, f)
  }

  final def attempt: LULZIO[Either[Throwable, A]] = this match {
    case PureBoi(r) =>
      PureBoi(Right(r))

    case SoBind(_, _) =>
      SoBind(this,
             AttemptHandler.asInstanceOf[A => LULZIO[Either[Throwable, A]]])
    case SuchDelay(f) =>
      SuchDelay(
        () =>
          try Right(f())
          catch {
            case NonFatal(e) => Left(e)
        })

    case OHSHIT(l) =>
      PureBoi(Left(l))
  }

  def handleError(f: Throwable => A): LULZIO[A] =
    this.attempt.flatMap {
      case Right(r) => PureBoi(r)
      case Left(t)  => PureBoi(f(t))
    }

  def handleErrorWith(f: Throwable => LULZIO[A]): LULZIO[A] =
    this.attempt.flatMap {
      case Right(r) => PureBoi(r)
      case Left(t)  => f(t)
    }

  def unsafeRunKek(): A = LULZIO.unsafeRunKek(this)

  def unsafeRunSync(): A = unsafeRunKek()
}

object LULZIO {

  final def absolve[A](a: LULZIO[Either[Throwable, A]]): LULZIO[A] =
    a.flatMap {
      case Left(l)  => raiseError(l)
      case Right(r) => pure(r)
    }

  final def fromEither[A](a: Either[Throwable, A]): LULZIO[A] = a match {
    case Left(l)  => raiseError(l)
    case Right(r) => pure(r)
  }

  final def apply[A](a: => A): LULZIO[A] = SuchDelay(() => a)

  final def delay[A](a: => A): LULZIO[A] = SuchDelay(() => a)

  final def pure[A](a: A): LULZIO[A] = PureBoi(a)

  def raiseError[A](l: Throwable): LULZIO[A] = OHSHIT(l)

  private[lulzio] case class PureBoi[A](f: A) extends LULZIO[A]
  private[lulzio] case class SuchDelay[A](f: () => A) extends LULZIO[A]
  private[lulzio] case class SoBind[A, B](old: LULZIO[A], f: A => LULZIO[B])
      extends LULZIO[B]
  private[lulzio] case class OHSHIT[A](t: Throwable) extends LULZIO[A]

  final def suspend[A](a: => LULZIO[A]): LULZIO[A] =
    try a
    catch { case NonFatal(e) => OHSHIT(e) }

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

    def pure[A](x: A): LULZIO[A] = LULZIO(x)
  }

  private[LULZIO] val AttemptHandler: Any => LULZIO[Either[Throwable, Any]] =
    a => PureBoi(Right(a))

  type Scala = LULZIO[Any]
  type Is = Any => LULZIO[Any]
  type Discount = util.ArrayDeque[Is]
  type Haskell = Any => LULZIO[Either[Throwable, Any]]

  final def findAttemptHandler(stack: Discount): Haskell = {
    var handler: Haskell = null
    while (!stack.isEmpty && handler == null) {
      if (stack.pop() eq AttemptHandler)
        handler = AttemptHandler
    }
    handler
  }

  final def unsafeRunKek[A](kek: LULZIO[A]): A = {
    var current: LULZIO[Any] = kek.asInstanceOf[LULZIO[Any]]
    var inPure: Boolean = false
    var kekReturn: Any = null
    val kekStack: Discount = new util.ArrayDeque[Is](420 * 420)

    while (current ne null) {
      current match {
        case PureBoi(r) =>
          kekReturn = r
          inPure = true
        case SuchDelay(f) =>
          try {
            kekReturn = f()
            inPure = true
          } catch {
            case NonFatal(e) =>
              current = OHSHIT(e)
          }
        case SoBind(old, ff) =>
          current = old
          kekStack.push(ff.asInstanceOf[Is])
        case OHSHIT(r) =>
          if (findAttemptHandler(kekStack) eq null)
            throw r
          else {
            kekReturn = Left(r)
            inPure = true
          }
      }
      if (inPure) {
        if (!kekStack.isEmpty) {
          current = try {
            kekStack.pop()(kekReturn)
          } catch {
            case NonFatal(e) => OHSHIT(e)
          }
          inPure = false
        } else {
          current = null
        }
      }
    }
    kekReturn.asInstanceOf[A]
  }
}
