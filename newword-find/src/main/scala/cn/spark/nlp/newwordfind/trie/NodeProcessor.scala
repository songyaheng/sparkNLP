package cn.spark.nlp.newwordfind.trie

/**
  *
  * @author songyaheng on 2017/11/28
  * @version 1.0
  */
trait NodeProcessor[T, R, E] extends Serializable{
  def process(e: E)(t: T, trie: Trie[T]): R
}
