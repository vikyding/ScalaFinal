import controllers.{Clustering, TfIdf, LoadData}
import org.scalatest.{FunSuite, Matchers}
/*
 * @author yaoyuanzhi
 */

class clusterTest extends FunSuite with Matchers {
  
    val textFile = Clustering.ReadFile("/Users/mengchending/Desktop/testTweet")

    val vocabulary = TfIdf.getVocabulary(textFile)

    val dictionary = TfIdf.getDictionary(vocabulary)


    test("calculateTF") {
      
      val tups= TfIdf.getTups(textFile)
      
      TfIdf.calculateTF(tups).count() shouldBe  7
      
    }
    
//    "calculateTF" should "return tups.reduceByKey(_ + _)" in{
//      val tups = TfIdf.getTups(textFile)
//      TfIdf.calculateTf(tups)
//    }
   
   
    test("calculateIDF"){
     
      val tups=TfIdf.getTups(textFile)

      val numOfFile= TfIdf.getNumOfFile(textFile)

      TfIdf.calculateIDF(tups,numOfFile).size  shouldBe  7
     
    }
    
    test("calculateTFIDF"){
     
      val vocabulary = TfIdf.getVocabulary(textFile)

      TfIdf.calculateTFIDF(vocabulary, TfIdf.calculateTF(TfIdf.getTups(textFile))).count()  shouldBe  7
 
    }
    
}