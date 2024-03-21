import scalismo.common.interpolation.NearestNeighborInterpolator3D
import scalismo.geometry.EuclideanVector
import scalismo.geometry._3D
import scalismo.io.MeshIO
import scalismo.io.StatisticalModelIO
import scalismo.kernels.DiagonalKernel3D
import scalismo.kernels.GaussianKernel3D
import scalismo.mesh.TriangleMesh
import scalismo.statisticalmodel.GaussianProcess3D
import scalismo.statisticalmodel.LowRankGaussianProcess
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.statisticalmodel.PointDistributionModel3D
import scalismo.ui.api.ScalismoUI

import java.io.File

def createModel(
    ref: TriangleMesh[_3D]
): PointDistributionModel[_3D, TriangleMesh] =
  val scaling = 10.0
  val sigma = 35.0
  val relativeTolerance = 0.01

  val kernel = GaussianKernel3D(sigma, scaling)
  val gp = GaussianProcess3D[EuclideanVector[_3D]](DiagonalKernel3D(kernel, 3))
  val lrGP = LowRankGaussianProcess.approximateGPCholesky(
    ref,
    gp,
    relativeTolerance,
    interpolator = NearestNeighborInterpolator3D()
  )
  val gpmm = PointDistributionModel3D(ref, lrGP)
  gpmm

@main def createGPMM() =
  println("Create deformable model for non-rigid registration")
  val dataDir = new File("data/vertebrae/")
  val ref = MeshIO.readMesh(new File(dataDir, "ref_20.ply")).get
  val gpmmFile = new File(dataDir, "gpmm.h5.json")

  val gpmm = StatisticalModelIO
    .readStatisticalTriangleMeshModel3D(gpmmFile)
    .getOrElse({
      println("Model not found, creating new one")
      val tmp = createModel(ref)
      StatisticalModelIO.writeStatisticalTriangleMeshModel3D(tmp, gpmmFile)
      tmp
    })

  println(s"Model rank: ${gpmm.rank}")

  val ui = ScalismoUI()
  ui.show(gpmm, "gpmm")
