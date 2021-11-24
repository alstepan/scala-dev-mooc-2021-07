package module1

import java.util.UUID
import scala.annotation.tailrec
import java.time.Instant

/** referential transparency
  */
object referential_transparency {

  case class Abiturient(id: String, email: String, fio: String)

  type Html = String

  sealed trait Notification
  object Notification {
    case class Email(email: String, text: Html) extends Notification
    case class Sms(telephone: String, msg: String) extends Notification
  }

  case class AbiturientDTO(email: String, fio: String, password: String)

  trait NotificationService {
    def sendNotification(notification: Notification): Unit
    def createNotification(abiturient: Abiturient): Notification
  }

  trait AbiturientService {

    def registerAbiturient(abiturientDTO: AbiturientDTO): Abiturient
  }

  class AbiturientServiceImpl(notificationService: NotificationService)
      extends AbiturientService {

    override def registerAbiturient(
        abiturientDTO: AbiturientDTO
    ): Abiturient = {
      val abiturient = Abiturient(
        UUID.randomUUID().toString(),
        abiturientDTO.email,
        abiturientDTO.fio
      )
      notificationService.sendNotification(
        Notification.Email(abiturient.email, "Some message")
      )
      abiturient
    }

    def registerAbiturient2(
        uuid: UUID,
        abiturientDTO: AbiturientDTO
    ): Abiturient = {
      val abiturient =
        Abiturient(uuid.toString(), abiturientDTO.email, abiturientDTO.fio)
      abiturient
    }

  }
}

// recursion

object recursion {

  /** Реализовать метод вычисления n!
    * n! = 1 * 2 * ... n
    */

  def fact(n: Int): Int = {
    var _n = 1
    var i = 2
    while (i <= n) {
      _n *= i
      i += 1
    }
    _n
  }

  def factRec(n: Int): Int =
    if (n == 1) 1
    else n * factRec(n - 1)

  def factTailRec(n: Int): Int = {
    @tailrec
    def loop(n: Int, accum: Int): Int =
      if (n == 1) accum
      else loop(n - 1, n * accum)

    loop(n, 1)
  }

  /** реализовать вычисление N числа Фибоначчи
    * F0 = 0, F1 = 1, Fn = Fn-1 + Fn - 2
    */

  def fib(n: Int): Int = {
    @tailrec
    def loop(n: Int, acc1: Int, acc2: Int): Int =
      if (n == 0) acc1
      else if (n == 1) acc2
      else loop(n - 1, acc2, acc1 + acc2)

    loop(n, 0, 1)
  }

}

object hof {

  def printFactorialResult(r: Int) = println(s"Factorial result is ${r}")

  def printFibonacciResult(r: Int) = println(s"Fibonacci result is ${r}")

  def printResult[T](r: T, funcName: String) = println(
    s"$funcName result is ${r}"
  )

  def printRunningTimeFunc1[A, B](a: A)(f: A => B): Unit = {
    val current = Instant.now().toEpochMilli()
    f(a)
    val current2 = Instant.now().toEpochMilli()
    println(current2 - current)
  }

  // Follow type implementation
  def partial[A, B, C](a: A, f: (A, B) => C): B => C = b => f(a, b)

  def sum(x: Int, y: Int): Int = x + y

  val r: Int => Int = partial(1, sum)

}

/**  Реализуем тип Option
  */

object opt {

  /** Реализовать тип Option, который будет указывать на присутствие либо отсутсвие результата
    */

  // Animal
  // Dog extend Animal
  // Option[Dog] Option[Animal]

  sealed trait Option[+T] {
    def isEmpty: Boolean = this match {
      case Option.Some(v) => false
      case Option.None    => true
    }

    def get: T = this match {
      case Option.Some(v) => v
      case Option.None    => throw new Exception("Get on empty Option")
    }

    def getOrElse[TT >: T](b: TT): TT = this match {
      case Option.Some(v) => v
      case Option.None    => b
    }

    def map[B](f: T => B): Option[B] = this match {
      case Option.Some(v) => Option.Some(f(v))
      case Option.None    => Option.None
    }

    def flatMap[B](f: T => Option[B]): Option[B] = this match {
      case Option.Some(v) => f(v)
      case Option.None    => Option.None
    }

    def printIfAny: Unit = this match {
      case Option.Some(v) => println(v)
      case Option.None    => ()
    }

    def zip[B](that: Option[B]): Option[(T, B)] = (this, that) match {
      case (Option.None, _)                   => Option.None
      case (_, Option.None)                   => Option.None
      case (Option.Some(ts), Option.Some(tt)) => Option.Some((ts, tt))
    }

    def filter(f: T => Boolean): Option[T] = this match {
      case Option.Some(v) if f(v) => this
      case _                      => Option.None
    }
  }

  object Option {
    case class Some[T](v: T) extends Option[T]
    case object None extends Option[Nothing]
  }

  /** Реализовать метод printIfAny, который будет печатать значение, если оно есть
    */

  def printIfAny[T](o: Option[T]): Unit = o.printIfAny

