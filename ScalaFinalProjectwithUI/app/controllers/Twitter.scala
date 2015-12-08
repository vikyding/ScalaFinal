package controllers

import org.apache.spark.streaming.dstream.DStream
import twitter4j.Status

import scala.util.matching.Regex

/**
  * Created by mengchending on 12/7/15.
  */
object Twitter {



  val stopWords:Array[String]=Array("able","about","above","according","accordingly","across","actually","after","afterwards","again","against","ain't","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything",
    "anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren't","around","as","a's","aside","ask","asking","associated","at","available","away","awfully","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being",
    "believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","came","can","cannot","cant","can't","cause","causes","certain","certainly","changes","clearly","c'mon","co","com","come","comes","concerning","consequently","consider","considering","contain","containing",
    "contains","corresponding","could","couldn't","course","c's","currently","definitely","described","despite","did","didn't","different","do","does","doesn't","doing","done","don't","down","downwards","during","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even",
    "ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","get","gets","getting","given","gives","go","goes","going","gone","got","gotten",
    "greetings","had","hadn't","happens","hardly","has","hasn't","have","haven't","having","he","hello","help","hence","her","here","hereafter","hereby","herein","here's","hereupon","hers","herself","he's","hi","him","himself","his","hither","hopefully","how","howbeit","however","i'd","ie","if","ignored","i'll","i'm","immediate",
    "in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isn't","it","it'd","it'll","its","it's","itself","i've","just","keep","keeps","kept","know","known","knows","last","lately","later","latter","latterly","least","less","lest","let","let's","like","liked","likely",
    "little","look","looking","looks","ltd","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor",
    "normally","not","nothing","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","particular","particularly","per","perhaps","placed","please","plus","possible",
    "presumably","probably","provides","que","quite","qv","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious",
    "seriously","seven","several","shall","she","should","shouldn't","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","take","taken","tell","tends","th","than","thank","thanks","thanx",
    "that","thats","that's","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","there's","thereupon","these","they","they'd","they'll","they're","they've","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus",
    "to","together","too","took","toward","towards","tried","tries","truly","try","trying","t's","twice","two","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","value","various","very","via","viz","vs","want","wants","was","wasn't","way","we","we'd","welcome",
    "well","we'll","went","were","we're","weren't","we've","what","whatever","what's","when","whence","whenever","where","whereafter","whereas","whereby","wherein","where's","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","who's","whose","why","will","willing","wish","with","within","without",
    "wonder","won't","would","wouldn't","yes","yet","you","you'd","you'll","your","you're","yours","yourself","yourselves","you've","zero","zt","ZT","zz","ZZ")


  object Parser {


    def Parse(tweetstream:DStream[Status]):DStream[(Long,String)] ={

      for(
        aa <-tweetstream

      ) yield (aa.getId,Tokenizer.tokenizeToString(aa.getText.toLowerCase))

    }
  }

  object Tokenizer {

    val Contractions = """(?i)(\w+)(n't|'ve|'ll|'d|'re|'s|'m)$""".r
    val Whitespace = """\s+""".r

    val punctChars = """['“\".?!,:;]"""
    val punctSeq = punctChars + """+"""
    val entity = """&(amp|lt|gt|quot);"""

    // URLs
    val urlStart1 = """(https?://|www\.)"""
    val commonTLDs = """(com|co\.uk|org|net|info|ca|ly|mp|edu|gov)"""
    val urlStart2 = """[A-Za-z0-9\.-]+?\.""" + commonTLDs + """(?=[/ \W])"""
    val urlBody = """[^ \t\r\n<>]*?"""
    val urlExtraCrapBeforeEnd = "(" + punctChars + "|" + entity + ")+?"
    val urlEnd = """(\.\.+|[<>]|\s|$)"""
    val url = """\b(""" + urlStart1 + "|" + urlStart2 + ")" + urlBody + "(?=(" + urlExtraCrapBeforeEnd + ")?" + urlEnd + ")"

    // Numeric
    val timeLike = """\d+:\d+"""
    val numNum = """\d+\.\d+"""
    val numberWithCommas = """(\d+,)+?\d{3}""" + """(?=([^,]|$))"""
    val numberWithOpera= """\d+\\\d+"""

