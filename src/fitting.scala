import java.io.File 
import scalismo.io.MeshIO
import scalismo.ui.api.ScalismoUI
import scalismo.geometry._
import scalismo.mesh.TriangleMesh3D
import scalismo.common.PointId
import scalismo.mesh.TriangleMesh
import scalismo.statisticalmodel.GaussianProcess3D
import scalismo.kernels.DiagonalKernel3D
import scalismo.kernels.GaussianKernel3D
import scalismo.utils.Random.FixedSeed.randBasis
import scalismo.common.interpolation.TriangleMeshInterpolator3D
import scalismo.common.interpolation.NearestNeighborInterpolator3D
import scalismo.statisticalmodel.LowRankGaussianProcess
import scalismo.io.statisticalmodel.StatismoIO
import scalismo.statisticalmodel.PointDistributionModel3D
import scalismo.kernels.GaussianKernel
import scalismo.kernels.PDKernel
import scalismo.kernels.MatrixValuedPDKernel
import scalismo.common.EuclideanSpace
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.io.StatisticalModelIO
import scalismo.io.LandmarkIO

@main def kernels() = 
    println(s"Scalismo version: ${scalismo.BuildInfo.version}")

    val dataDir = new File("data/vertebrae/")
    val gpmmFile = new File(dataDir, "gpmm.h5.json")
    val lmsFile = new File(dataDir, "ref_20.json")
    val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).get
    val lms = LandmarkIO.readLandmarksJson[_3D](lmsFile).get

    val targetMesh = MeshIO.readMesh(new File(dataDir, "aligned/sub-verse010_segment_20.ply")).get
    val targetLms = LandmarkIO.readLandmarksJson[_3D](new File(dataDir, "aligned/sub-verse010_segment_20.json")).get

    val lmsData = lms.zip(targetLms).map{ case(lm1, lm2) => (gpmm.reference.pointSet.findClosestPoint(lm1.point).id, lm2.point)}.toIndexedSeq
    val lmPosterior = gpmm.posterior(lmsData, 1.0)
    val lmFit = lmPosterior.mean

    val ui = ScalismoUI()
    val modelGroup = ui.createGroup("modelGroup")
    val targetGroup = ui.createGroup("targetGroup")
    ui.show(modelGroup, gpmm, "gpmm")
    ui.show(modelGroup, lms, "landmarks")
    ui.show(targetGroup, targetMesh, "target")
    ui.show(targetGroup, lmFit, "lmFit")
    ui.show(targetGroup, targetLms, "landmarks")