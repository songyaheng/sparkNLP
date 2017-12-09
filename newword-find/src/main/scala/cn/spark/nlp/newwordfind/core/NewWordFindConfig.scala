package cn.spark.nlp.newwordfind.core

/**
  *
  * @author songyaheng on 2017/12/7
  * @version 1.0
  */
case class NewWordFindConfig(
                              minlen: Int,
                              maxlen: Int,
                              minCount: Int,
                              minInfoEnergy: Double,
                              minPmi: Double,
                              numPartitions: Int
                            )

object NewWordFindConfig {
  val minlen: Int = 2
  val maxlen: Int = 4
  val minCount: Int = 5
  val minInfoEnergy: Double = 1.0
  val minPmi: Double = 1.0
  val numPartitions: Int = 6
}
