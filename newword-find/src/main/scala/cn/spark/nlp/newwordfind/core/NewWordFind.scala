package cn.spark.nlp.newwordfind.core

import cn.spark.nlp.newwordfind.trie.{NodeProcessor, Trie, TrieErgodicProcessor}
import cn.spark.nlp.newwordfind.utils.{NGram, WordCountUtils}
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
  *
  * @author songyaheng on 2017/12/7
  * @version 1.0
  */
object NewWordFind {

  def newWordRDD(sc: SparkContext, rdd: RDD[_], newWordFindConfig: NewWordFindConfig): RDD[(String, Double, Double, Int)] = {
    val lineCount = rddLineCount(rdd)
    val trieRdd = trieRDD(rdd, newWordFindConfig)
    val trieCount = trieRdd.reduce(_.join(new NodeProcessor[(Double, Int), Trie[(Double,Int)], String] {
      override def process(e: String)(t: (Double, Int), trie: Trie[(Double, Int)]) = {
        trie.value(e) match {
          case Some(tt) => trie.updateValue(e, (math.min(t._1, tt._1), t._2 + tt._2))
          case None => trie.insert(e, t)
        }
      }
    })(_))
    val btrieCount = sc.broadcast(trieCount)
    trieRdd.mapPartitions(r => rddPmi(r, newWordFindConfig, lineCount, btrieCount))
  }

  def rddLineCount(rdd: RDD[_]): Double = {
    rdd.map(line => line.toString.length)
      .sum()
  }

  def trieRDD(rdd: RDD[_], newWordFindConfig: NewWordFindConfig): RDD[Trie[(Double, Int)]] = {
    rdd.flatMap(l => NGram.nGram(l.toString, 1, newWordFindConfig.maxlen))
      .reduceByKey(WordCountUtils.count)
      .filter(wmf => wmf._2._3 >= newWordFindConfig.minCount)
      .map(WordCountUtils.energyCount)
      .partitionBy(new PrefixPartitioner(newWordFindConfig.numPartitions))
      .mapPartitions(WordCountUtils.trieRDD)
      .cache()
  }

  def rddPmi(iterator: Iterator[Trie[(Double, Int)]], newWordFindConfig: NewWordFindConfig, lineCount: Double, btrieCount: Broadcast[Trie[(Double, Int)]]): Iterator[(String, Double, Double, Int)] = {
    val trie = iterator.next()
    var array = new ArrayBuffer[(String, Double, Double, Int)]()
    trie.ergodic(new TrieErgodicProcessor[String, (Double, Int)] {
      override def process(t: String, e: (Double, Int)): Unit = {
        if (t.length >= newWordFindConfig.minlen) {
          val pmi = (0 to t.length - 2).map(i => {
            val a = t.substring(0, i + 1)
            val b = t.substring(i + 1)
            val av = btrieCount.value.value(a) match {
              case Some(v) => v._2.toDouble
              case _ => newWordFindConfig.minCount.toDouble
            }
            val bv = btrieCount.value.value(b) match {
              case Some(v) => v._2.toDouble
              case _ => newWordFindConfig.minCount.toDouble
            }
            e._2 * lineCount * 1.0 / (av * bv)
          }).min
          if (pmi >= newWordFindConfig.minPmi && e._1 >= newWordFindConfig.minInfoEnergy) {
            array += ((t, e._1, pmi, e._2))
          }
        }
      }
    })
    array.toIterator
  }

}
