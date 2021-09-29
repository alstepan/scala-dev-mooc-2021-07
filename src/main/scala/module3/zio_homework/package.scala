package module3
import zioConcurrency.printEffectRunningTime
import zio.{Has, Task, ULayer, ZIO, ZLayer}
import zio.clock.{Clock, sleep}
import zio.console._
import zio.duration.durationInt
import zio.macros.accessible
import zio.random._

import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.io.StdIn
import scala.language.postfixOps
import _root_.module3.zio_homework.config.AppConfig

package object zio_homework {
  /**
   * 1.
   * Используя сервисы Random и Console, напишите консольную ZIO программу которая будет предлагать пользователю угадать число от 1 до 3
   * и печатать в когнсоль угадал или нет. Подумайте, на какие наиболее простые эффекты ее можно декомпозировать.
   */


  def guessNumber = zio.random.nextIntBounded(10).map(_ + 1) //загадываем число
  def getNumber = zio.console.putStr("Guess the number (from 1 to 10): ") *> zio.console.getStrLn.map(_.toInt)
  def guessOnce = for {
    guess <- guessNumber
    input <- getNumber
    _ <- zio.console.putStrLn(if (guess == input) "You guessed!" else "Try again!")
  } yield guess == input

  lazy val guessProgram = guessOnce.repeatN(2)

  /**
   * 2. реализовать функцию doWhile (общего назначения), которая будет выполнять эффект до тех пор, пока его значение в условии не даст true
   * 
   */

  def doWhile[R, E, A](eff: ZIO[R, E, A])(preduicate: A => Boolean) = ???

  /**
   * 3. Реализовать метод, который безопасно прочитает конфиг из файла, а в случае ошибки вернет дефолтный конфиг
   * и выведет его в консоль
   * Используйте эффект "load" из пакета config
   */

  def loadConfigOrDefault = config.load.orElse(ZIO.succeed(AppConfig("default", "default")))


  /**
   * 4. Следуйте инструкциям ниже для написания 2-х ZIO программ,
   * обратите внимание на сигнатуры эффектов, которые будут у вас получаться,
   * на изменение этих сигнатур
   */


  /**
   * 4.1 Создайте эффект, который будет возвращать случайеым образом выбранное число от 0 до 10 спустя 1 секунду
   * Используйте сервис zio Random
   */
  lazy val eff = ZIO.sleep(1 second) *> zio.random.nextIntBounded(10)

  /**
   * 4.2 Создайте коллукцию из 10 выше описанных эффектов (eff)
   */
  lazy val effects = List.fill(10)(eff)

  
  /**
   * 4.3 Напишите программу которая вычислит сумму элементов коллекци "effects",
   * напечатает ее в консоль и вернет результат, а также залогирует затраченное время на выполнение,
   * можно использовать ф-цию printEffectRunningTime, которую мы разработали на занятиях
   */
  lazy val sum = ZIO.foldLeft(effects)(0)((x, y) => y.map(v => v + x))
  lazy val app = printEffectRunningTime(sum).flatMap(v => zio.console.putStrLn(s"Sum is $v"))


  /**
   * 4.4 Усовершенствуйте программу 4.3 так, чтобы минимизировать время ее выполнения
   */

  lazy val fastSum = ZIO.reduceAllPar(ZIO.succeed(0), effects)((x, y) => x + y)
  lazy val appSpeedUp = printEffectRunningTime(fastSum).flatMap(v => zio.console.putStrLn(s"Sum is $v"))


  /**
   * 5. Оформите ф-цию printEffectRunningTime разработанную на занятиях в отдельный сервис, так чтобы ее
   * молжно было использовать аналогично zio.console.putStrLn например
   */


   /**
     * 6.
     * Воспользуйтесь написанным сервисом, чтобы созадть эффект, который будет логировать время выполнения прогаммы из пункта 4.3
     *
     * 
     */

  lazy val appWithTimeLogg = ???

  /**
    * 
    * Подготовьте его к запуску и затем запустите воспользовавшись ZioHomeWorkApp
    */

  lazy val runApp = appSpeedUp
  
}
