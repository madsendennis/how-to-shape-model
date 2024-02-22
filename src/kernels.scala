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

case class xMirroredKernel(kernel: PDKernel[_3D]) extends PDKernel[_3D]:
  override def domain = kernel.domain
  override def k(x: Point[_3D], y: Point[_3D]) = kernel(Point(x(0) * -1.0, x(1), x(2)), y)

def symmetrizeKernel(kernel: PDKernel[_3D]): MatrixValuedPDKernel[_3D] =
  val xmirrored = xMirroredKernel(kernel)
  val k1 = DiagonalKernel3D(kernel, 3)
  val k2 = DiagonalKernel3D(xmirrored * -1f, xmirrored, xmirrored)
  k1 + k2

case class ChangePointKernel(kernel1: MatrixValuedPDKernel[_3D], kernel2: MatrixValuedPDKernel[_3D])
    extends MatrixValuedPDKernel[_3D]():
  override def domain = EuclideanSpace[_3D]
  val outputDim = 3
  def s(p: Point[_3D]) = 1.0 / (1.0 + math.exp(-p(0)))
  def k(x: Point[_3D], y: Point[_3D]) =
    val sx = s(x)
    val sy = s(y)
    kernel1(x, y) * sx * sy + kernel2(x, y) * (1 - sx) * (1 - sy)

def getMyKernel() =
  // ChangePointKernel(
  //     DiagonalKernel3D(GaussianKernel3D(100, 10), 3),
  //     DiagonalKernel3D(GaussianKernel3D(1, 0), 3)
  //     )
  symmetrizeKernel(GaussianKernel3D(100, 10))

@main def kernels() =
  println(s"Scalismo version: ${scalismo.BuildInfo.version}")

  val kernel = getMyKernel()

  val gp = GaussianProcess3D[EuclideanVector[_3D]](kernel)

  val ref = MeshIO.readMesh(new File("data/face/ref.ply")).get

  val lowRankGP = LowRankGaussianProcess.approximateGPCholesky(
    ref,
    gp,
    relativeTolerance = 0.1,
    interpolator = NearestNeighborInterpolator3D()
  )
  val pdm = PointDistributionModel3D(ref, lowRankGP)
  val sampleFromPdm: TriangleMesh[_3D] = pdm.sample()

  val augmentedPDM = PointDistributionModel.augmentModel(pdm, lowRankGP)

  val ui = ScalismoUI()
  ui.show(pdm, "pdm")
  // ui.show(ref, "ref")
  // ui.show(sample, "sample")
