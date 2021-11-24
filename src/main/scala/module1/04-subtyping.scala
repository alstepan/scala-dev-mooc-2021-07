package module1

import java.time.YearMonth

object subtyping {


  /**
   * Type Operators
   */

  trait Vehicle
  trait Car        extends Vehicle
  trait Moto       extends Vehicle
  object Harley    extends Moto
  object Mustang   extends Car

  type IsSubtypeOf[A, B >: A]

  type IsSupertypeOf[A, B <: A]


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Vehicle
   *
   */

   val t1 : IsSubtypeOf[Car, Vehicle] = ???


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Mustang
   *
   */

   val t2: IsSubtypeOf[Mustang.type, Car] = ???


  /**
   *
   * С помощью типа выразить отношение Vehicle и Harley, причем чтобы они шли в этом порядке
   *
   */

   val t3: IsSupertypeOf[Vehicle, Harley.type ] = ???


  /**
   * В этом примере вам нужно правильно выбрать оператор отношения,
   * чтобы среди идущих ниже выражений, те которые корректны по смыслу компилировались, а остальные нет
   */

  def isInstanceOf[A, B >: A](a: A): Unit = ???




    lazy val mustCompile1    = isInstanceOf[Mustang.type, Car](Mustang)
    lazy val mustCompile2    = isInstanceOf[Harley.type, Moto](Harley)
    // lazy val mustNotCompile1 = isInstanceOf[Mustang.type, Moto](Mustang)
    // lazy val mustNotCompile2 = isInstanceOf[Harley.type, Car](Harley)


  // Вариантность
  class Garage[T]


  // Вариантность полей
  // val
//  class Garage2[+T](val vehicle: T)
//  val harley = new Moto {}
//  val harleyGarage = new Garage2[Vehicle](harley)
//  val carGarage: Garage2[Car] = harleyGarage
//  val _: Car = carGarage.vehicle


  // var
//  class Garage3[T](var vehicle: T)
//  val harley = new Moto {}
//  val harleyGarage = new Garage3[Moto](harley)
//  val garageVehicle: Garage3[Vehicle] = harleyGarage
//  garageVehicle.vehicle = new Car {}

  // method args
  // class Garage4[+T]{
  //   def put(vehicle: T): Unit
  // }
// val garageVehicle: Garage4[Vehicle] = new Garage4[Car]{
//   override def put(vehicle: Car): Unit = ???
// }
// val _ = garageVehicle.put(new Moto {})


  // method return value
  // abstract class Garage5[-T]{
  //   def get: T
  // }

  // val garageVehicle = new Garage5[Vehicle] {
  //   override def get: Vehicle = new Moto {}
  // }
  // val carGarage: Garage5[Car] = garageVehicle

  // val _: Car = carGarage.get




  object adt{


    object tuples{


      // Boolean * Unit
      // true unit
      // false unit

      /**
       * Tuples
       *
       */
      type ProductUnitBoolean = (Boolean, Unit)

      /**
       * Создать возможные экземпляры с типом ProductUnitBoolean
       */

      val p1 = (true, ())
      val p2 = (false, ())


      /**
       * Реализовать тип Person который будет содержать имя и возраст
       */

       type Person = (String, Int)


      /**
       *
       *  Реализовать тип `CreditCard` который может содержать номер (String),
       *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
       */

      type CreditCard = (String, java.time.YearMonth, String, Short)

      lazy val cc: CreditCard = ???


    }

    object case_classes {
      /**
       * Case classes
       */



      //  Реализовать Person с помощью case класса
      case class Person(name: String, age: Int)


      // Создать экземпляр для Tony Stark 42 года
      lazy val tonyStark = Person("Tony", 43)




      // Создать case class для кредитной карты
      case class CreditCard(number: String, date: YearMonth, cv: Short, name: String)




      // Pattern matching


      /**
       * используя паттерн матчинг напечатать имя и возраст
       */

