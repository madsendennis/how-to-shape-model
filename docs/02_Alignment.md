# How to shape model - Part 3 - Alignment / Rigid registration

In this tutorial, I'll show you different methods to rigidly align your dataset. We will go from randomly aligned meshes ![Vertebrae dataset!](/img/vertebrae/all_raw.png) to a well-aligned set of meshes from where it is much simpler to establish point-correspondence ![Vertebrae dataset!](/img/vertebrae/all_aligned.png) 

<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the second tutorial in the series on how to create statistical shape models. 

Aligning your dataset can be tedious, but the time spent here can be well worth it, as it will make it much simpler to establish point correspondence and thereby create great statistical shape models.

In this tutorial, I'll go over 4 different methods for aligning your dataset.
1. We will start looking at the most basic, which always works, manual landmark annotation.
2. Then we will look at simple automatic alignment methods such as
    * centre alignment and
    * PCA alignment
3. Finally, we will look at a fully automatic iterative alignment method, namely rigid iterative closest point, or ICP.

Also have a look at the official scalismo documentation, there is also a guide on rigid alignment [link](https://scalismo.org/docs/Tutorials/tutorial02).

For the manual annotation, let's start an instance of Scalismo-UI and load a mesh. Then we click the landmarking tool and start clicking landmarks. Either you can make use of the order the landmarks are defined, or as an alternative, I like to give meaningful names to the landmarks. If you have a large dataset, you can code up a semi-automatic method to help automatically change the landmarking names. Still, for the simplicity of this tutorial, we stick to manually editing the names.

After the landmarks are clicked for each and every mesh in the dataset, it is time to align our data using Scalismo. 

We first load in the reference landmarks file as these are the ones we would like to align our data to.
Then we loop over all the data in our data folder, read in the landmark and mesh pairs one by one, calculate the transformation using the Landmarks transformation, apply the transformation to the mesh and save the mesh in a new aligned folder. Of course feel free to overwrite the original file. 
Also a good idea is to visualize the actual aligned mesh output to be sure that all your landmarks were correctly clicked. 

```scala 
val ui = ScalismoUI()
val lm = LandmarkIO.read(new File("");
```

Origin alignment is an automatic method that works well if the orientation of the meshes are similar. This could also additionally be refined with rigid alignment that we will discuss briefly.
```scala
    val file = new File("data/vertebrae/raw/sub-verse010_segment_20.ply")
    val mesh: TriangleMesh[_3D] = MeshIO.readMesh(file).get
    val origin = mesh.pointSet.points.map(_.toVector).reduce(_ + _) / mesh.pointSet.numberOfPoints
    val translation = Translation3D(EuclideanVector3D(origin.x, origin.y, origin.z))
    val alignedMesh = mesh.transform(translation.inverse)
```
If you have metadata or some domain specific knowledge that hints about the applied rotation, translation and even scaling, you can also build up your own complete transformation by specifying each of the components. Classes exist that then applies the different transformations one after the other, as hinted by the name. In the case of `TranslationAfterScalingAfterRotation3D`, rotation will be applied first, then scaling and then translation. 
```scala
    val translation = Translation3D(EuclideanVector3D(origin.x, origin.y, origin.z))
    val rotation = Rotation3D(phi = 0, theta = 0, psi = 0, center = Point3D(0, 0, 0))
    val scaling = Scaling3D(1.0)
    val transformation = TranslationAfterScalingAfterRotation3D(translation, scaling, rotation)
    val alignedMesh = mesh.transform(transformation)
```

If the meshes have a major directional axis, then the PCA alignment could be useful. Note however that the axis direction might be opposites, so you will need to manually go over and rotate the meshes by 180 degrees around some of the axis. For this example I'll show you a few femur bones where there is a clear major axis direction.
```scala
    def alignmentPrincipalAxises(mesh: TriangleMesh3D): RotationAfterTranslation[_3D] =
        val N = 1.0 / mesh.pointSet.numberOfPoints
        val center = (mesh.pointSet.points.map(_.toVector).reduce(_ + _) / mesh.pointSet.numberOfPoints).toPoint
        val cov = mesh.pointSet.points.foldLeft[SquareMatrix[_3D]](SquareMatrix.zeros)((acc, e) => acc + (e - center).outer(e - center)) * N
        val SVD(u, _, _) = breeze.linalg.svd(cov.toBreezeMatrix)
        val translation = Translation3D(center.toVector).inverse
        val rotation = Rotation3D(SquareMatrix[_3D](u.toArray), Point3D(0, 0, 0)).inverse
        RotationAfterTranslation(rotation, translation)

    val translation = Translation3D(EuclideanVector3D(100,100,100))
    val rotation = Rotation3D(phi = Math.PI/8, theta = Math.PI/4, psi = Math.PI/2, center = Point3D(0, 0, 0))
    val transformation = TranslationAfterRotation(translation, rotation)
    val targetMesh = mesh.transform(transformation)

    val transformationPCA = alignmentPrincipalAxises(targetMesh)
    val orientedMesh = targetMesh.transform(transformationPCA)
```
Finally, let's perform the alignment using automatic ICP alignment. [Scalismo Rigid ICP tutorial](https://scalismo.org/docs/Tutorials/tutorial10)

```scala

```

In reality, you might often end up using a mixture of the above-mentioned methods. For the vertebras, I have defined a few manually clicked landmarks as also available on the github repository. After the rough initial alignment, I performed rigid ICP.
Depending on the dataset you are working with, a different mixture might be more useful.

And that’s basically all there is to rigidly aligning data. Of course, a lot of more advanced methods exist out there, but for building simple statistical shape models from a relatively small set of data items, I am confident that the provided methods will take you far.

In the next tutorial:
* We’ll look at kernels. Or said in another way, how to choose the space of possible deformations that the reference mesh can undergo.

<!-- That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video! -->
