package cn.spark.nlp.newwordfind.core

import org.apache.spark.Partitioner

/**
  *
  * @author songyaheng on 2017/12/8
  * @version 1.0
  */
class PrefixPartitioner(numPartition: Int) extends Partitioner{
  override def numPartitions = numPartition

  override def getPartition(key: Any): Int = {
    val prefix = key.toString.substring(0, 1)
    val code = prefix.hashCode % numPartition
    if (code < 0) {
      code + numPartition
    } else {
      code
    }
  }

  override def equals(other: Any): Boolean = other match {
    case prefixPartitioner: PrefixPartitioner =>
      prefixPartitioner.numPartitions == numPartitions
    case _ =>
      false
  }

  override def hashCode(): Int = numPartitions

}


