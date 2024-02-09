//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:1.0.0"

import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.LandmarkIO
import scalismo.geometry._3D
import scalismo.registration.LandmarkRegistration
import scalismo.geometry.Point3D

@main def alignMeshes() = 
    val ref = MeshIO.readMesh(new File("data/ref_20.ply")).get
    val lms = LandmarkIO.readLandmarksJson[_3D](new File("data/ref_20.json")).get
    
    val rawDataFolder = new File("data/raw/")
    val alignedDataFolder = new File("data/aligned/")
    alignedDataFolder.mkdirs()

    rawDataFolder.listFiles().filter(_.getName.endsWith(".ply")).foreach { f => 
        val mesh = MeshIO.readMesh(f).get
        val jsonFile = new File(f.getAbsolutePath.replace(".ply", ".json"))
        val landmarks = LandmarkIO.readLandmarksJson[_3D](jsonFile).get
        val transform = LandmarkRegistration.rigid3DLandmarkRegistration(landmarks, lms, Point3D(0,0,0))
        val alignedMesh = mesh.transform(transform)
        val alignedLms = landmarks.map(lm => lm.copy(point = transform(lm.point)))
        val alignedMeshFile = new File(alignedDataFolder, f.getName)
        MeshIO.writeMesh(alignedMesh, alignedMeshFile)
        LandmarkIO.writeLandmarksJson[_3D](alignedLms, new File(alignedDataFolder, f.getName.replace(".ply", ".json")))
    }