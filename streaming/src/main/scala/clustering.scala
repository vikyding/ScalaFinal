
import java.io.FileWriter
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.util.StringTokenizer
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.clustering.KMeans
import java.io.File

/**
 * @author yaoyuanzhi
 */
object clustering{



  def getDataset(list:RDD[(Long,Array[(String,Long)])],IDFMap:Map[String, Double],numOfFeatures:Long,dictionary:Map[String,Long]):RDD[Vector]={
   list.flatMap(e => {
      val tfs = e._2
      //calculate the number of terms in each document
      var numTerm = 0L
      tfs.foreach(f => { numTerm = numTerm + f._2 })
      val vector = getVector(IDFMap, numOfFeatures, tfs, numTerm, dictionary, e._1)
      Array(vector)
    })


  }



  def getVector(IDFMap: Map[String, Double], numOfFeatures: Long, frequency: Array[(String, Long)], numTerms: Long, dictionary: Map[String, Long], Id: Long): Vector = {
    var indexArray = new Array[Int](0)
    var valueArray = new Array[Double](0)
    //      println("information for "+Id)
    frequency.foreach(
      f => {
        indexArray = indexArray ++ Array(dictionary(f._1).toInt)
        val idf = IDFMap(f._1)
        val tf = f._2.toDouble / numTerms.toDouble
        //      println("information of "+f._1+" tf"+tf+" idf"+idf)
        valueArray = valueArray ++ Array(tf * idf)
      })

    val result: Vector = Vectors.sparse(numOfFeatures.toInt, indexArray, valueArray)
    result
  }


  def main(args: Array[String]): Unit = {


      val conf = new SparkConf().setAppName("AppName").setMaster("local").set("master.clustering", "1g")
      System.setProperty("master.clustering", "1g")

      val sc = new SparkContext(conf)

      val stopWord= sc.textFile("/Users/mengchending/Desktop/stopwords.txt").filter(_.length()>1).collect()

      stopWord.foreach(aa=>print("\""+aa+"\","))

      val text= sc.textFile("/Users/mengchending/Desktop/result/tweets").filter(_.length>0)



     val textFile=TfIdf.delimite(text,stopWord)

    val tweets=TfIdf.getTweets(text)

    val vocabulary= TfIdf.getVocabulary(textFile)

    val dictionary= TfIdf.getDictionary(vocabulary)

    val tups=TfIdf.getTups(textFile)

    val numOfFile= TfIdf.getNumOfFile(textFile)

    val tf=TfIdf.calculateTF(tups)

    val idf=TfIdf.calculateIDF(tups,numOfFile)

    val tfidf=TfIdf.calculateTFIDF(vocabulary,tf)

    val numOfFeatures= TfIdf.getNumOfFeatures(vocabulary)

    val dataset=getDataset(tfidf,idf,numOfFeatures,dictionary)

    val clusters = KMeans.train(dataset,20,20)


    val WSSSE = clusters.computeCost(dataset)

    val clusterResult = tfidf.flatMap(e => {
      val id = e._1
      val tfs = e._2
      //calculate the number of terms in each document
      var numTerm = 0L
      tfs.foreach(f => { numTerm = numTerm + f._2 })
      val vector = getVector(idf, numOfFeatures, tfs, numTerm, dictionary, e._1)
      val cluster = clusters.predict(vector)
      Array((id, cluster))
    })


    val fw = new FileWriter(new File("/Users/mengchending/Desktop/answer/result"))

    val lines=textFile.flatMap(e => {

      val trip = new StringTokenizer(e, " ")

      val id = trip.nextToken()

      if (!trip.hasMoreTokens()) {
        var termList: Array[(Long, String)] = new Array[(Long, String)](0)
        termList
      } else {
        val str = trip.nextToken()

        var termList = Array((id.trim().toLong, str))

        termList
      }
    })

    //lines.foreach(aa=>println(aa+"!"))

  // val result = lines.join(clusterResult)

    val result =tweets.join(clusterResult)

    val find=result map( r => {
      val clusterid=r._2._2
      val tweetId=r._1
      ((clusterid,tweetId),1L)
    })

    val res=find.reduceByKey(_+_) map (
      rr=>
        {
          (rr._1._1,1L)
        }
      )

    val fres=res.reduceByKey(_+_)



    fres.collect.foreach(f => {

      fw.write(f._1.toString+" \tcluster"+" has "+f._2+ " tweets \n")
      fw.flush
    })
    fw.flush()
    println("Within Set Sum of Squared Errors = " + WSSSE)
  }

}