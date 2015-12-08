import java.util.StringTokenizer

import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}

/**
  * Created by mengchending on 12/2/15.
  */
object test {

  def split(text:RDD[String]):RDD[(String,String)]={

    val content=text map (f=>{
      f.split(",")
    })
    content map(
      c=>{
       val id= c(1).replace("\""," ").trim
       val tweet=c(5).replace("\""," ").trim
        (id,tweet)
      }
      )






  }



  def main(args: Array[String]): Unit = {



  }



}
