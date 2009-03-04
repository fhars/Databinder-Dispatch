package net.databinder.dispatch.times

trait Times extends Js {
  lazy val http = new Http("api.nytimes.com")
  val api_key: String
  val service: String
  val version: Int
  
  def apply(action: String, params: Map[String, Any]) = http(
    ("/svc" :: service :: "v" + version :: action :: Nil).mkString("/")
  ) ?< (params + ("api-key" -> api_key))

  def apply(action: String): Http#Request = this(action, Map[String, Any]())

  val results: Js#M => List[Js#M] = js => ('results ! list)(None)(js) map obj
}

case class People(api_key: String) extends Times {
  val service = "timespeople/api";
  val version = 1
  
  def profile(user_id: Int) = this("/user/" + user_id + "/profile.js")
}

case class Search(api_key: String) extends Times {
  val service = "search"
  val version = 1
  
  def search(query: String) = this("/article", Map("query" -> query))
}

case class Community(api_key: String) extends Times {
  val service = "community"
  val version = 2

  override val results: Js#M => List[Js#M] = { js => ('comments ! list)(None)(('results ! obj)(None)(js)) map obj }
  
  def recent = this("comments/recent.json") $ results
}


case class News(api_key: String) extends Times {
  val service = "news"
  val version = 2
  
  def recent = this("all/recent.json") $ results
}