package org.supersrsFP.lulzio

import org.supersrsFP.IOLinkedArrayQueue

import scala.util.control.NonFatal
import scala.annotation.switch

sealed abstract class LULZIO[A] {
  import LULZIO._

  final def map[B](f: A => B): LULZIO[B] = (this.tag: @switch) match {
    case Tags.Failed => this.asInstanceOf[LULZIO[B]]
    case _           => MuchMap(this, f)
  }

  final def flatMap[B](f: A => LULZIO[B]): LULZIO[B] =
    SoBind(this, f)

  final def attempt: LULZIO[Either[Throwable, A]] = (this.tag: @switch) match {
    case Tags.Strict =>
      PureBoi(Right(this.asInstanceOf[PureBoi[A]].f))

    case Tags.Bind | Tags.Fmap =>
      SoBind(this, AttemptHandler.asInstanceOf[Haskell[A]])

    case Tags.Effect =>
      SuchDelay(
        () =>
          try Right(this.asInstanceOf[SuchDelay[A]].f())
          catch {
            case NonFatal(e) => Left(e)
        })

    case _ =>
      PureBoi(Left(this.asInstanceOf[OHSHIT[A]].t))
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

  final def unsafeRunSync(): A = LULZIO.unsafeRunKek(this)

  def tag: Int
}

object LULZIO {

  final object Tags {
    final val Bind = 0
    final val Strict = 1
    final val Effect = 2
    final val Fmap = 3
    final val Failed = 4
  }

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

  private[lulzio] case class PureBoi[A](f: A) extends LULZIO[A] {
    override def tag: Int = Tags.Strict
  }
  private[lulzio] case class SuchDelay[A](f: () => A) extends LULZIO[A] {
    override def tag: Int = Tags.Effect
  }
  private[lulzio] case class MuchMap[A, B](old: LULZIO[A], f: A => B)
      extends LULZIO[B]
      with (A => LULZIO[B]) {
    override def apply(v1: A): LULZIO[B] = PureBoi(f(v1))

    override def tag: Int = Tags.Fmap

    override def toString(): String =
      s"Map(${old.toString}, <function1>)"
  }
  private[lulzio] case class SoBind[A, B](old: LULZIO[A], f: A => LULZIO[B])
      extends LULZIO[B] {
    override def tag: Int = Tags.Bind
  }
  private[lulzio] case class OHSHIT[A](t: Throwable) extends LULZIO[A] {
    override def tag: Int = Tags.Failed
  }

  final def suspend[A](a: => LULZIO[A]): LULZIO[A] =
    try a
    catch { case NonFatal(e) => OHSHIT(e) }

  private[LULZIO] val AttemptHandler: Any => LULZIO[Either[Throwable, Any]] =
    a => PureBoi(Right(a))

  type Scala = LULZIO[Any]
  type Is = Any => LULZIO[Any]
  type Discount = IOLinkedArrayQueue
  type Haskell[A] = A => LULZIO[Either[Throwable, A]]

  final def findAttemptHandler(bFirst: Is, stack: Discount): Haskell[Any] = {
    if ((bFirst ne null) && (bFirst eq AttemptHandler))
      AttemptHandler
    else {
      var handler: Haskell[Any] = null
      while (!stack.isEmpty && (handler eq null)) {
        if (stack.pop() eq AttemptHandler)
          handler = AttemptHandler
      }
      handler
    }
  }

  final def unsafeRunKek[A](kek: LULZIO[A]): A = {
    //Last seen IO
    var current: LULZIO[Any] = kek.asInstanceOf[LULZIO[Any]]
    //Do we have a value
    var inPure: Boolean = false
    // null values or some sht
    var kekReturn: Any = null
    // Something about happy path
    var firstBind: Is = null
    // 50% off all stacks
    val kekStack: Discount = new IOLinkedArrayQueue

    //Put da handler on da stack mon it's alright
    val Ayylmao = AttemptHandler

    do {
      current match {
        case r: PureBoi[Any] @unchecked =>
          kekReturn = r.f
          inPure = true
        case r: SuchDelay[_] =>
          try {
            kekReturn = r.f()
            inPure = true
          } catch {
            case NonFatal(e) =>
              current = OHSHIT(e)
          }
        case r: SoBind[Any, Any] @unchecked =>
          if (firstBind ne null) {
            kekStack.push(firstBind)
          }
          firstBind = r.f
          current = r.old
        case m: MuchMap[Any, Any] @unchecked =>
          if (firstBind ne null) {
            kekStack.push(firstBind)
          }
          firstBind = m
          current = m.old
        case OHSHIT(r) =>
          if (findAttemptHandler(firstBind, kekStack) eq null)
            throw r
          else {
            kekReturn = Left(r)
            inPure = true
          }
          firstBind = null
      }
      if (inPure) {
        nextBind(firstBind, kekStack) match {
          case null =>
            current = null
          case f =>
            firstBind = null
            current = try {
              f(kekReturn)
            } catch {
              case e: Throwable if !e.isInstanceOf[VirtualMachineError] =>
                OHSHIT(e)
            }
            inPure = false
        }
      }
    } while (current ne null)
    kekReturn.asInstanceOf[A]
  }

  private[this] def nextBind(b1: Is, rest: Discount): Is = {
    if ((b1 ne null) && (b1 ne AttemptHandler))
      b1
    else {
      var c: Is = null
      while ((c eq null) && !rest.isEmpty) {
        c = rest.pop().asInstanceOf[Is]
      }
      c
    }
  }
}
