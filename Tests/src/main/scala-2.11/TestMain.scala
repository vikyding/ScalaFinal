/**
  * Created by mengchending on 11/11/15.
  */

object TestMain {
  def main(args: Array[String]): Unit ={
   val s1=" IT IS VERY GOOD"
    val res =Parser.SplitAndFilter(s1)

    res.foreach(it=>println(it))
  }
}
