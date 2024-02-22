import java.io.File
import scalismo.io.StatisticalModelIO
import breeze.linalg.DenseVector
import scalismo.statisticalmodel.PointDistributionModel
import scalismo.io.MeshIO

// After exporting the meshes, open the mesh collection in ParaView.
// - apply "generate surface normals" to get a better visualization of the model.
// - open the animation view.
// -  set canvas size: view -> preview -> custom
// -  choose model: sequence and set start time 0 and end time to number of meshes that are exported (68)
// -  set no of frames to the frame rate you want times the number of seconds that the clip should last.
// - export the animation (file -> save animation) as png files.

// Use imagemagick to convert the png files to a gif:
// - convert -delay 4 -loop 0 -alpha set -dispose previous *.png ssm.gif
// NOTE: The -delay is in 100ths of a second, so 4 is 0.04 seconds, meaning a frame rate of 25 fps.
@main def ssmComponentExport() =
  val ssm = StatisticalModelIO
    .readStatisticalMeshModel(new File("data/vertebrae/pca.h5.json"))
    .get
  val outputDir = new File("data/tmp/")
  outputDir.mkdirs()

  val valstop = 25
  val stepSize = 5
  val rangeInts =
    (0 to valstop by stepSize) ++ (valstop to -valstop by -stepSize) ++ (-valstop to 0 by stepSize)
  val rangeDouble = rangeInts.map(f => f.toDouble / 10.0)

  val components = Seq(0, 1, 2)

  var cnt = 0

  def processComponent(comp: Int): Unit = {
    rangeDouble.foreach { v =>
      val modelPars = DenseVector.zeros[Double](ssm.rank)
      modelPars(comp) = v
      val instance = ssm.instance(modelPars)
      MeshIO.writeMesh(
        instance,
        new File(outputDir, s"${"ssm_%04d".format(cnt)}.ply")
      )
      cnt += 1
    }
  }

  components.foreach { comp =>
    println(s"Component: $comp")
    processComponent(comp)
  }
