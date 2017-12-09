package cn.spark.nlp.newwordfind.utils

import scala.collection.mutable.ArrayBuffer

/**
  * n-gram 切词工具
  *
  * @author songyaheng on 2017/11/27
  * @version 1.0
  */
object NGram extends Serializable {
  /**
    * 单句双向切词
    * @param s
    * @return
    */
  def newWordGram(s: String, len: Int): List[(String, (Map[String, Int], Map[String, Int], Int))] = {
    val sen = "$" + s + "$"
    (1 to s.length - len + 1).map(i => {
      val w = sen.substring(i, i + len)
      val lw = sen.substring(i -1, i)
      val rw = sen.substring(i + len).substring(0, 1)
      (w, (Map(lw -> 1), Map(rw -> 1), 1))
    }).toList
  }

  def nGram(s: String, minlen: Int, maxlen: Int): List[(String, (Map[String, Int], Map[String, Int], Int))] = {
    (minlen to maxlen).flatMap( i => newWordGram(s, i)).toList
  }

  def nGramByLen(s: String, len: Int, f: Double): List[(String, Double)] =
    (0 to s.length - len).map(i => {
      (s.substring(i, i + len), f)
    }).toList


  def nGramByWord(s: String, minlen: Int, maxlen: Int, f: Double): List[(String, Double)] =
    (minlen to maxlen).flatMap( i => nGramByLen(s, i, f)).toList


}
