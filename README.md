# How to Shape Model - A tutorial
**The almost complete idiot guide to making your first shape model.**

This repository contains the setup coding scripts and data used in the YouTube video series [How to Shape Model](#).

In the video series, I go over all the practical design decisions when creating a statistical shape model (SSM).

Links to blog posts and videos of each tutorial
1. **Introduction/Overview and Point-correspondence**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part1/)
   * [Video](https://www.youtube.com/watch?v=D4W2sotakYk)
2. **Rigid Alignment**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part2/)
   * [Video](https://www.youtube.com/watch?v=FiC0lVt-noQ)
3. **Template design**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part3/)
   * [Video](https://www.youtube.com/watch?v=AO2UH4Xji60)
4. **Deformable template / Kernel design**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part4/)
   * [Video](https://www.youtube.com/watch?v=sOsaoDUIh94)
5. **Non-rigid registration/fitting**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part5/)
   * [Video](https://www.youtube.com/watch?v=4ELS5ZYm7eo)
6. **Model evaluation**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part6/)
   * [Video](https://www.youtube.com/watch?v=V81OuoHDRkk)
7. **Model visualization**
   * [Blog post](https://dennismadsen.me/posts/how-to-shape-model-part7/)
   * [Video](https://www.youtube.com/watch?v=ROSB3q82gsg)

The accompanying code can be found in the source folder. To generate all the output files as shown in the videos, execute each of the `scala` scripts in order.
  
If running from the command line, remember to include the `project.scala` file in the execution, e.g.: 

```bash
// cd how-to-shape-model
scala-cli project.scala src/prepare_data/01_alignment.scala
scala-cli project.scala src/prepare_data/02_defineModel.scala
scala-cli project.scala src/prepare_data/03_registration.scala
scala-cli project.scala src/prepare_data/04_buildSSM.scala
```

Data is found in the `data/` folder. Link to the original source of the data:

* The Vertebrates are extracted from the images provided for the [MICCAI 2020 Challenge](https://github.com/anjany/verse)
* The Face template is from the [Basel Face Model](https://faces.dmi.unibas.ch/bfm/bfm2019.html)
* The Femur template is from the [Publication code repo - Learning Shape Priors](https://github.com/unibas-gravis/shape-priors-from-pieces)
