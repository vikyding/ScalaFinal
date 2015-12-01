
import java.io.FileWriter
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.util.StringTokenizer
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import scala.math._
import org.apache.spark.mllib.clustering.KMeans
/**
 * @author yaoyuanzhi
 */
object clustering{

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("hello").setMaster("local").set("master.clustering", "1g")
    System.setProperty("master.clustering", "1g")
    val sc = new SparkContext(conf)

    println("test")
    val textFile = sc.textFile("/Users/mengchending/Desktop/result/re-1448966168000/part*")

    val numOfFile = textFile.count
    println("file count " + numOfFile)

    val vocabulary = textFile.flatMap(e => {

      val trip = new StringTokenizer(e, "\t")
      val id = trip.nextToken()

      if (!trip.hasMoreTokens()) {
        var termList: Array[(String, Long)] = new Array[(String, Long)](0)
        termList
      } else {
        val str = new StringTokenizer(trip.nextToken(), " ")

        var termList: Array[(String, Long)] = new Array[(String, Long)](0)
        while (str.hasMoreTokens) {
          val term = str.nextToken().trim()
          termList = termList ++ Array((term, 1L))

        }
        termList
      }
    }).distinct().keys.sortBy(identity)


    vocabulary.foreach(s => printf(s + "\n"))

    val dictionary = vocabulary.zipWithIndex.collectAsMap.toMap


    val lines = textFile.flatMap(e => {

      val trip = new StringTokenizer(e, "\t");
      val id = trip.nextToken()
      if (!trip.hasMoreTokens()) {
        var termList: Array[(Long, String)] = new Array[(Long, String)](0)
        termList
      } else {
        val str = trip.nextToken();

        var termList = Array((id.trim().toLong, str))

        termList
      }
    })



    lines.foreach(f => println(f + ""))

    val tups = lines.flatMap {
      case (id, line) =>
      {
        val str = new StringTokenizer(line, " ");
        var tuples: Array[((Long, String), Long)] = new Array[((Long, String), Long)](0)
        while (str.hasMoreTokens()) {
          val term = str.nextToken()
          tuples = tuples ++ Array(((id, term), 1L))
        }
        tuples
      }
    }


    tups.foreach(f => printf(f + "\n"))
    //collect TF
    val TF_count = tups.reduceByKey(_ + _)
    println("number of occurrence of each word in each document")
    TF_count.foreach(t => printf(t + "\n"))
    val pairs = TF_count.map { case (tuple, count) => (tuple._2, 1L) }
    //calculate IDF
    val IDF = pairs.reduceByKey(_ + _)
    println("number of documents a word appears in")
    IDF.foreach(f => println(f + "\n"))
    val IDFList = IDF.map {
      case (term, count) => {
        val idf = log10(numOfFile / (count.toDouble))
        (term, idf)
      }
    }

    val IDFMap = IDFList.collectAsMap.toMap

    //calculate the number of features
    val numOfFeatures = vocabulary.count
    //calculate TF-IDF
    val prepare = TF_count.map {
      case (tuple, count) => (tuple._1, Array((tuple._2, count)))
    }
    val list = prepare.reduceByKey((a, b) => a ++ b)
    list.foreach(f => {
      println(f._1 + " ")
      f._2.foreach(f => print(f))
      println("")
    })

    //feature construction
    val dataset = list.flatMap(e => {
      val tfs = e._2
      //calculate the number of terms in each document
      var numTerm = 0L
      tfs.foreach(f => { numTerm = numTerm + f._2 })
      val vector = getVector(IDFMap, numOfFeatures, tfs, numTerm, dictionary, e._1)
      Array(vector)
    })



    val clusters = KMeans.train(dataset, 3,6)

    val WSSSE = clusters.computeCost(dataset)

    val clusterResult = list.flatMap(e => {
      val id = e._1
      val tfs = e._2
      //calculate the number of terms in each document
      var numTerm = 0L
      tfs.foreach(f => { numTerm = numTerm + f._2 })
      val vector = getVector(IDFMap, numOfFeatures, tfs, numTerm, dictionary, e._1)
      val cluster = clusters.predict(vector)
      Array((id, cluster))
    })


    //clusterResult.foreach(f=>println(f))

    val fw = new FileWriter("/Users/mengchending/Desktop/answer/")




    val result = lines.join(clusterResult)
    result.collect.foreach(f => {
      fw.write(f._2._1 + "\tcluster" + f._2._2 + "\n")
      fw.flush
    })
    fw.flush()
    println("Within Set Sum of Squared Errors = " + WSSSE)
  }





  def getVector(IDFMap: Map[String, Double], numOfFeatures: Long, frequency: Array[(String, Long)], numTerms: Long, dictionary: Map[String, Long], Id: Long): Vector = {
    var indexArray = new Array[Int](0)
    var valueArray = new Array[Double](0)
    //      println("information for "+Id)
    frequency.foreach(f => {
      indexArray = indexArray ++ Array(dictionary(f._1).toInt)
      val idf = IDFMap(f._1)
      val tf = f._2.toDouble / numTerms.toDouble
      //      println("information of "+f._1+" tf"+tf+" idf"+idf)
      valueArray = valueArray ++ Array(tf * idf)
    })

    val result: Vector = Vectors.sparse(numOfFeatures.toInt, indexArray, valueArray)
    result
  }

}