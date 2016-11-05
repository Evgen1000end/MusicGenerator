package ru.demkin.models

import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class SoundbankPath {
  val pathProperty = SimpleStringProperty()
}

class UserModel(var soundbankPath: SoundbankPath) : ViewModel() {
  val path = bind { soundbankPath.pathProperty }
}