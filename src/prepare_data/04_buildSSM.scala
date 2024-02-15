import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.StatisticalModelIO
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.statisticalmodel.dataset.DataCollection
import scalismo.utils.Random.implicits._

@main def buildSSM() = 
    println("Build Statistical shape model / GPMM with a PCA kernel")
    val dataDir = new File("data/vertebrae/")
    val registeredDataFolder = new File(dataDir, "registered")
    val registeredMeshes = registeredDataFolder.listFiles().filter(_.getName.endsWith(".ply")).map(MeshIO.readMesh(_).get).toIndexedSeq
    val ref = MeshIO.readMesh(new File(dataDir, "ref_20.ply")).get
    val ssmFile = new File(dataDir, "pca.h5.json")

    val dataCollection = DataCollection.fromTriangleMesh3DSequence(ref, registeredMeshes)
    val ssm = PointDistributionModel.createUsingPCA(dataCollection)
    // StatisticalModelIO.writeStatisticalTriangleMeshModel3D(ssm, ssmFile)

    val ui = ScalismoUI()
    ui.show(ssm, "ssm")