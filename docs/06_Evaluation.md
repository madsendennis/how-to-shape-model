# How to shape model - Part 6 - Model evaluation

In this tutorial, I'll go over different strategies to evaluate how good a shape model actually is. 

<!-- Hi and welcome to “Coding with Dennis” - my name is Dennis  -->
This is the sixth tutorial in the series on how to create statistical shape models. 

When it comes to model evaluation, nothing beats visual inspection of the anatomical properties the model. With Scalismo-UI we can inspect the individual principal components of the model one-by-one as already shown in the previous tutorials. 

Load in the model and inspect the principal components one-by-one. Then try to randomly sample from the model to see if the shapes look realistic and anatomically plausibl. and, very important, check that the model does not produce any kind of artifacts, such as self-intersection, inverted surface or similar. 

As a second step, think about the application where the model is to be used and see if an automatic evaluation can be created. This could for instance be partial mesh completion. In such a case, you can synthetically remove parts of a mesh and check the models reconstruction with the ground-truth.
A good first step for this is to use the training data itself to begin with. For the training data we know that the model should be able to exactly represent it. Then, after it is validated, we can move on to check the models performance on a test dataset where we know the complete shape as well. And finally, the model can be used on partial data with no ground-truth data. This data can of course only be qualitatively evaluated. 

Instead of comparing the complete meshes with a ground-truth mesh, another common evaluation strategy is landmark-based evaluation. Here the models ability to idetify certain landmark points are checked. For the model, we just need to create a landmark file where the landmarks are placed on the template mesh, then we extract the position of the same point ID after registration. The idea is now to compare the landmark with landmarks clicked by a human observer and possibly other automatic methods. 

Beside the qualitatively looking at the model or looking at the models performance on  on a specific application task, we can also look at some fundamental properties of  model, e.g. 

1. If we randomly sample from the model, is the model only able to represent items from the dataset it was built? Or how different shapes is it able to create? 
2. How good is the model in explaining items not in the training dataset?
3. How much variability does the model have, and how is this captured in the model?

These three properties are referred to as Specificity, Generalization and Compactness. The measures were introduced by Martin Styner in his 2003 paper:
[Evaluation of 3D correspondence methods for model building](https://link.springer.com/chapter/10.1007/978-3-540-45087-0_6) and has since become the standard when evaluating statistical shape models. Even though the methods provide some good insight about a single model, the measures are more useful when comparing different models, e.g. built on different datasets or built using different methods. 

The 3 metrics are usually reported by limiting the model to a certain number of principal components. A 2D plot can then be created by plotting the number of principal components used on the X-Axis and the metric value itself on the Y-Axis. 

## Generalization 
Generalization is measured on data items not in the dataset, so we will build our model on 7 of the Vertebrae samples and test on the remaining 3. 
A different strategy would be to do the average of a leave-one-out calculation. 

First, let's define our training and test data items and build our model to be evaluated.

```scala
    val dir = new File("data/vertebrae/registered")
```

Then we'll use the built in Scalismo function to compute the Generalization of the model. 

```scala
// Generalization function
```

Then we'll call the function with the model limited to a certain number of principal components
```scala
// map // model.rank to generalization
```
And finally, we can plot the data. 

<!-- Here we see that ... -->

## Specificity

## Compactness


<!-- Can be used to evaluate individual models, but more useful when compared to other models. This could for instance be if augmenting a model as introduced in one of the previous tutorials. To check if the model is augmented enough to generalize better, but not too much to suddently produce garbage/junk samples.  -->




Think about the application of the model. 
E.g. if used to complete meshes, then evaluate the model on that ability. 

Useful to compare differently build models or to 

- Compactness 
    * a model’s ability to use a minimal set of parameters to represent the data variability
    * sum of variability of all components
- Specificity
    * evaluate if the model only generates instances that are similar to those in the training set
    * Sample X items from model
- Generalization
    * the ability to describe instances outside of the training set
    * describe a shape not in the model


In the next tutorial I'll go over:
* Different ways to visualize statistical shape models. 

<!-- That was all for this video. Remember to give the video a like, comment below with your own shape model project and of course subscribe to the channel for more content like this.
See you in the next video! -->