package module1


import java.io.Closeable
import java.io.File
import java.io.InputStream
import scala.io.BufferedSource
import java.io.InputStreamReader

object type_system {

  /**
   * Scala type system
   *
   */

   // AnyVal

   //


   // Unit



   // Null


   // Nothing

  def absurd(v: Nothing) = ???


  // Generics

  // работа с ресурсом

 lazy  val file : File = ???


  def ensureClose[S <: Closeable, R](r: S)(f: S => R): R = {
    try {
      f(r)
    } finally {
      r.close()
    }
  }

 lazy val result = ensureClose(scala.io.Source.fromFile(file))(s => s.getLines())



  /**
   *
   * class
   *
   *
   */


   class Foo{

    private var _a: String = _ 
     def a: String = _a
     def a_=(s: String) = _a = s

   }

   val foo: Foo = new Foo()

   foo.a = "foo"
   



  /**
   * Задание 1: Создать класс "Прямоугольник"(Rectangle), мы должны иметь возможность создавать прямоугольник с заданной
   * длиной(length) и шириной(width), а также вычислять его периметр и площадь
   * 
   */
   
   class Rectangle private(val x: Int, val y: Int) {
    def area(): Int = x * y
    def perimeter(): Int = 2 * (x + y)
  }





  /**
   * object
   *
   * 1. Паттерн одиночка
   * 2. Линивая инициализация
   * 3. Могут быть компаньоны
   */

   object Rectangle{
      def apply(x: Int, y: Int): Rectangle = new Rectangle(x, y)
      def apply(x: Int): Rectangle = new Rectangle(x, x)
   }


   val rec4 = Rectangle(2, 4)


   def foo(s: => String) = ???

   def bar: String = ???



  /**
   * case class
   *
   */

   case class Rectangle2(width: Int, length: Int)

   val rec2 = Rectangle2(4, 2)
   val rec3 = Rectangle2(4, 2)

   val square = rec3.copy(length = 4)

   rec2 == rec3 // true


   // создать case класс кредитная карта с двумя полями номер и cvc






  /**
   * case object
   *
   * Используются для создания перечислений или же в качестве сообщений для Акторов
   */



  /**
   * trait
   *
   */

   trait Service{
     def getUser: String
     val getUser2: String
   }


   class ServiceImpl extends Service{
     val getUser: String = ???
     lazy val getUser2: String = ???
   }



  class A {
    def foo() = "A"
  }

  trait B extends A {
    override def foo() = "B" + super.foo()
  }

  trait C extends B {
    override def foo() = "C" + super.foo()
  }

  trait D extends A {
    override def foo() = "D" + super.foo()
  }

  trait E extends C {
    override def foo(): String = "E" + super.foo()
  }

  val v = new A with D with C with B

  // CBDA

  // A -> D -> B -> C

  val v1 = new A with E with D with C with B

  // DECBA

  // A -> B -> C -> E -> D






  /**
   * Value classes и Universal traits
   */


}
