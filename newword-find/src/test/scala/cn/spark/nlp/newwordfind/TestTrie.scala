package cn.spark.nlp.newwordfind

import cn.spark.nlp.newwordfind.trie.{NodeProcessor, Trie}

/**
  *
  * @author songyaheng on 2017/11/30
  * @version 1.0
  */
object TestTrie {
  def main(args: Array[String]): Unit = {
    val trie: Trie[Double] = new Trie[Double]()
    trie.insert("中国", 1.0)
    trie.insert("中国人", 2.0)
    trie.insert("中华人民", 3.0)
    trie.updateValue("中国人", 9.0)

    trie.insert("中国", 1.0 , new NodeProcessor[Double, Trie[Double], String] {
      override def process(e: String)(t: Double, trie: Trie[Double]) = {
        trie.value(e) match {
          case Some(tt) => trie.updateValue(e, tt + t)
          case None => trie.insert(e, t)
        }
      }
    })

    println(trie.exist("中国"))
    println(trie.value("中国"))
    println(trie.allWords("中国"))

    val trie2 = new Trie[Double]()
    trie2.insert("中华小当家", 0.5)
    trie2.insert("中华人民共和国", 0.6)
    trie2.insert("中国", 1.0)
    trie.join(new NodeProcessor[Double, Trie[Double], String] {
      override def process(e: String)(t: Double, trie: Trie[Double]) = {
        trie.value(e) match {
          case Some(tt) => trie.updateValue(e, tt + t)
          case None => trie.insert(e, t)
        }
      }
    })(trie2)


    println(trie.value("中国"))

    println(trie.allWords("中华"))

  }
}
