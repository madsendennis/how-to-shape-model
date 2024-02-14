//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::gingr:0.1-SNAPSHOT"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:1.0.0"

import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.LandmarkIO
import scalismo.geometry._3D
import scalismo.registration.LandmarkRegistration
import scalismo.geometry.Point3D
import scalismo.io.StatisticalModelIO
import gingr.simple.GingrInterface
import gingr.api.registration.config.CpdConfiguration
import scalismo.utils.Random.implicits._
import gingr.api.RigidTransforms
import gingr.api.registration.config.IcpConfiguration
import gingr.api.NoTransforms
import gingr.api.registration.config.AlongNormalClosestPoint

@main def registerMeshes() = 
    println("register meshes / establish point-to-point correspondences")
    val dataFolder = new File("data/vertebrae/")
    val gpmmFile = new File(dataFolder, "gpmm.h5.json")
    val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).get
    val lms = LandmarkIO.readLandmarksJson[_3D](new File(dataFolder, "ref_20.json")).get

    val alignedDataFolder = new File(dataFolder, "aligned")
    val registeredDataFolder = new File(dataFolder, "registered")
    registeredDataFolder.mkdirs()

    alignedDataFolder.listFiles().filter(_.getName.endsWith(".ply")).sorted.foreach { f => 
        println(" - registering " + f.getName)
        val target = MeshIO.readMesh(f).get
        val jsonFile = new File(f.getAbsolutePath.replace(".ply", ".json"))
        val landmarks = LandmarkIO.readLandmarksJson[_3D](jsonFile).get
    
        val ui = ScalismoUI("MultiResolution")
        ui.show(target, "target")
        ui.show(gpmm.mean, "model mean")

        val gingrInterface = GingrInterface(gpmm, target, modelLandmarks = Option(lms), targetLandmarks = Option(landmarks))
        val configCPD = CpdConfiguration(maxIterations = 200)
        val cpd = gingrInterface.CPD(configCPD)

        val fit = cpd.runDecimated(modelPoints = 500, targetPoints = 500, globalTransformation = RigidTransforms)
        fit.general.printStatus()
        ui.show(fit.general.fit, "fit")

        MeshIO.writeMesh(fit.general.fit, new File(registeredDataFolder, f.getName))
    }