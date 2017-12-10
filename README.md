# sparkNLP
基于spark的NLP应用

一 新词发现

一个完全不基于词典的分词

主要实现点：左邻右邻信息熵， 点间互信息， Ngram

==> 后续会考虑重合子串的处理

调用方式：
    
    import cn.spark.nlp.newwordfind._
    
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
          .setAppName("new-words-find")
          .setMaster("local[3]")
    
        val pattern = "[\u4E00-\u9FA5]+".r
        val stopwords = "[你|我|他|她|它]+"
    
        val minLen = 2
        val maxLen = 6
        val minCount = 20
        val minInfoEnergy = 2.0
        val minPim = 300.0
        val numPartition = 6
    
        val newWordFindConfig = NewWordFindConfig(minLen, maxLen,
          minCount, minInfoEnergy, minPim, numPartition)
    
        val sc = new SparkContext(conf)
        
        val lines =sc.textFile("/Users/songyaheng/Downloads/西游记.txt")
          .flatMap(pattern.findAllIn(_).toSeq)
          .flatMap(_.split(stopwords))
          .newWord(sc, newWordFindConfig)
          .map(wepf => (wepf._2 * wepf._3 * wepf._4, wepf._1))
          .sortByKey(false, 1)
          .foreach(println)
    
        sc.stop()
      }
      
结果如下：

        (18168.514250954064,袈裟)
        (17713.06665111492,芭蕉)
        (16798.82717604416,吩咐)
        (16142.526118040218,葫芦)
        (11702.377091478338,乾坤)
        (11628.283999511725,哪吒)
        (11457.444521822847,猢狲)
        (10285.647417662267,琉璃)
        (7756.876983908059,荆棘)
        (7340.1686818898015,包袱)
        (7250.92797731874,校尉)
        (6876.8133704999855,钵盂)
        (6141.238795255687,揭谛)
        (6097.826306360437,惫懒)
        (4567.736774433127,苍蝇)
        (4398.391082713808,弼马温)
        (4208.842378551715,抖擞)
        (3865.6764241340757,孽畜)
        (3806.4273334808236,驿丞)
        (3369.570454781614,夯货)
        (3209.808428480634,悚惧)
        (3104.343061153103,祭赛)
        (3051.358367810302,武艺)
        (2996.755537579268,丑陋)
        (2821.9446891721645,怠慢)
        (2789.486615314228,蟠桃)
        (2706.7702230076206,逍遥)
        (2661.6587009929654,伺候)
        (2428.8996887030903,输赢)
        (2318.4894066182214,纷纷)
        (2314.1992786437513,奶奶)
        (2309.8451555988668,妈妈)
        (2021.7681532826207,尘埃)
        (1840.4474331072206,森森)
        (1768.7517043782416,伽蓝)
        (1673.1962179067696,悄悄)
        (1453.9759495186454,踪迹)
        (1338.3997377699582,杨柳)