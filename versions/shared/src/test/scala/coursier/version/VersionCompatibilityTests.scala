package coursier.version

import utest._

object VersionCompatibilityTests extends TestSuite {

  def compatible(wanted: String, selected: String)(implicit compat: VersionCompatibility): Unit = {
    val compatible = compat.isCompatible(wanted, selected)
    assert(compatible)
  }
  def incompatible(wanted: String, selected: String)(implicit compat: VersionCompatibility): Unit = {
    val compatible = compat.isCompatible(wanted, selected)
    assert(!compatible)
  }

  val tests = Tests {
    "semver" - {

      implicit val compat = VersionCompatibility.SemVer

      test - compatible("1.1.0", "1.2.3")
      test - compatible("1.1.0", "1.2.3-RC1")
      test - incompatible("1.2.3-RC1", "1.2.3-RC2")

      test - compatible("0.1.1", "0.1.2")
      test - incompatible("0.1.1", "0.2.2")
    }

    "semverspec" - {

      implicit val compat = VersionCompatibility.SemVerSpec

      test - compatible("1.1.0", "1.2.3")
      test - compatible("1.1.0", "1.2.3-RC1")
      test - incompatible("1.2.3-RC1", "1.2.3-RC2")

      test - incompatible("0.1.1", "0.1.2")
      test - incompatible("0.1.1", "0.2.2")
    }

    "package versioning" - {

      implicit val compat = VersionCompatibility.PackVer

      test - incompatible("1.1.0", "1.2.3")
      test - incompatible("1.1.0", "1.2.3-RC1")
      test - compatible("0.1.0", "0.1.0+foo")
    }

    "all" - {

      val compatibilities = Seq(
        VersionCompatibility.SemVer,
        VersionCompatibility.SemVerSpec,
        VersionCompatibility.PackVer,
        VersionCompatibility.Strict
      )

      def compatible(wanted: String, selected: String): Unit =
        for (compat <- compatibilities) {
          val compatible = compat.isCompatible(wanted, selected)
          Predef.assert(compatible, s"Expected '$selected' to be compatible with '$wanted' per $compat")
        }
      def incompatible(wanted: String, selected: String): Unit =
        for (compat <- compatibilities) {
          val compatible = compat.isCompatible(wanted, selected)
          Predef.assert(!compatible, s"Expected '$selected' not to be compatible with '$wanted' per $compat")
        }

      test - incompatible("1.1+", "1.2.3")
      test - compatible("[1.1,1.3)", "1.2.3")
      test - incompatible("[1.1,1.2)", "1.2.3")
    }
  }

}
