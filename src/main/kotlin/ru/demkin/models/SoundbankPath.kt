package ru.demkin.models

import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel

/**
 * Description of ru.demkin.models
 * @author evgen1000end
 * @since 07.08.2016
 */
class SoundbankPath {
  val pathProperty = SimpleStringProperty()
}

class UserModel(var soundbankPath: SoundbankPath) : ViewModel() {
  val path = bind { soundbankPath.pathProperty }
}