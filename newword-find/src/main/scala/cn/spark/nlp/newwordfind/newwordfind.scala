package cn.spark.nlp

import cn.spark.nlp.newwordfind.core.{NewWordFind, NewWordFindConfig}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
  *
  * @author songyaheng on 2017/12/7
  * @version 1.0
  */
package object newwordfind {

  implicit def sparkRDDFunctions(rdd: RDD[String]) = new SparkRDDFunctions[String](rdd)

  class SparkRDDFunctions[T : ClassTag](rdd: RDD[T]) extends Serializable {
    def newWord(sc: SparkContext): RDD[(String, Double, Double, Int)] =
      NewWordFind.newWordRDD(sc, rdd, NewWordFindConfig(NewWordFindConfig.minlen,
        NewWordFindConfig.maxlen,
        NewWordFindConfig.minCount,
        NewWordFindConfig.minInfoEnergy,
        NewWordFindConfig.minPmi,
        NewWordFindConfig.numPartitions))
    def newWord(sc: SparkContext, newWordFindConfig: NewWordFindConfig): RDD[(String, Double, Double, Int)] =
      NewWordFind.newWordRDD(sc, rdd, newWordFindConfig)

  }




}
