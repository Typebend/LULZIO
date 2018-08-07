package org.supersrsFP.lulzio


import sun.misc.Unsafe
import java.lang.reflect.Field
object UnsafeHelper {

  val Unsafe: Unsafe = {

    val f: Field = classOf[Unsafe].getDeclaredField("theUnsafe")
    f.setAccessible(true)
    f.get(null).asInstanceOf[Unsafe]
  }

  val boxOffset: Long =
    Unsafe.objectFieldOffset(classOf[Box].getDeclaredField("item"))
}
