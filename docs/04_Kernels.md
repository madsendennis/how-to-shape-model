# How to shape model - Part 4 - Deformable template / Kernel design

In this tutorial, I'll show you how to define the space of deformations our template mesh can undergo. So we will go from a static mesh <show template>, to a parameterized model that defines a space of deformations on the template, which we e.g. can randomly sample from to visually see the space of deformations <show random meshes from of model>.

<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the fourth tutorial in the series on how to create statistical shape models. 

Defining the deformation space that the template can undergo might seem daunting with the endless possibilities of combinations. 

Commonly asked question on the Scalismo mailing list is: “what parameters should I use for my models” and “what kernels to choose”. 

With a few heuristics in mind and a clear plan for defining your kernels, this task becomes a lot simpler. 

For simplicity of this tutorial, I will not go into the formal definition of kernels.
For this, I suggest you take a look at the tutorial from the scalismo website, or the instruction video from the statisticial shape modelling course at the university of Basel. I will link both of these resources in the video description.

When talking about Gaussian Processes, a Kernel function describes how two data points are connected by describing their covariance. Simply said, when data point 1 moves, does this have, or how much influence does this have on data point 2? And also, how is the distance between point 1 and point 2 measured?

As a side note, such a function should be positive definite - but for more details, look at the linked resources.

In this tutorial we will mainly look at the Gaussian kernel, and how we can modify it to, e.g. be symmetrical around a defined axis using the "symmetry kernel", only be active in certain areas using the "Change Point Kernel", or how to mix Gaussian kernels with different properties. Finally, I'll show the PCA kernel, as introduced in the first video, and how we can make it more flexible by augmenting it with an analytically defined kernel.

5 different types:
* Gaussian Kernel
* Mixture of Gaussians
* Symmetry Kernel
* Changepoint Kernel
* Augmenting a PCA kernel

Many more kernels exists than just the Gaussian kernel - but I often find mixing different Gaussian kernels sufficient, instead of using other kernel types.

For completeness, I also attach a link to different valid kernels to use in the description. This such as the exponential kernel, rational quadratic kernel and the periodic kernel (XXX LINK).

One thing we need to remember when working with kernels, is the dimensionality we work within. I will only show 3D examples, and so it would also be possible to model the covariance between dimensions. But for simplicity, we always assume that the 3 dimensions are independent when analytically defining kernels.
This is done using the DiagonalKernel.
As input, this kernel takes a PDKernel as, e.g. a GaussianKernel.

For the Gaussian Kernel, we have two parameters to set:
* The Sigma value, which defines the "width" of the kernel. The smaller this value is, the more local it is, meaning that only very close points will have a covariance over 0.
* With the scaling parameter, the strength of the covariance can be adjusted
val sigma = 10
val scaling = 1

val kernel = GaussianKernel3D(sigma, scaling)
val diagonal = DiagonalKernel3D(kernel)

val gp = GaussianProcess3D[EuclideanVector[_3D]](kernel)

When inspecting the model, it is important to remember what the model will be used for. Our goal is to make the model flexible enough to represent all the other shapes in our dataset this also means that when we randomly sample from our model, it is perfectly fine that the deformations look exaggerated and produces non-natural shapes. The most important part is that the deformations are smooth such that the mesh does not intersect with itself. This also means that e.g. the mesh we see here is far from flexible enough to represent other meshes as it only deforms slightly very locally. 

For the sigma value I like to use a value that is 1/2 or 1/3 of the longest distance in the mesh. For the vertebra, it is XX mm  on the X axis, therefore a sigma value of XX could make sense. 
Then we can tune the scaling value afterward if the magnitude of the deformations are not large enough. 

I would recommend starting out with a simple model like this with a simple Gaussian kernel, and then only make it more advanced if needed. 

And how do you know if more is needed? If the model has problems representing the meshes in your dataset, then more is needed. E.g. if some local curvatures are not nicely captured. 
You will only get to know so after running the non-rigid registration as introduced in the next two videos. 

For completeness of this video, let’s continue adding some local deformations to our model by combining a kernel with a large sigma and one with a small sigma, I.e. a global and a local kernel.

— — — — 

An alternative kernel is the symmetry kernel. To showcase this kernel, I’ll use the reference mesh from the basel face model.
The reference mesh is symmetrical around the Z axis and so we’ll define our kernel to be as well. In reality, faces are of course not fully symmetrical, but it is a good global kernel to have, we can then always add local deformations to it. 

— — — — 

Another kernel is the change point kernel. For this, let’s stick with the face mesh and make one side of the face with one kind of kernel and the other side with another kernel. 

— — — — 

The final kernel I want to show is another mixture of kernels. This kernel could e.g. be used to iteratively include more data into your model.
We start out with 5 meshes that are registered, from this we can create a PCA kernel as also shown in the first video. Of course 5 principal components rarely contain all small possible deformations, so we can augment the model e.g. with a Gaussian kernel, to make it more flexible. 


And that’s the end of the practical guide to choosing your kernels and hyperparameters. Really the most crucial part being visualizing your models at every step.


In the next tutorial I'll show you:
* How to manually code up a simple non-rigid registration algorithm.

<!-- That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video! -->