  /** Реализовать метод zip, который будет создавать Option от пары значений из 2-х Option
    */
  def zip[A, B](o1: Option[A], o2: Option[B]): Option[(A, B)] = o1 zip o2

  /** Реализовать метод filter, который будет возвращать не пустой Option
    * в случае если исходный не пуст и предикат от значения = true
    */
  def filter[A](o: Option[A], f: A => Boolean): Option[A] = o.filter(f)

}

object list {

  /** Реализовать односвязанный иммутабельный список List
    * Список имеет два случая:
    * Nil - пустой список
    * Cons - непустой, содердит первый элемент (голову) и хвост (оставшийся список)
    */

  sealed trait List[+T] {

    def ::[TT >: T](el: TT) = List.cons(el, this)

    def mkString(delimiter: Char = ','): String = {
      @tailrec
      def loop(l: List[T], acc: String = ""): String = l match {
        case List.Nil         => acc.substring(1)
        case List.Cons(x, xs) => loop(xs, s"$acc$delimiter$x")
      }
      loop(this)
    }

    def map[B](f: T => B): List[B] = {
      @tailrec
      def loop(l: List[T], acc: List[B] = List.Nil): List[B] = l match {
        case List.Nil        => acc.reverse
        case List.Cons(h, t) => loop(t, f(h) :: acc)
      }
      loop(this)
    }

    def combine[TT >: T](that: List[TT]): List[TT] = {
      @tailrec
      def loop(l1: List[TT], l2: List[TT], acc: List[TT] = List.Nil): List[TT] =
        (l1, l2) match {
          case (List.Nil, List.Nil)          => acc.reverse
          case (List.Cons(h1, t1), _)        => loop(t1, l2, h1 :: acc)
          case (List.Nil, List.Cons(h2, t2)) => loop(List.Nil, t2, h2 :: acc)
        }
      loop(this, that)
    }

    def flatMap[B](f: T => List[B]): List[B] = {
      @tailrec
      def loop(l: List[T], acc: List[B] = List.Nil): List[B] = l match {
        case List.Nil        => acc
        case List.Cons(h, t) => loop(t, acc combine f(h))
      }
      loop(this)
    }

    def filter(f: T => Boolean): List[T] = {
      @tailrec
      def loop(l: List[T], acc: List[T] = List.Nil): List[T] = l match {
        case List.Nil                => acc.reverse
        case List.Cons(h, t) if f(h) => loop(t, h :: acc)
        case List.Cons(_, t)         => loop(t, acc)
      }
      loop(this)
    }

    def reverse: List[T] = {
      @tailrec
      def loop(l: List[T], acc: List[T] = List.Nil): List[T] = l match {
        case List.Nil        => acc
        case List.Cons(h, t) => loop(t, h :: acc)
      }
      loop(this)
    }
  }

  object List {
    final case class Cons[+T](head: T, tail: List[T]) extends List[T]
    final case object Nil extends List[Nothing]

    def cons[T](el: T, list: List[T] = Nil): List[T] = list match {
      case List.Nil              => List.Cons(el, List.Nil)
      case List.Cons(head, tail) => List.Cons(el, List.Cons(head, tail))
    }

    def apply[T](args: T*): List[T] = {
      var l: List[T] = List.Nil
      args.foreach { a =>
        l = cons(a, l)
      }
      l.reverse
    }
  }

  /** Метод cons, добавляет элемент в голову списка, для этого метода можно воспользоваться названием `::`
    */
  def cons[T](el: T, list: List[T] = List.Nil): List[T] = List.cons(el, list) 


  /** Метод mkString возвращает строковое представление списка, с учетом переданного разделителя
    */
  def mkString[T](l: List[T], delim: Char = ','): String = l.mkString(delim)

  /** Конструктор, позволяющий создать список из N - го числа аргументов
    * Для этого можно воспользоваться *
    *
    * Например вот этот метод принимает некую последовательность аргументов с типом Int и выводит их на печать
    * def printArgs(args: Int*) = args.foreach(println(_))
    */
  val l = List(1,2,3,4,5)

  /** Реализовать метод reverse который позволит заменить порядок элементов в списке на противоположный
    */
  def reverse[T](l: List[T]): List[T] = l.reverse

  /** Реализовать метод map для списка который будет применять некую ф-цию к элементам данного списка
    */
  def map[A, B](l: List[A])(f: A => B): List[B] = l.map(f)

  /** Реализовать метод filter для списка который будет фильтровать список по некому условию
    */
  def filter[T](l: List[T])(f: T => Boolean): List[T] = l.filter(f)

  /** Написать функцию incList котрая будет принимать список Int и возвращать список,
    * где каждый элемент будет увеличен на 1
    */
  def incList(list: List[Int]): List[Int] = list.map(_ + 1)

  /** Написать функцию shoutString котрая будет принимать список String и возвращать список,
    * где к каждому элементу будет добавлен префикс в виде '!'
    */
  def shoutString(list: List[String]): List[String] = list.map(_.prepended('!'))

}