       val (x, y) = (1, "foo")
       val Person(w, z) = tonyStark

       def printNameAndAge: Unit = tonyStark match {
         case Person(n, a) => println(s"$n $a")
       }






      final case class Employee(name: String, address: Address)
      final case class Address(street: String, number: Int)

      val alex = Employee("Alex", Address("XXX", 221))

      /**
       * воспользовавшись паттерн матчингом напечатать номер из поля адрес
       */

      alex match {
        case Employee(_, Address(_, number)) => println(s"$number")
      }


      /**
       * Паттерн матчинг может содержать литералы.
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно соотвествовать Alex
       * 2. Все остальные
       */

      alex match {
        case Employee("Alex", _) => println("yeee")
        case _ => println("nope")
      }



      /**
       * Паттерны могут содержать условия. В этом случае case сработает,
       * если и паттерн совпал и условие true.
       * Условия в паттерн матчинге называются гардами.
       */

      alex match {
        case Employee(n, _) if n == "Alex" => println("yeee")
        case _ => println("nope")
      }



      /**
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно начинаться с A
       * 2. Все остальные
       */
      alex match {
        case Employee(n, _) if n.startsWith("A") => println("ok")
        case _ => println("not ok")
      }


      /**
       *
       * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
       * x @ ..., где x это любая переменная. Это переменная может использоваться, как в условии,
       * так и внутри кейса
       */

      alex match {
        case e @ Employee(a, _) => println(e.address)
        case _ => println("nope")
      }

      alex match {
        case a: Employee => ???
      }


      /**
       * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
       */

       sealed trait A
       case class B(v: Int) extends A
       case class C(v: Int) extends A
       case class D(v: Int) extends A

      val a: A = ???

      a match {
        case B(_) | C(_) => println("yes")
        case D(v) => println("no")
      }

      val b: Int = ???

      b match {
        case v: Int if v >0 => ???
      }

    }


    object either{


      /**
       * Sum
       */

      // Unit + Boolean  () | true | false




      /**
       * Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
       * Также как и кортежи обладает целым рядом полезных методов
       * Иммутабелен
       */

      type IntOrString = Either[Int, String]

      /**
       * Реализовать экземпляр типа IntOrString с помощью конструктора Right(_)
       */
      val intOrString: IntOrString = Right("Hello")

      final case class CreditCard()
      final case class WireTransfer()
      final case class Cash()

      /**\
       * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
       */
      type PaymentMethod = Either[CreditCard, Either[WireTransfer, Cash]]

    }




    object sealed_traits{
      /**
       * Также Sum type можно представить в виде sealed trait с набором альтернатив
       */


      sealed trait Card
      object Card {
        final case class Clubs(points: Int)    extends Card // крести
        final case class Diamonds(points: Int) extends Card // бубны
        final case class Spades(points: Int)   extends Card // пики
        final case class Hearts(points: Int)   extends Card // червы
      }

      lazy val card: Card = Card.Spades(10)


      /**
       * Написать паттерн матчинг на 10 пику, и на все остальное
       */
      card match {
        case Card.Spades(10) => println("you are lucky")
        case _ => println("nice try")
      }



      /**
       * Написать паттерн матчинг который матчит карты номиналом >= 10
       */
      card match {
        case Card.Spades(x) if x >= 10 => println("you are lucky")
        case _ => println("nice try")
      }

    }


    val fp: PartialFunction[Any, String] = {
      case string: String => string
      case int: Int => int.toString
    }

    fp.isDefinedAt(2.0) // false

    val f: (Any) => String = {
      case i: Int => "Int"
      case d: Double => "Double"
      case _ => "Other"
    }

    // f.isDefined - value isDefinedAt is not a member of Any => String



    def ff(a: Any): String = a match {
      case i: Int => "Int"
      case d: Double => "Double"
      case _ => "Other"
    }

  }
}