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
    val dataFolder = new File(dataDir, "registered")
    val meshes = dataFolder.listFiles().filter(_.getName.endsWith(".ply")).map(MeshIO.readMesh(_).get).toIndexedSeq
    meshes.foreach(_.pointSet.numberOfPoints)
    val ref = MeshIO.readMesh(new File(dataDir, "ref_20.ply")).get
    val ssmFile = new File(dataDir, "pca.h5.json")

    val dataCollection = DataCollection.fromTriangleMesh3DSequence(ref, meshes)
    val ssm = PointDistributionModel.createUsingPCA(dataCollection)
    StatisticalModelIO.writeStatisticalTriangleMeshModel3D(ssm, ssmFile)

    val ui = ScalismoUI()
    ui.show(ssm, "ssm")