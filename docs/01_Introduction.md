# How to shape model - Part 1 - Introduction

In this series, I’ll show you how to get from a set of 3d meshes to a 3D statistical shape model. So let’s get right into it! 

<!-- <Show visualisation of meshes merging into model> -->
![Vertebrae Dataset!](/img/vertebrae/raw_dataset.png)


<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the first tutorial in a series on how to create statistical shape models. 

In short, a statistical shape model is a geometric model that describes the statistical properties of semantically similar objects very compactly. 
And if that sounds a bit too complex, let's instead start with looking at an example:

![Vertebrae Dataset!](/img/vertebrae/ssm.gif)

Here we visually inspect an already created statistical shape model. We see that each of the components show some variability in the geometry. 
The order of these components is such that the first components shows the direction in the data with the highest variability. The next component a perpendicular direction with the second highest variability and so on. 
We can also randomly set all the parameters in the model to create a new novel instance. 

In my previous [video](https://www.youtube.com/watch?v=__1tvaIKtaU), I already showed some use cases of statistical shape models - so if you are still not sure wether this is for you, have a look at the demo applications linked in the description. 

The code for creating such a model is straightforward. So let’s switch to an IDE and get typing. 
For the coding part, I’ll be using the Scala command line interface, or CLI in short. I will use the SCALA programming language and the Scalismo library. And I’ll use VS code as my development environment.

First install `scala-cli` if you haven’t already, use `scala-cli —version` to check your system version. It needs to be at least version 1.x. 
Then execute `scala-cli ide-setup .` to configure the folder you are in to be a scala client folder and open vs code with `code .`
To begin with, we specify the Scala version to use and the scalismo library. 

```scala
//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:1.0.0"
```

We will use the newer Scala 3 format and stick to the new python like styling with indentation instead of curly brackets.


Now, let’s load in the data. The data is stored in a folder in .PLY format. We need to specify one of the meshes as the reference mesh. In The next video we will go over good practices of choosing this mesh. For now, a random one can be chosen.

```scala
    val dataDir = new File("data/vertebrae/")
    val dataFolder = new File(dataDir, "registered")
    val registeredMeshes = dataFolder.listFiles().filter(_.getName.endsWith(".ply")).map(MeshIO.readMesh(_).get).toIndexedSeq
    val ref = registeredMeshes.head

    val dataCollection = DataCollection.fromTriangleMesh3DSequence(ref, registeredMeshes)
    val ssm = PointDistributionModel.createUsingPCA(dataCollection)
    val ui = ScalismoUI()
    ui.show(ssm, "ssm")
```

And that’s basically all there is to it.
Now you might ask why I would need a whole video series to explain this simple method that only takes up a few lines of code.
The reason for this is the requirement that these meshes need to be in point-correspondence with one another. Before explaining this phenomena, let’s try to build a model from meshes that are not in point-correspondence.

Instead of using the registered folder, let's instead use the aligned folder.

```scala
    ... 
    val dataFolder = new File(dir, "aligned")
    ... 
```

If we are lucky to compute the model, we might end up with such a model, where the deformations makes little to no sense.
Alternatively, you might get an error regarding the number of points in the meshes that were used. This will happen in the meshes in the dataset has fewer points than the reference mesh.

Let’s have a closer look at our meshes. For this, let’s visualize the same point ID on all the meshes in our dataset. In the dataset that works, we see that the same point ID corresponds to the same anatomical point on all the meshes.

```scala
val pId = PointId(0)
val lms = meshes.map{m =>
  Landmark3D(id="id0", m1.PointSet.Point(pId))
}
ui.show(lms, "lms")
```

If we do the same for the other meshes, we see that this isn’t the case.

Let’s look at a simple case of 3 hands. What the shape model contains is essentially the mean position and variance for each point in the mesh - and of cause the covariance to neighbouring points. So in the case of the hands, we will find the mean hand size as well as the variability at each point, here specified with a gray ellipsis.

When meshes are extracted from images e.g. by using the marching cubes algorithm or by scanning an object, they will not by default be in point-correspondence. They will rarely have the same number of points. For this, we can perform non-rigid registration between a reference mesh and all the meshes in our dataset to obtain this property. This is also often referred to as fitting.

A simple way to explain this, is that we choose 1 reference mesh and we then find a deformation field that deforms the reference mesh to approximate each of the meshes in the dataset as visualized here. 

<!-- <Visual slide from thesis - wrapping mesh around target> -->

Each of the meshes in the dataset will in other words get the same point structure as the reference mesh, which is why it might be a good idea to not just choosing a random mesh.

For this video series, we will use a shape dataset of vertices from the vertebrae segmentation challenge at [MICCAI (VerSe: Large Scale Vertebrae Segmentation Challenge 2020)](
https://github.com/anjany/verse). For simplicity I've already extracted the mesh from 10 of the segmentation masks and added those to my repository which I have linked in the description.

So, in the following videos we will go over:

1. How to choose or design a reference mesh.
2. How to rigidly align the mesh data to simplify the non-rigid registration.
3. How to choose the space of possible deformations that the reference mesh can undergo.
4. How to manually code up a simple non-rigid registration algorithm.
5. How to perform non-rigid registration with different registration algorithms using the GiNGR algorithm. 
6. How to evaluate and compare statistical shape models
7. How to visualize statistical shape models
   
With the [GiNGR](https://github.com/unibas-gravis/GiNGR) framework, I’ll also show how to do simple multi-resolution fitting which enables very fast and precise registrations.
That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video!
