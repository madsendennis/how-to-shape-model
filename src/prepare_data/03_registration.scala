import gingr.api.RigidTransforms
import gingr.api.registration.config.CpdConfiguration
import gingr.simple.GingrInterface
import scalismo.geometry._3D
import scalismo.io.LandmarkIO
import scalismo.io.MeshIO
import scalismo.io.StatisticalModelIO
import scalismo.ui.api.ScalismoUI
import scalismo.utils.Random.implicits._

import java.io.File

@main def registerMeshes() =
  println("register meshes / establish point-to-point correspondences")
  val dataDir = new File("data/vertebrae/")
  val gpmmFile = new File(dataDir, "gpmm.h5.json")
  val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).get
  val lms =
    LandmarkIO.readLandmarksJson[_3D](new File(dataDir, "ref_20.json")).get

  val alignedDataFolder = new File(dataDir, "aligned")
  val registeredDataFolder = new File(dataDir, "registered")
  registeredDataFolder.mkdirs()

  alignedDataFolder
    .listFiles()
    .filter(_.getName.endsWith(".ply"))
    .sorted
    .foreach { f =>
      println(" - registering " + f.getName)
      val target = MeshIO.readMesh(f).get
      val jsonFile = new File(f.getAbsolutePath.replace(".ply", ".json"))
      val landmarks = LandmarkIO.readLandmarksJson[_3D](jsonFile).get

      val gingrInterface = GingrInterface(
        gpmm,
        target,
        modelLandmarks = Option(lms),
        targetLandmarks = Option(landmarks)
      )
      val configCPD = CpdConfiguration(maxIterations = 200)
      val cpd = gingrInterface.CPD(configCPD)

      val fit = cpd.runDecimated(
        modelPoints = 500,
        targetPoints = 500,
        globalTransformation = RigidTransforms
      )
      fit.general.printStatus()

      MeshIO.writeMesh(
        fit.general.fit,
        new File(registeredDataFolder, f.getName)
      )
      // Optionally, visualize the results
      // val ui = ScalismoUI("MultiResolution")
      // ui.show(target, "target")
      // ui.show(gpmm.mean, "model mean")
      // ui.show(fit.general.fit, "fit")
    }
