package cn.spark.nlp.newwordfind.utils

import cn.spark.nlp.newwordfind.trie.{NodeProcessor, Trie}
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

/**
  *
  * @author songyaheng on 2017/12/4
  * @version 1.0
  */
object WordCountUtils extends Serializable {

  /**
    * 计算左邻右邻信息和频率
    * @param a
    * @param b
    * @return
    */
  def count(a: (Map[String, Int], Map[String, Int], Int), b: (Map[String, Int], Map[String, Int], Int)): (Map[String, Int], Map[String, Int], Int) = {
    var la = a._1
    b._1.foreach(kv => {
      if (la.contains(kv._1)) {
        val v = la(kv._1) + kv._2
        la += (kv._1 -> v)
      } else {
        la += kv
      }
    })
    var lr = a._1
    b._2.foreach(kv => {
      if (lr.contains(kv._1)) {
        val v = lr(kv._1) + kv._2
        lr += (kv._1 -> v)
      } else {
        lr += kv
      }
    })
    val wc = a._3 + b._3
    (la, lr, wc)
  }

  /**
    * 计算左邻右邻信息熵
    * @param v
    * @return
    */
  def energyCount(v: (String, (Map[String, Int], Map[String, Int], Int))): (String, (Double, Int)) = {
    val lcount = v._2._1.values.sum
    val rcount = v._2._2.values.sum
    val le = v._2._1.values.map(c => {
      val p = c * 1.0 / lcount
      -1 * p * Math.log(p) / Math.log(2)
    }).sum
    val re = v._2._2.values.map(c => {
      val p = c * 1.0 / rcount
      -1 * p * Math.log(p) / Math.log(2)
    }).sum
    val e = Math.min(le, re)
    (v._1, (e, v._2._3))
  }

  def trieRDD(words: Iterator[(String, (Double, Int))]): Iterator[Trie[(Double, Int)]] = {
    val trie = new Trie[(Double, Int)]
    while (words.hasNext) {
      val w = words.next()
      trie.insert(w._1, w._2, new NodeProcessor[(Double, Int), Trie[(Double, Int)], String] {
        override def process(e: String)(t: (Double, Int), trie: Trie[(Double, Int)]) = {
          trie.value(e) match {
            case Some(ef) => trie.updateValue(e, (Math.min(ef._1, t._1), ef._2 + t._2))
            case None => trie.insert(e, t)
          }
        }
      })
    }
    List(trie).iterator
  }




  def permutations(list: List[Int]): Set[List[Int]] = {
    list match {
      case Nil => Set(Nil)
      case (head::tail) =>
        for(p0 <- permutations(tail); i<-0 to (p0 length); (xs,ys)=p0 splitAt i) yield xs:::List(head):::ys
    }
  }

}
