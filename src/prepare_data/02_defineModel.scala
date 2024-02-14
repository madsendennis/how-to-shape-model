//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:1.0.0"

import scalismo.ui.api.ScalismoUI
import scalismo.io.MeshIO
import java.io.File
import scalismo.io.LandmarkIO
import scalismo.geometry._3D
import scalismo.registration.LandmarkRegistration
import scalismo.geometry.Point3D
import scalismo.statisticalmodel.PointDistributionModel3D
import scalismo.kernels.GaussianKernel
import scalismo.kernels.DiagonalKernel
import scalismo.mesh.TriangleMesh
import scalismo.statisticalmodel.LowRankGaussianProcess
import scalismo.common.interpolation.TriangleMeshInterpolator3D
import scalismo.statisticalmodel.GaussianProcess3D
import scalismo.geometry.EuclideanVector3D
import scalismo.common.Field
import scalismo.common.EuclideanSpace3D
import scalismo.geometry.Point
import scalismo.geometry.EuclideanVector
import scalismo.common.interpolation.NearestNeighborInterpolator3D
import scalismo.io.StatisticalModelIO
import scalismo.statisticalmodel.PointDistributionModel

def createModel(ref: TriangleMesh[_3D]): PointDistributionModel[_3D, TriangleMesh] =
    val scaling = 50.0
    val sigma = 35.0
    val relativeTolerance = 0.01

    val kernel = GaussianKernel[_3D](sigma) * scaling
    val gp = GaussianProcess3D[EuclideanVector[_3D]](DiagonalKernel(kernel, 3))
    val lrGP = LowRankGaussianProcess.approximateGPCholesky(ref, gp, relativeTolerance, interpolator = NearestNeighborInterpolator3D())
    val gpmm = PointDistributionModel3D(ref, lrGP)
    gpmm

@main def createGPMM() = 
    println("Create deformable model for non-rigid registration")
    val dataFolder = new File("data/vertebrae/")
    val ref = MeshIO.readMesh(new File(dataFolder, "ref_20.ply")).get
    val gpmmFile = new File(dataFolder, "gpmm.h5.json")

    val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).getOrElse({
        println("Model not found, creating new one")
        val tmp = createModel(ref)
        StatisticalModelIO.writeStatisticalTriangleMeshModel3D(tmp, gpmmFile)
        tmp
    })

    println(s"Model rank: ${gpmm.rank}")

    val ui = ScalismoUI()
    ui.show(gpmm, "gpmm")

