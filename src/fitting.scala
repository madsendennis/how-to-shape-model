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
import scalismo.kernels.GaussianKernel
import scalismo.kernels.PDKernel
import scalismo.kernels.MatrixValuedPDKernel
import scalismo.common.EuclideanSpace
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.io.StatisticalModelIO
import scalismo.io.LandmarkIO
import scalismo.mesh.MeshMetrics

def nonrigidICP(
    model: PointDistributionModel[_3D, TriangleMesh],
    targetMesh: TriangleMesh3D,
    numOfSamplePoints: Int,
    numOfIterations: Int
): TriangleMesh3D =
  val numOfPoints = model.reference.pointSet.numberOfPoints
  val ptIds = (0 until numOfPoints by (numOfPoints / numOfIterations)).map(i => PointId(i))

  def attributeCorrespondences(
      movingMesh: TriangleMesh3D
  ): IndexedSeq[(PointId, Point[_3D])] =
    ptIds.map((id: PointId) =>
      val pt = movingMesh.pointSet.point(id)
      val closestPointOnMesh2 = targetMesh.pointSet.findClosestPoint(pt).point
      (id, closestPointOnMesh2)
    )

  def fitting(
      movingMesh: TriangleMesh3D,
      iteration: Int,
      uncertainty: Double
  ): TriangleMesh3D =
    println(s"iteration: $iteration")
    if (iteration == 0) then movingMesh
    else
      val correspondences = attributeCorrespondences(movingMesh)
      val posterior = model.posterior(correspondences, uncertainty)
      posterior.mean
      fitting(posterior.mean, iteration - 1, uncertainty)

  fitting(model.reference, numOfIterations, 1.0)

def evaluate(
    mesh1: TriangleMesh3D,
    mesh2: TriangleMesh3D,
    description: String
): Unit =
  val avg1 = MeshMetrics.avgDistance(mesh1, mesh2)
  val avg2 = MeshMetrics.avgDistance(mesh2, mesh1)
  val hausdorff1 = MeshMetrics.hausdorffDistance(mesh1, mesh2)
  val hausdorff2 = MeshMetrics.hausdorffDistance(mesh2, mesh1)
  println(
    s"$description - avg1: $avg1, avg2: $avg2, hausdorff1: $hausdorff1, hausdorff2: $hausdorff2"
  )

@main def kernels() =
  println(s"Scalismo version: ${scalismo.BuildInfo.version}")

  val dataDir = new File("data/vertebrae/")
  val gpmmFile = new File(dataDir, "gpmm.h5.json")
  val lmsFile = new File(dataDir, "ref_20.json")
  val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).get
  val lms = LandmarkIO.readLandmarksJson[_3D](lmsFile).get

  val targetMesh = MeshIO
    .readMesh(new File(dataDir, "aligned/sub-verse010_segment_20.ply"))
    .get
  val targetLms = LandmarkIO
    .readLandmarksJson[_3D](
      new File(dataDir, "aligned/sub-verse010_segment_20.json")
    )
    .get

  val lmsData = lms
    .zip(targetLms)
    .map { case (lm1, lm2) =>
      (gpmm.reference.pointSet.findClosestPoint(lm1.point).id, lm2.point)
    }
    .toIndexedSeq
  val lmPosterior = gpmm.posterior(lmsData, 1.0)
  val lmFit = lmPosterior.mean

  val icpFit = nonrigidICP(lmPosterior, targetMesh, 100, 50)

  evaluate(targetMesh, lmFit, "lmFit")
  evaluate(targetMesh, icpFit, "icpFit")

  val ui = ScalismoUI()
  val modelGroup = ui.createGroup("modelGroup")
  val targetGroup = ui.createGroup("targetGroup")
  ui.show(modelGroup, gpmm, "gpmm")
  // ui.show(modelGroup, lms, "landmarks")
  ui.show(targetGroup, targetMesh, "target")
  ui.show(targetGroup, lmFit, "lmFit")
  ui.show(targetGroup, icpFit, "icpFit")
  // ui.show(targetGroup, targetLms, "landmarks")
