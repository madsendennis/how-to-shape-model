import java.io.File
import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO

val dataDir = new File("data/vertebrae/")
val dataFolder = new File(dataDir, "registered")
val meshes = dataFolder.listFiles().filter(_.getName.endsWith(".ply")).map(MeshIO.readMesh(_).get).toIndexedSeq
val ref = meshes.head

val dataCollection = DataCollection.fromTriangleMesh3DSequence(ref, meshes)
val ssm = PointDistributionModel.createUsingPCA(dataCollection)
val ui = ScalismoUI()
ui.show(ssm, "ssm")