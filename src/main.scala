import scalismo.ui.api.ScalismoUI
@main def main() =
  println(s"Scalismo version: ${scalismo.BuildInfo.version}")
  val ui = ScalismoUI()
