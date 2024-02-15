import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.LandmarkIO
import scalismo.geometry._3D
import scalismo.registration.LandmarkRegistration
import scalismo.geometry.Point3D

@main def alignMeshes() = 
    println("Aligning meshes and landmarks")
    val dataDir = new File("data/vertebrae/")
    val ref = MeshIO.readMesh(new File(dataDir, "ref_20.ply")).get
    val lms = LandmarkIO.readLandmarksJson[_3D](new File(dataDir, "ref_20.json")).get
    
    val rawDataFolder = new File(dataDir, "raw/")
    val alignedDataFolder = new File(dataDir, "/aligned/")
    alignedDataFolder.mkdirs()

    rawDataFolder.listFiles().filter(_.getName.endsWith(".ply")).foreach { f => 
        println(" - aligning " + f.getName)
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