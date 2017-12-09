package cn.spark.nlp.newwordfind

import cn.spark.nlp.newwordfind.utils.{NGram, WordCountUtils}

/**
  *
  * @author songyaheng on 2017/12/4
  * @version 1.0
  */
object Test {
  def main(args: Array[String]): Unit = {
    val s = "中华人民中华"
    NGram.nGramByWord(s, 1, 4, 1).foreach(println)
  }
}
