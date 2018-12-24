package github.llxxbb.taskLoader

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

import scala.collection.JavaConverters._

class TaskLooperImplTest extends FunSuite with MockFactory {
  private val processer: TaskProcesser[String] = mock[TaskProcesser[String]]

  test("config illegal for constructor") {
    intercept[Exception] {
      val testee = new TaskLooperImpl[String](processer, null)
    }
  }
  test("config illegal for `setConfig`") {
    val testee = new TaskLooperImpl[String](processer, new TaskLooperConfig)
    intercept[Exception] {
      testee.setConfig(null)
    }
  }

  test("load error, logout can see hello exception") {
    (processer.getTasks _).expects(100).throwing(new Exception("hello"))
    new TaskLooperImpl[String](processer, new TaskLooperConfig)
    Thread.sleep(100)
  }
  test("load error after one load, logout can see hello exception") {
    (processer.getTasks _).expects(100).returning(List("A", "B", "C").asJava)
    (processer.doTask _).expects("A")
    (processer.doTask _).expects("B")
    (processer.doTask _).expects("C")
    (processer.getTasks _).expects(100).throwing(new Exception("hello"))
    new TaskLooperImpl[String](processer, new TaskLooperConfig)
    Thread.sleep(100)
  }

  test("stop stop stop: logout should have nothing") {
    val config = new TaskLooperConfig
    config.cmd = TaskCommand.STOP
    val testee = new TaskLooperImpl[String](processer, config)
    (processer.getTasks _).expects(100).never()
    testee.setConfig(config)
    testee.setConfig(config)
  }
  test("stop stop start, logout can see many `do task : B`") {
    val config = new TaskLooperConfig
    config.cmd = TaskCommand.STOP
    val testee = new TaskLooperImpl[String](processer, config)
    (processer.getTasks _).expects(100).atLeastOnce().returning(List("B").asJava)
    (processer.doTask _).expects("B").atLeastOnce()
    testee.setConfig(config)
    config.cmd = TaskCommand.RUN
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("stop start stop， logout can see `looper stopped`") {
    val config = new TaskLooperConfig
    config.cmd = TaskCommand.STOP
    config.idleTime = 200
    val testee = new TaskLooperImpl[String](processer, config)
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.doTask _).expects("A").atLeastOnce()
    (processer.getTasks _).expects(100).returning(null)
    config.cmd = TaskCommand.RUN
    testee.setConfig(config)
    Thread.sleep(100)
    config.cmd = TaskCommand.STOP
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("stop start start, logout can see tow `looper starting`") {
    val config = new TaskLooperConfig
    config.cmd = TaskCommand.STOP
    config.idleTime = 200
    val testee = new TaskLooperImpl[String](processer, config)
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.getTasks _).expects(100).returning(null)
    (processer.doTask _).expects("A").atLeastOnce()
    config.cmd = TaskCommand.RUN
    testee.setConfig(config)
    Thread.sleep(100)
    (processer.getTasks _).expects(100).returning(List("B").asJava)
    (processer.getTasks _).expects(100).returning(null)
    (processer.doTask _).expects("B").atLeastOnce()
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("start stop stop, logout can see one `looper stopped`") {
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.getTasks _).expects(100).anyNumberOfTimes().returning(null)
    (processer.doTask _).expects("A").atLeastOnce()
    val testee = new TaskLooperImpl[String](processer, new TaskLooperConfig)
    // set to stop
    Thread.sleep(100)
    var config = new TaskLooperConfig
    config.cmd = TaskCommand.STOP
    config.setLimit(20)
    config.setThreadNum(5)
    config.idleTime = 2
    testee.setConfig(config)
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("logout can see : start stop start") {
    val config = new TaskLooperConfig
    config.idleTime = 200
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.getTasks _).expects(100).returning(null)
    (processer.doTask _).expects("A").atLeastOnce()
    val testee = new TaskLooperImpl[String](processer, config)
    // set to stop
    Thread.sleep(100)
    config.cmd = TaskCommand.STOP
    config.setLimit(20)
    config.setThreadNum(5)
    config.idleTime = 190
    testee.setConfig(config)
    Thread.sleep(50)
    // set to start
    (processer.getTasks _).expects(20).returning(List("B").asJava)
    (processer.getTasks _).expects(20).returning(null)
    (processer.doTask _).expects("B").atLeastOnce()
    config.cmd = TaskCommand.RUN
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("start start stop") {
    val config = new TaskLooperConfig
    config.idleTime = 200
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.getTasks _).expects(100).returning(null)
    (processer.doTask _).expects("A").atLeastOnce()
    val testee = new TaskLooperImpl[String](processer, config)
    Thread.sleep(100)
    // start again
    (processer.getTasks _).expects(20).returning(List("B").asJava)
    (processer.getTasks _).expects(20).returning(null)
    (processer.doTask _).expects("B").atLeastOnce()
    config.setLimit(20)
    config.setThreadNum(5)
    config.idleTime = 192
    testee.setConfig(config)
    Thread.sleep(100)
    // set to stop
    config.cmd = TaskCommand.STOP
    testee.setConfig(config)
    Thread.sleep(100)
  }
  test("start start start") {
    val config = new TaskLooperConfig
    config.idleTime = 200
    (processer.getTasks _).expects(100).returning(List("A").asJava)
    (processer.getTasks _).expects(100).returning(null)
    (processer.doTask _).expects("A").atLeastOnce()
    val testee = new TaskLooperImpl[String](processer, config)
    Thread.sleep(100)
    // start again
    (processer.getTasks _).expects(20).returning(List("B").asJava)
    (processer.getTasks _).expects(20).returning(null)
    (processer.doTask _).expects("B").atLeastOnce()
    config.setLimit(20)
    config.setThreadNum(5)
    config.idleTime = 199
    testee.setConfig(config)
    Thread.sleep(100)
    // set to stop
    (processer.getTasks _).expects(20).returning(List("C").asJava)
    (processer.getTasks _).expects(20).returning(null)
    (processer.doTask _).expects("C").atLeastOnce()
    config.idleTime = 210
    testee.setConfig(config)
    Thread.sleep(100)
  }

  test("long time `stop` and `start`") {
    val sleep = (x: String) => {
      Thread.sleep(1000)
    }
    val config = new TaskLooperConfig
    (processer.getTasks _).expects(100).atLeastOnce().returning(List("C","C","C","C","C","C","C","C","C","C").asJava)
    (processer.doTask _).expects("C").atLeastOnce().onCall(sleep)
    val testee = new TaskLooperImpl[String](processer, config)
    Thread.sleep(100)
  }


}
