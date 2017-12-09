package cn.spark.nlp.newwordfind.trie

/**
  *
  * @author songyaheng on 2017/12/8
  * @version 1.0
  */
trait TrieErgodicProcessor[T, E] extends Serializable{
  def process(t: T, e: E)
}
