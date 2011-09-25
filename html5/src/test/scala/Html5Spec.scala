import org.specs._

object Html5Spec extends Specification {
  import dispatch._
  import dispatch.html5.Html5._

  val test = :/("technically.us") / "test.html"
  
  "HTML5 Parsing" should {
    "find title of a page" in {
      val http = new Http
      (http x (test <#> ( _ \\ "title"))).text must_== 
        "404 Not Found"
    }
  }
}
