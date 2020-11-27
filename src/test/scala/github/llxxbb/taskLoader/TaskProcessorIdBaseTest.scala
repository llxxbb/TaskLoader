package github.llxxbb.taskLoader

import java.util

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class TaskProcessorIdBaseTest extends FunSuite with MockFactory {

  private val config = new TaskLooperConfig

  class MyTest extends TaskProcessorIdBase[String](config) {
    override def getTasks(limit: Int): util.List[String] = null
    override def doTask(task: String): Unit = ()
  }

  test("last id check") {
    val test1 = new MyTest
    assert(test1.getLastId  == 0)

    test1.setLastId(5, 10)
    assert(test1.getLastId  == 10)

    test1.setLastId(5, 11)
    assert(test1.getLastId  == 11)

    test1.setLastId(0, 100)
    assert(test1.getLastId  == 0)
  }
}
