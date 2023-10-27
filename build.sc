import mill._, scalalib._


object EchoServer extends ScalaModule {
  def scalaVersion = "3.3.0"

  def ivyDeps = Agg(
    ivy"dev.zio::zio-http:3.0.0-RC2",
    ivy"dev.zio::zio-json:0.6.1"
  )
}
