# How to Shape Model - 
The almost complete idiot guide to making your first shape model.

This repository contains the video scripts and coding scripts as used in the YouTube video series [How to Shape Model](#).

In the video series, I go over all the practical design decisions when creating a statistical shape models (SSM).

1. [Introduction/Overview and Point-correspondence](docs/01_Introduction.md)
   * Video coming <!-- * [Video]() -->
2. [Template design](docs/02_Template.md)
   * Video coming <!-- * [Video]() -->
3. [Rigid Alignment](docs/03_Alignment.md)
   * Video coming <!-- * [Video]() -->
4. [Deformable template / Kernel design](docs/04_Kernels.md)
   * Video coming <!-- * [Video]() -->
5. [Simple Non-rigid registration](docs/05_SimpleNonRigid.md)
   * Video coming <!-- * [Video]() -->
6. [Registration Framework GiNGR](docs/06_GiNGR.md)
   * Video coming <!-- * [Video]() -->
7. [Model evaluation](docs/07_Evaluation.md)
   * Video coming <!-- * [Video]() -->

The accompanying code can be found in the source folder. To generate all the output files as shown in the videos, execute each of the `scala` scripts in order.
  
If running from the command line, remember to include the `project.scala` file in the execution, e.g.: 

```
// cd how-to-shape-model
scala-cli project.scala src/prepare_data/01_alignment.scala
```

Data is found in the `data/` folder. Link to the original source of the data:

* The Vertebrates are extracted from the images provided for the [MICCAI 2020 Challenge](https://github.com/anjany/verse)
* The Face template is from the [Basel Face Model](https://faces.dmi.unibas.ch/bfm/bfm2019.html)
* The Femur template is from the [Publication code repo - Learning Shape Priors](https://github.com/unibas-gravis/shape-priors-from-pieces)