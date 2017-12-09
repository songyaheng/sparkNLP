package cn.spark.nlp.newwordfind.trie

import scala.collection.mutable.ArrayBuffer

/**
  *
  * @author songyaheng on 2017/11/28
  * @version 1.0
  */
class Trie[T] extends Serializable {

  val root: Node[T] = new Node[T](' ')

  def insert(word: String, t: T): Trie[T] = {
    insert(word, t, null)
  }

  /**
    * 插入数据的方法
    * @param word
    * @param t
    * @param processor
    */
  def insert(word: String, t: T, processor: NodeProcessor[T, Trie[T], String]): Trie[T] = {
    this.synchronized {
      if (word.isEmpty) return this
      value(word) match {
        case Some(tt) => {
          if (processor == null) {
            return this
          } else {
            return processor.process(word)(t, this)
          }
        }
        case None => {
          var curentNode: Node[T] = root
          var deep: Int = 0
          word.trim.foreach(c => {
            deep = deep + 1
            curentNode.nextNode(c) match {
              case Some(nd) =>
                curentNode = nd
              case None =>
                curentNode.childMap += (c -> new Node[T](c))
                curentNode.count = curentNode.childMap.size
                curentNode.nextNode(c) match {
                  case Some(nd) =>
                    curentNode = nd
                    curentNode.depth = deep
                  case None =>
                    return this
                }
            }
          })
          curentNode.t = t
          curentNode.isEnd = true
          this
        }
      }

    }
  }

  /**
    * 判断是否存在
    * @param word
    * @return
    */
  def exist(word: String): Boolean = {
    var curentNode = root
    word.trim.toCharArray.foreach(c => {
      if (curentNode.nextNode(c).isEmpty) {
        false
      } else {
        curentNode.nextNode(c) match {
          case Some(nd) => curentNode = nd
          case None => return false
        }
      }
    })
    if (curentNode.isEnd) {
      true
    } else {
      false
    }
  }

  /**
    * 获取输入字符串的标签值
    * @param word
    * @return
    */
  def value(word: String): Option[T] = {
    if (word.isEmpty) return None
    var curentNode = root
    word.toCharArray.foreach(c => {
      curentNode.nextNode(c) match {
        case Some(nd) => curentNode = nd
        case None => return None
      }
    })
    if (curentNode.isEnd) {
      Some(curentNode.t)
    } else {
      None
    }
  }

  def updateValue(word: String, t: T): Trie[T] = {
    if (word.isEmpty) return this
    this.synchronized {
      var curentNode = root
      word.toCharArray.foreach(c => {
        curentNode.nextNode(c) match {
          case Some(nd) =>
            curentNode = nd
          case None => return this
        }
      })
      if (curentNode.isEnd) curentNode.t = t
      this
    }
  }

  /**
    * 获取前缀的所有词
    * @param prifix
    * @return
    */
  def allWords(prifix: String): ArrayBuffer[String] = {
    val rs: ArrayBuffer[String] = ArrayBuffer[String]()
    if (prifix.isEmpty) {
      return rs
    }
    var node: Node[T] = root
    prifix.trim.toCharArray.foreach(c => {
      if (node.count == 0) {
        return rs
      } else {
        node.nextNode(c) match {
          case Some(nd) => node = nd
          case None => return rs
        }
      }
    })
    if (node.count != 0) {
      fullWords(node, prifix, rs)
    }
    rs
  }

  /**
    * 递归获取节点值
    * @param node
    * @param profix
    * @param arrayBuffer
    */
  private def fullWords(node: Node[T], profix: String, arrayBuffer: ArrayBuffer[String]): Unit = {
    node.childMap.values.foreach(nd => {
      fullWords(nd, profix + nd.content, arrayBuffer)
    })
    if (node.isEnd) arrayBuffer += profix
  }

  /**
    * 两棵树的join
    * @param trie
    * @return
    */
  def join(processor: NodeProcessor[T, Trie[T], String])(trie: Trie[T]): Trie[T] = {
    this.synchronized {
      val node = trie.root
      if (node.count != 0) {
        interacte(node, "", processor)
      }
      this
    }
  }

  private def interacte(node: Node[T], profix: String, processor: NodeProcessor[T, Trie[T], String]): Unit = {
    node.childMap.values.foreach(nd => interacte(nd, profix + nd.content, processor))
    if (node.isEnd) {
      this.value(profix) match {
        case Some(t) => processor.process(profix)(node.t, this)
        case None => this.insert(profix, node.t)
      }
    }
  }

  def ergodic(trieErgodicProcessor: TrieErgodicProcessor[String, T]): Unit =
    this.synchronized {
      val node = this.root
      if (node.count != 0) {
        innerErgodic(node, "", trieErgodicProcessor)
      }
    }

  private def innerErgodic(node: Node[T], profix: String, trieErgodicProcessor: TrieErgodicProcessor[String, T]): Unit = {
    node.childMap.values.foreach(nd => innerErgodic(nd, profix + nd.content, trieErgodicProcessor))
    if (node.isEnd) {
      trieErgodicProcessor.process(profix, node.t)
    }
  }

}