    // Abbreviations
    val boundaryNotDot = """($|\s|[“\"?!,:;]|""" + entity + ")"
    val aa1 = """([A-Za-z]\.){2,}(?=""" + boundaryNotDot + ")"
    val aa2 = """[^A-Za-z]([A-Za-z]\.){1,}[A-Za-z](?=""" + boundaryNotDot + ")"
    val standardAbbreviations = """\b([Mm]r|[Mm]rs|[Mm]s|[Dd]r|[Ss]r|[Jj]r|[Rr]ep|[Ss]en|[Ss]t)\."""
    val arbitraryAbbrev = "(" + aa1 + "|" + aa2 + "|" + standardAbbreviations + ")"

    val separators = "(--+|―)"
    val decorations = """[♫]+"""
    val thingsThatSplitWords = """[^\s\.,]"""
    val embeddedApostrophe = thingsThatSplitWords + """+'""" + thingsThatSplitWords + """+"""

    // Emoticons
    val normalEyes = "(?iu)[:=]"
    val wink = "[;]"
    val noseArea = "(|o|O|-|[^a-zA-Z0-9 ])"
    val happyMouths = """[D\)\]]+"""
    val sadMouths = """[\(\[]+"""
    val tongue = "[pP]"
    val otherMouths = """[doO/\\]+""" // remove forward slash if http://'s aren't cleaned

    // mouth repetition examples:
    // @aliciakeys Put it in a love song :-))
    // @hellocalyclops =))=))=)) Oh well

    def OR(parts: String*) = {
      "(" + parts.toList.mkString("|") + ")"
    }

    val emoticon = OR(
      // Standard version :) :( :] :D :P
      OR(normalEyes, wink) + noseArea + OR(tongue, otherMouths, sadMouths, happyMouths),

      // reversed version (: D: use positive lookbehind to remove "(word):"
      // because eyes on the right side is more ambiguous with the standard usage of : ;
      """(?<=( |^))""" + OR(sadMouths, happyMouths, otherMouths) + noseArea + OR(normalEyes, wink) // TODO japanese-style emoticons
      // TODO should try a big precompiled lexicon from Wikipedia, Dan Ramage told me (BTO) he does this
    )

    def allowEntities(pat: String) = {
      // so we can write patterns with < and > and let them match escaped html too
      pat.replace("<", "(<|&lt;)").replace(">", "(>|&gt;)")

    }

    val Hearts = allowEntities("""(<+/?3+)""")


    val Arrows = allowEntities("""(<*[-=]*>+|<+[-=]*>*)""")

    val Hashtag = """#[a-zA-Z0-9_]+""" // also gets #1 #40 which probably aren't hashtags .. but good as tokens

    val AtMention = """@[a-zA-Z0-9_]+\s*:?"""

    val Singnature ="""RT"""

    // I was worried this would conflict with at-mentions
    // but seems ok in sample of 5800: 7 changes all email fixes
    // http://www.regular-expressions.info/email.html
    val Bound = """(\W|^|$)"""
    val Email = "(?<=" + Bound + """)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}(?=""" + Bound + ")"

    // We will be tokenizing using these regexps as delimiters
    // Additionally, these things are "protected", meaning they shouldn't be further split themselves.
    val Protected = new Regex(
      OR(
        entity,
        punctSeq,
        arbitraryAbbrev,
        separators,
        decorations,
        embeddedApostrophe
      ))

    val Removed = new Regex(
      OR(
        timeLike,
        numNum,
        numberWithCommas,
        Hearts,
        Arrows,
        emoticon,
        url,
        Email,
        Hashtag,
        AtMention,
        Singnature
      )
    )

    // Edge punctuation
    // Want: 'foo' => ' foo '
    // While also: don't => don't
    // the first is considered "edge punctuation".
    // the second is word-internal punctuation -- don't want to mess with it.
    // BTO (2011-06): the edgepunct system seems to be the #1 source of problems these days.
    // I remember it causing lots of trouble in the past as well. Would be good to revisit or eliminate.

    // Note the 'smart quotes' (http://en.wikipedia.org/wiki/Smart_quotes)

    val edgePunctChars = """'"“”‘’«»{}\(\)\[\]\*"""
    val edgePunct = "[" + edgePunctChars + "]"
    val notEdgePunct = "[a-zA-Z0-9]" // content characters
    val offEdge = """(^|$|:|;|\s)""" // colon here gets "(hello):" ==> "( hello ):"
    val EdgePunctLeft = new Regex(offEdge + "(" + edgePunct + "+)(" + notEdgePunct + ")")
    val EdgePunctRight = new Regex("(" + notEdgePunct + ")(" + edgePunct + "+)" + offEdge)

