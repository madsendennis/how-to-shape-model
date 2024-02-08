//> using scala "3.3"
//> using dep "ch.unibas.cs.gravis::scalismo-ui:0.92.0,exclude=ch.unibas.cs.gravis%vtkjavanativesmacosimpl"

import scalismo.ui.api.ScalismoUI

object HelloScalismo extends App {
    scalismo.initialize()
    ScalismoUI()
}
