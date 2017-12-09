package cn.spark.nlp.newwordfind.trie

import scala.collection.immutable.TreeMap


/**
  *
  * @author songyaheng on 2017/11/27
  * @version 1.0
  */
class Node[T](char: Char) extends Serializable {
  var content: Char = char
  var isEnd: Boolean = false
  var childMap: Map[Char, Node[T]] = TreeMap[Char, Node[T]]()
  var t: T = _
  var depth: Int = 0
  var count: Int = 0

  def nextNode(char: Char): Option[Node[T]] = {
    if (childMap.nonEmpty) {
      childMap.get(char)
    } else {
      None
    }
  }
}
