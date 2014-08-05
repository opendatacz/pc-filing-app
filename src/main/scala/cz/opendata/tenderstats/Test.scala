package cz.opendata.tenderstats

object Test {

  def main(args: Array[String]) {
    NumberOfBidders.sendPost("http://linked.opendata.cz/resource/pc-filing-app/public-contract/94d761c6-ef78-4a80-9fee-2036516b388c", "http://linked.opendata.cz/resource/pc-filing-app/dataset/contracting-authority/pro@palmovka.cz")
  }
}
