package cn.spark.nlp.newwordfind

import cn.spark.nlp.newwordfind.core.NewWordFindConfig
import org.apache.spark.{SparkConf, SparkContext}
import cn.spark.nlp.newwordfind._
/**
  *
  * @author songyaheng on 2017/12/9
  * @version 1.0
  */
object TestNewWordFind {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("new-words-find")
      .setMaster("local[3]")

    val pattern = "[\u4E00-\u9FA5]+".r
    val stopwords = "[你|我|他|她|它]+"

    val minLen = 2
    val maxLen = 6
    val minCount = 20
    val minInfoEnergy = 2.0
    val minPim = 20.0
    val numPartition = 6

    val newWordFindConfig = NewWordFindConfig(minLen, maxLen,
      minCount, minInfoEnergy, minPim, numPartition)

    val sc = new SparkContext(conf)

    val lines =sc.textFile("/Users/songyaheng/Downloads/西游记.txt")
      .flatMap(pattern.findAllIn(_).toSeq)
      .flatMap(_.split(stopwords))
      .newWord(sc, newWordFindConfig)
      .map(wepf => (wepf._2 * wepf._3 * wepf._4, wepf._1))
      .sortByKey(false, 1)
      .foreach(println)

    sc.stop()
  }
}
