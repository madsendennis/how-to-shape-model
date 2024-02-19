import java.io.File 
import scalismo.io.MeshIO
import scalismo.ui.api.ScalismoUI
import scalismo.geometry._
import scalismo.transformations.Translation3D
import scalismo.transformations.Rotation3D
import scalismo.transformations.Scaling3D
import scalismo.transformations.TranslationAfterScalingAfterRotationSpace3D
import scalismo.transformations.TranslationAfterScalingAfterRotation
import scalismo.transformations.TranslationAfterScalingAfterRotation3D
import org.apache.commons.math3.geometry.euclidean.threed.Rotation
import scalismo.mesh.TriangleMesh3D
import scalismo.transformations.TranslationAfterRotation3D
import breeze.linalg.svd.SVD
import scalismo.transformations.TranslationAfterRotation
import scalismo.transformations.RotationAfterTranslation
import scalismo.registration.LandmarkRegistration
import scalismo.common.PointId
import scalismo.mesh.TriangleMesh

def alignmentPrincipalAxises(mesh: TriangleMesh3D): RotationAfterTranslation[_3D] =
    val N = 1.0 / mesh.pointSet.numberOfPoints
    val center = (mesh.pointSet.points.map(_.toVector).reduce(_ + _) / mesh.pointSet.numberOfPoints).toPoint
    val cov = mesh.pointSet.points.foldLeft[SquareMatrix[_3D]](SquareMatrix.zeros)((acc, e) => acc + (e - center).outer(e - center)) * N
    val SVD(u, _, _) = breeze.linalg.svd(cov.toBreezeMatrix)
    val translation = Translation3D(center.toVector).inverse
    val rotation = Rotation3D(SquareMatrix[_3D](u.toArray), Point3D(0, 0, 0)).inverse
    RotationAfterTranslation(rotation, translation)

// def attributeCorrespondences(movingMesh: TriangleMesh[_3D], ptIds : Seq[PointId]) : Seq[(Point[_3D], Point[_3D])] = 
//     ptIds.map((id : PointId) =>
//         val pt = movingMesh.pointSet.point(id)
//         val closestPointOnMesh2 = mesh2.pointSet.findClosestPoint(pt).point
//         (pt, closestPointOnMesh2)
//     )

// def ICPRigidAlign(movingMesh: TriangleMesh3D, ptIds : Seq[PointId], numberOfIterations : Int) : TriangleMesh[_3D] = 
//     if (numberOfIterations == 0) then 
//       movingMesh 
//     else 
//       val correspondences = attributeCorrespondences(movingMesh, ptIds)
//       val transform = LandmarkRegistration.rigid3DLandmarkRegistration(correspondences, center = Point(0, 0, 0))
//       val transformed = movingMesh.transform(transform)
//       ICPRigidAlign(transformed, ptIds, numberOfIterations - 1)  


// def alignmentRigidICP(mesh: TriangleMesh3D, numOfPoints: Int, iterations: Int): TriangleMesh[_3D] = 
//     val ptIds = (0 until mesh.pointSet.numberOfPoints by 50).map(i => PointId(i))
//     ICPRigidAlign(mesh, ptIds, iterations)


@main def main() = 
    println(s"Scalismo version: ${scalismo.BuildInfo.version}")
    val file = new File("data/femur/ref.ply")
    val mesh = MeshIO.readMesh(file).get

    val translation = Translation3D(EuclideanVector3D(100,100,100))
    val rotation = Rotation3D(phi = Math.PI/8, theta = Math.PI/4, psi = Math.PI/2, center = Point3D(0, 0, 0))
    val transformation = TranslationAfterRotation(translation, rotation)
    val targetMesh = mesh.transform(transformation)
    
    val transformationPCA = alignmentPrincipalAxises(targetMesh)
    val orientedMesh = targetMesh.transform(transformationPCA)

    val ui = ScalismoUI()
    ui.show(targetMesh, "mesh").color = java.awt.Color.RED
    ui.show(orientedMesh, "mesh").color = java.awt.Color.GREEN