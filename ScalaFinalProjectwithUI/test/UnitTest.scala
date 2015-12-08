/**
  * Created by mengchending on 12/7/15.
  */
import org.scalatest.{FlatSpec, Matchers, Inside}
import controllers.Twitter

class UnitTest extends FlatSpec with Inside with Matchers{

  "OR" should "return ((?iu)[:=]|[;]) for "+"(?iu)[:=]"+" , "+"[;]" in {
    val x = "(?iu)[:=]"
    val y = "[;]"
    Twitter.Tokenizer.OR(x,y) shouldBe ("((?iu)[:=]|[;])")
  }

  "allowEntities" should  "return ((<|&lt;)+/?3+) for "+"(<+/?3+)" in {
    val x="(<+/?3+)"
    Twitter.Tokenizer.allowEntities(x) shouldBe ("((<|&lt;)+/?3+)")
  }

  it should "return ((<|&lt;)*[-=]*(>|&gt;)+|(<|&lt;)+[-=]*(>|&gt;)*) for "+"(<*[-=]*>+|<+[-=]*>*)" in {
    val x="(<*[-=]*>+|<+[-=]*>*)"
    Twitter.Tokenizer.allowEntities(x) shouldBe ("((<|&lt;)*[-=]*(>|&gt;)+|(<|&lt;)+[-=]*(>|&gt;)*)")
  }

  "splitEdgePunct" should "return ;([\" hello \"]); for"+" ;([\"+'\"'+\"hello\"+'\"'+\"]);" in{
    val x= ";(["+'"'+"hello"+'"'+"]);"
    Twitter.Tokenizer.splitEdgePunct(x) shouldBe(";([\" hello \"]);")
  }

  "squeezeWhitespace" should "return it is a nice day" in {
    val x= "it  is a    nice day"
    Twitter.Tokenizer.squeezeWhitespace(x) shouldBe("it is a nice day")
  }

  "simpleTokenize" should "return List(it,is,a,nice,day)" in {
    val x="RT @bOBLOL : it is a nice day :) http://io/p/123"
    Twitter.Tokenizer.simpleTokenize(x) shouldBe(List("it","is","a","nice","day"))
  }

  "spliToken" should "return you're good" in {
    val x="you're good"
    Twitter.Tokenizer.splitToken(x) shouldBe(List("you're good"))
  }

  "normalizeText" should "return <it is @>" in {
    val x="&lt;it is @>"
    Twitter.Tokenizer.normalizeText(x) shouldBe ("<it is @>")
  }

  "tokenizeToString" should "ser  " in {
    val x="@SelenaG_Forum: #INFO Selena's a dit Tour. (source @SeIGomezUK)"
    Twitter.Tokenizer.tokenizeToString(x) shouldBe("Selena s a dit Tour source")
  }

  "splitEdgePunct" should "return @SelenaG_Forum: #INFO Selena a dit Tour. ( source @SeIGomezUK )" in{
    val x="@SelenaG_Forum: #INFO Selena a dit Tour. (source @SeIGomezUK)"
    Twitter.Tokenizer.splitEdgePunct(x) shouldBe("@SelenaG_Forum: #INFO Selena a dit Tour. ( source @SeIGomezUK )")
  }

  "Remove" should "return Selena a dit Tour. ( source  )" in {
    val x="@SelenaG_Forum: #INFO Selena a dit Tour. ( source @SeIGomezUK )"
    Twitter.Tokenizer.Remove(x) shouldBe("  Selena a dit Tour. ( source )")
  }

  "simpleTokenize" should "return   " in {
    val x="Selena a dit Tour. ( source  )"
    Twitter.Tokenizer.simpleTokenize(x) shouldBe(Seq("Selena","a","dit","Tour",".","(","source",")"))
  }

  "tokenizerForTagger" should "return   " in {
    val x="Selena a dit Tour. ( source  )"
    Twitter.Tokenizer.tokenizeForTagger(x) shouldBe(Seq("Selena","a","dit","Tour","source"))
  }

  "eaualSymbol" should "return " in {
    val x=")"
    Twitter.Tokenizer.equalSymbol(x) shouldBe ("")
  }






  //"Name" should "parse Tom Brady" in {
  //  Name.parse("Tom Brady") should matchPattern { case Success(h) => }
  //}


}