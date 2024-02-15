# How to shape model - Part 2 - Template design

In this tutorial, I'll show you how to design your shape model reference shape. We'll go from this <noisy mesh extracted from image> to this <clean smooth triangulation> which will dramatically improve the usefulness of your shape model.

<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the second tutorial in the series on how to create statistical shape models. 

Choosing a good reference mesh is a very important step - but unfortunately also often overlooked. By mastering this step, you'll be able to create shape models that generalise better and can be magnitudes faster and easier to use.

So let's go over the most common strategies:
1. The most common strategy is to choose a random shape from the shape dataset.
    1. The benefit of this is the process being FAST, but this comes at the price of potentially BAD GENERALIZATION or inheriting biases in the chosen sample to your dataset if it, for instance, has some patient-specific artefacts or biases.
2. Another common method is to design the reference fully manually. The benefit of this is 100% control of what the reference contains. This is popular if none of the datasets is complete, or they might all contain a certain level of biases. The Basel Face Model shown in the demo video - which I’ll link in the description - is for instance, designed by an artist, where they have increased the triangulation in areas of high detail such as the eyes, mouth and nose, whereas large surface areas are with more sparse triangulation. The negative side of this is the time needed to create such a reference as well as the expertise that might be required to use 3D modelling software.
3. In my opinion, the best initial option is instead to correct a random shape from the dataset manually. This will be both relatively fast and will be able to generalise well if we manage to remove the patient-specific biases. For this, we will shortly go over an example
4. The fourth choice is an optional setting after either steps 1 or 3. The strategy here is to bring all the meshes in point correspondence with the chosen template and then compute the mean shape, which is what you'll use as the new reference mesh. This step can be iterated until the mean, and the reference mesh converges to the same shape, which usually happens already after 1 or 2 iterations.


For the semi-manual reference creation, I will use of two different programs, beside the Scalismo library. These are: Meshlab and Meshmixer. 
Meshlab will be used for applying different filters to the mesh: such as smoothening, sharpening, or triangulation decimation. 
Meshmixer will be used for manual correction of the mesh. Oftentimes, I even go back and forth a few times between the two programs. Many more options exist out there, such as Blender, Unity, Maya and so on. 

So, let's get started and create a reference mesh. For this, I'll choose a random mesh from the Vertebra dataset as mentioned in the introduction tutorial of the series.

The very first thing I normally do, is to align the mesh to the origin and rotate it in the direction you are interested in. This step can easily be done in Meshlab. Next up, we will inspect the mesh in Meshmixer. Now it is all a matter of using the different sculpt tools to clean up our mesh - or maybe using the select tool to select items that we do not want in the reference. If the triangulation is very coarse, I usually start out applying the refinement method to the mesh and thereafter either draw, flatten, drag, move or use one of the smoothening tools to clean up the mesh.

The last step I do is to decimate the mesh. The amount of decimation completely depends on your usage of the model. The more the mesh is decimated, the less points it will have, thereby it will be faster to compute and take up less space. But it will also not look as good when rendering it.

Let’s try out different decimations level in Slicer and see how it looks like. I personally prefer the mesh output from quadratic edge decimation algorithm, which can be found under x and x. 

The same algorithm is also available directly in Scalismo, so use it there you can simply load in the mesh, perform the decimation operation and save the mesh again. 

// Decimate mesh with scalismo

That’s all there is to it. Some simple steps that will save you hours if not days of work down the line.

In the next tutorial I'll show you:
* How to rigidly align the mesh data to simplify the non-rigid registration.

<!-- That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video! -->
