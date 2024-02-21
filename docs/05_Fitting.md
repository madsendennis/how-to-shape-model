# How to shape model - Part 5 - Model fitting (non-rigid registration)

In this tutorial, I'll show you how to use the deformable model that we created to establish correspondence between meshes. 
<!-- VIDEO OF Non-rigid registration -->

<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the fifth tutorial in the series on how to create statistical shape models. 

In this tutorial we'll first manually code up a model fitting function to understand all the aspects of what goes into non-rigid regitration using the Gaussian Processes and the Scalismo library. 

At first, let's load in a model and a target mesh. Before doing so, be sure to have executed step 1 and 2 in the `src/prepare_data` folder as this will first align all our target data and create the model to use.
```scala
    val dataDir = new File("data/vertebrae/")
    val gpmmFile = new File(dataDir, "gpmm.h5.json")
    val lmsFile = new File(dataDir, "ref_20.json")
    val gpmm = StatisticalModelIO.readStatisticalTriangleMeshModel3D(gpmmFile).get
    val lms = LandmarkIO.readLandmarksJson[_3D](lmsFile).get

    val targetMesh = MeshIO.readMesh(new File(dataDir, "aligned/sub-verse010_segment_20.ply")).get
    val targetLms = LandmarkIO.readLandmarksJson[_3D](new File(dataDir, "aligned/sub-verse010_segment_20.json")).get

    val ui = ScalismoUI()
    val modelGroup = ui.createGroup("modelGroup")
    val targetGroup = ui.createGroup("targetGroup")
    ui.show(modelGroup, gpmm, "gpmm")
    ui.show(targetGroup, targetMesh, "target")
```
Since the target data in this example is very noisy, our goal is not to do a perfect fit, but instead to capture the overall size of the target data.
Since we have landmark points available both for the model and the target mesh, we can start out trying to "fit" our model to these landmark points.
```scala
    val lmsData = lms.zip(targetLms).map{ case(lm1, lm2) => (gpmm.reference.pointSet.findClosestPoint(lm1.point).id, lm2.point)}.toIndexedSeq
    val lmPosterior = gpmm.posterior(lmsData, 1.0)
    val lmFit = lmPosterior.mean
```
For some applications, this might be good enough. We can even just continue adding landmarks to get the surfaces closer and closer together. You might also want to play around with the uncertainty value when calculating the posterior, this value should be seen as the uncertainty of the landmark observation. 

Instead of adding more landmarks, we want to find the "corresponding points" automatically. To do so, we will implement a form of iterative closest point (ICP)algorithm, which uses the same principal as above, to calculate a posterior model given some observations and then we take the most likely shape, i.e. the mean from the distribution as our proposal. 
To find the corresponding points, we simply estimate this to be the closest point on the target surface. To begin with, we can then assign a large uncertainty value to our observation. The idea is then to iteratively move the model closer to the target mesh, by estimating new corresponding points in each iteration and lowering the uncertainty. 
To begin with, I will just show you a very simple implementation method that has the same structure as the rigid ICP alignment we saw in [tutorial 2](05_Fitting.md)

```scala

```

A ton of configuration possibilities exist for the ICP algorithm, for instance how the closest points are taken. This could be either closest Euclidean point on a target surface, closest vertex on the target surface, closest point along the surface normal and many more. The same goes for the uncertainty, which can either be manually set for all correspondent pairs or it we can come up with a way to calculate the uncertainty based on the distance between the model surface and the target surface for each point. 

Continuing we can either make use of the original model `gpmm` or we can use the model that is conditioned on the landmark observations. 

And that’s the end of the practical steps to create your first statistical shape model. The most cruzial part is to look at your data and from there, deside how e.g. the kernel parameters need to look as well as the noise assumption during the fitting stage. If the dataset is very noisy, it does not make sense to creata model with thousands of basis functions that can perfectly fit the data. And likewise, if you have perfectly clean data, you need to add enough basis function to your model in order for it to be able to describe the data in detail. Also rememeber to look at the official Scalismo tutorial on [Model fitting](https://scalismo.org/docs/Tutorials/tutorial11).

The remaining tutorials are focused on model evaluation and different ways to visualize your created statistical models. 

In the next tutorial I'll go over:
* Typical evaluation metrics used to evaluate your statistical shape model. 

<!-- That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video! -->

