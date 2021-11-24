import module1.opt._

Option.Some("Hello").printIfAny
Option.None.printIfAny

Option.Some(-1).filter(_ > 0)
Option.Some(123).filter(_ > 0)
val o: Option[Int] = Option.None
o.filter(_ > 0)

Option.Some(123) zip Option.Some("abcd")
Option.None zip Option.Some(123)
Option.None zip Option.None

import module1.list._

val a = List.cons(1)
val b = List.cons(2, a)

a.mkString()
b.mkString()

val c = 1 :: 2 :: 3 :: List.Nil
c.reverse
c.mkString()

b.mkString()
c.map(_ * 2).mkString()
c.filter(_ > 1)
("abc" :: "def" :: "qwe" :: List.Nil).map(_.prepended('!')).mkString()

c.reverse.mkString()

import module1.recursion._
val ff = List(0, 1, 2, 3, 4, 5, 6, 7)
ff.mkString()
ff.map(fib(_)).mkString()

val ll = List(1, 2, 3, 4)
ll.flatMap(x => List(x * 2)).mkString()
ll.flatMap(x => List(x, 2 * x)).mkString()
(c combine ll).mkString()
incList(ll).mkString()

shoutString(List("Hello", "my", "first", "home", "work")).mkString()
