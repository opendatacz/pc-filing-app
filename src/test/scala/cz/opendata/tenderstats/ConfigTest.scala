package cz.opendata.tenderstats

import org.junit._
import org.junit.Assert._

class ConfigTest {

  @Test
  def testComparerConfigFiles: Unit = {
    List(
      "CPVComparer.xml",
      "GeoDistanceComparer.xml",
      "PublicationDateComparer.xml",
      "TenderDeadlineComparer.xml",
      "TextComparer.xml") foreach (strFile =>
        assertNotNull((s"Config file $strFile does not exist", getClass().getResource(s"config/$strFile"))))
  }

}