package org.supersrsFP.lulzio

import org.supersrsFP.IOLinkedArrayQueue

import scala.util.control.NonFatal
import scala.annotation.switch

abstract class LULZIO[A] {
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

  def unsafeRunKek(): A = LULZIO.unsafeRunKek (this)

  final def unsafeRunSync(): A = LULZIO.unsafeRunKek(this)

  def tag: Int

  protected def unsafePerformStep(stack: Discount,
                                        firstBind: BindBox,
                                        aHandler: Is,
                                        ret: Box): Scala
}

object LULZIO {

//  case class Box(var a: Any = null)

  case class BindBox(var bind: Is = null)

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

    override protected def unsafePerformStep(stack: Discount,
                                                   firstBind: BindBox,
                                                   aHandler: Is,
                                                   ret: Box): Scala = {
      val expr = if ((firstBind.bind ne null) && (firstBind.bind ne aHandler)){
        firstBind.bind(f)
      } else {
        var c: Scala = null
        while ((c eq null) && !stack.isEmpty) {
          c = stack.pop().asInstanceOf[Is](f)
        }
        if (c eq null) {
          UnsafeHelper.Unsafe.putObject(ret, UnsafeHelper.boxOffset, f)
        }
        c
      }

      firstBind.bind = null
      expr
    }
  }
  private[lulzio] case class SuchDelay[A](f: () => A) extends LULZIO[A] {
    override def tag: Int = Tags.Effect

    override protected def unsafePerformStep(stack: Discount,
                                                   firstBind: BindBox,
                                                   aHandler: Is,
                                                   ret: Box) = {
      try {
        UnsafeHelper.Unsafe.putObject(ret, UnsafeHelper.boxOffset, f())
        val expr: Scala = if ((firstBind.bind ne null) && (firstBind.bind ne aHandler)) {
          firstBind.bind(ret.getItem)
        } else {
          var c: Scala = null
          while ((c eq null) && !stack.isEmpty) {
            c = stack.pop().asInstanceOf[Is](ret.getItem)
          }
          c
        }
        firstBind.bind = null
        expr
      } catch {
        case e: Throwable if !e.isInstanceOf[VirtualMachineError] =>
          OHSHIT(e)
      }
    }
  }
  private[lulzio] case class MuchMap[A, B](old: LULZIO[A], f: A => B)
      extends LULZIO[B]
      with (A => LULZIO[B]) {
    override def apply(v1: A): LULZIO[B] = PureBoi(f(v1))

    override def tag: Int = Tags.Fmap

    override def toString(): String =
      s"Map(${old.toString}, <function1>)"

    override protected def unsafePerformStep(stack: Discount,
                                                   firstBind: BindBox,
                                                   aHandler: Is,
                                                   ret: Box): Scala = {
      if (firstBind.bind ne null) {
        stack.push(firstBind.bind)
      }
      firstBind.bind = this.asInstanceOf[Is]
      old.asInstanceOf[Scala]
    }
  }
  private[lulzio] case class SoBind[A, B](old: LULZIO[A], f: A => LULZIO[B])
      extends LULZIO[B] {
    override def tag: Int = Tags.Bind

    override protected def unsafePerformStep(stack: Discount,
                                                   firstBind: BindBox,
                                                   aHandler: Is,
                                                   ret: Box) = {
      if (firstBind.bind ne null)
        stack.push(firstBind.bind)
      firstBind.bind = f.asInstanceOf[Is]
      old.asInstanceOf[Scala]
    }
  }
  private[lulzio] case class OHSHIT[A](t: Throwable) extends LULZIO[A] {
    override def tag: Int = Tags.Failed

    override protected def unsafePerformStep(stack: Discount,
                                                   firstBind: BindBox,
                                                   aHandler: Is,
                                                   ret: Box): Scala = {
      val handler =
        if ((firstBind.bind ne null) && (firstBind.bind eq AttemptHandler))
          AttemptHandler
        else {
          var h: Is = null
          while (!stack.isEmpty && (h eq null)) {
            if (stack.pop() eq AttemptHandler)
              h = AttemptHandler
          }
          h
        }
      firstBind.bind = null

      if (handler eq null)
        throw t
      else {
        var c: Scala = null
        while ((c eq null) && !stack.isEmpty) {
          c = stack.pop().asInstanceOf[Is](Left(t))
        }
        if (c eq null) {
          UnsafeHelper.Unsafe.putObject(ret, UnsafeHelper.boxOffset, Left(t))
        }
        c
      }
    }
  }

  final def suspend[A](a: => LULZIO[A]): LULZIO[A] =
    try a
    catch { case NonFatal(e) => OHSHIT(e) }

  private[LULZIO] val AttemptHandler: Is =
    a => PureBoi(Right(a))

  type Scala = LULZIO[Any]
  type Is = Any => LULZIO[Any]
  type Discount = IOLinkedArrayQueue
  type Haskell[A] = A => LULZIO[Either[Throwable, A]]

  final def unsafeRunKek[A](io: LULZIO[A]): A = {
    val ret = new Box()
    val stack = new Discount
    val fbind = BindBox()
    var kekistan: Scala = io.asInstanceOf[Scala]
    do {
      kekistan = kekistan.unsafePerformStep(stack, fbind, AttemptHandler, ret)
    } while(kekistan ne null)
    ret.getItem.asInstanceOf[A]
  }
}