    def splitEdgePunct(input: String) = {
      var s = input

      s = EdgePunctLeft.replaceAllIn(s, "$1$2 $3")

      s = EdgePunctRight.replaceAllIn(s, "$1 $2$3")
      s
    }

    //
    def Remove(text:String) ={
      Removed.replaceAllIn(text,"")
    }

    //
    val symbol="""[^a-zA-Z]"""
    val RSymbol=new Regex(symbol)

    def equalSymbol(text:String) = {
      RSymbol.replaceAllIn(text, "")
    }

    // The main work of tokenizing a tweet.
    def simpleTokenize(text: String) = {

      // Do the no-brainers first
      val splitPunctText = splitEdgePunct(text)

      val textLength = splitPunctText.length

      // Find the matches for subsequences that should be protected,
      // e.g. URLs, 1.0, U.N.K.L.E., 12:53
      val textafterremoved=Remove(splitPunctText)

      val matches = Protected.findAllIn(textafterremoved).matchData.toList


      // The spans of the "bads" should not be split.
      val badSpans = matches map (mat => Tuple2(mat.start, mat.end))

      // Create a list of indices to create the "goods", which can be
      // split. We are taking "bad" spans like
      // List((2,5), (8,10))
      // to create
      /// List(0, 2, 5, 8, 10, 12)
      // where, e.g., "12" here would be the textLength
      val indices = (0 :: badSpans.foldRight(List[Int]())((x, y) => x._1 :: x._2 :: y)) ::: List(textLength)
      // Group the indices and map them to their respective portion of the string
      val goods = indices.grouped(2).map { x => textafterremoved.slice(x(0), x(1)) }.toList


      //The 'good' strings are safe to be further tokenized by whitespace
      val splitGoods = goods map { str => str.trim.split(" ").toList }

      //Storing as List[List[String]] to make zip easier later on
      val bads = badSpans map { case (start, end) => List(textafterremoved.slice(start, end)) }

      // Reinterpolate the 'good' and 'bad' Lists, ensuring that
      // additonal tokens from last good item get included
      val zippedStr =
        (if (splitGoods.length == bads.length)
          splitGoods.zip(bads) map { pair => pair._1 ++ pair._2 }
        else
          (splitGoods.zip(bads) map { pair => pair._1 ++ pair._2 }) ::: List(splitGoods.last)).flatten

      // Split based on special patterns (like contractions) and check all tokens are non empty
      zippedStr.map(splitToken(_)).flatten.filter(_.length > 0)

    }

    // "foo bar" => "foo bar"
    def squeezeWhitespace(input: String) = Whitespace.replaceAllIn(input, " ").trim()

    // Final pass tokenization based on special patterns
    def splitToken(token: String) = {
      token match {
        // BTO: our POS tagger wants "ur" and "you're" to both be one token.
        // Uncomment to get "you 're"
        case Contractions(stem, contr) => List(stem.trim, contr.trim)
        case token => List(token.trim)
      }
    }
    // Apply method allows it to be used as Twokenize(line) in Scala.
    def apply(text: String): Seq[String] = simpleTokenize(squeezeWhitespace(text))

    // More normal name for @apply@
    def tokenize(text: String): Seq[String] = apply(text)

    // Very slight normalization for AFTER tokenization.
    // The tokenization regexes are written to work on non-normalized text.
    // (to make byte offsets easier to compute)
    // Hm: 2+ repeated character normalization here?
    // No, that's more linguistic, should be further down the pipeline
    def normalizeText(text: String) = {
      text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&")
    }

    def tokenizeForTagger(text: String): Seq[String] = {
      tokenize(text).map(normalizeText).map(equalSymbol).filter(_.length>0)
    }

    // def tokenizeForTagger_J(text: String): Seq[String] = {
    //   tokenizeForTagger(text).toSeq
    // }

    // Convenience method to produce a string representation of the
    // tokenized tweet in a standard-ish format.
    def tokenizeToString(text: String): String = {
      tokenizeForTagger(text).mkString(" ")
      //RemoveSymbol.replaceAllIn(words, " ")
    }



  }
}
