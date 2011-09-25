package dispatch.html5
import dispatch._

import org.xml.sax.InputSource

import scala.xml._
import parsing._

object Html5 {
  /** Add HTML5-processing method <#> to dispatch.HandlerVerbs */
  implicit def handlerToHtml5Verbs(r: HandlerVerbs) =
    new Html5HandlerVerbs(r)
  implicit def requestToHtml5Verbs(r: Request) =
    new Html5HandlerVerbs(r)
  implicit def stringToHtml5Verbs(str: String) =
    new Html5HandlerVerbs(new Request(str))
  implicit def callbackToHtml5Verbs(r: Request) =
    new Html5CallbackVerbs(r)
}

class Html5Parser extends NoBindingFactoryAdapter {

  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource) = {
    import nu.validator.htmlparser.{sax,common}
    import sax.HtmlParser
    import common.XmlViolationPolicy

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }
}

class Html5HandlerVerbs(subject: HandlerVerbs) {
  /** Process response as NodeSeq in block */
  def <#> [T](block: NodeSeq => T) = subject >> { (stm, charset) => 
    block((new Html5Parser).load(new java.io.InputStreamReader(stm, charset)))
  }
}

class Html5CallbackVerbs(subject: CallbackVerbs) {
  def ^#> [T](block: NodeSeq => T) =
    subject ^-- { s => block((new Html5Parser).loadString(s)) }
}
