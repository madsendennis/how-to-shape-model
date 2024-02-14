//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:1.0.0"

import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.StatisticalModelIO
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.statisticalmodel.dataset.DataCollection
import scalismo.utils.Random.implicits._

@main def buildSSM() = 
    println("Build Statistical shape model / GPMM with a PCA kernel")
    val dataFolder = new File("data/vertebrae/")
    val ref = MeshIO.readMesh(new File(dataFolder, "ref_20.ply")).get
    val registeredDataFolder = new File(dataFolder, "registered")
    val registeredMeshes = registeredDataFolder.listFiles().filter(_.getName.endsWith(".ply")).map(MeshIO.readMesh(_).get).toIndexedSeq
    val ssmFile = new File(dataFolder, "pca.h5.json")
    
    val dataCollection = DataCollection.fromTriangleMesh3DSequence(ref, registeredMeshes)
    val ssm = PointDistributionModel.createUsingPCA(dataCollection)
    StatisticalModelIO.writeStatisticalTriangleMeshModel3D(ssm, ssmFile)

    val ui = ScalismoUI()
    ui.show(ssm, "ssm")