package ru.demkin.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.Button
import javafx.stage.FileChooser
import javafx.stage.Modality
import org.slf4j.LoggerFactory
import ru.demkin.app.Styles
import ru.demkin.controllers.GitHub
import ru.demkin.models.SoundbankPath
import ru.demkin.models.UserModel
import tornadofx.*
/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class LoginView : View() {

  val logger = LoggerFactory.getLogger(LoginView::class.java)

  override val root = Form().addClass(Styles.login)
  val model = UserModel(SoundbankPath())
  val github: GitHub by inject()

  //var player: RealTimePlayer? = null

  val checkValue = SimpleBooleanProperty()

  init {
    title = "Soundbank loading"
    model.path.value = "C:\\Workspace\\jmg-master\\soundfonts\\fluid2.sf2"
    with(root) {

      hbox {
        addClass(Styles.logi)
        textfield(model.path).required(message = "Insert your path")
        button("Select") {

          setOnAction {
            val fileChooser = FileChooser()
            model.path.value = fileChooser.showOpenDialog(null).absolutePath
          }
        }
      }
      checkbox("By default") {
        bind(checkValue)
        setOnAction {
          println("Hello, checkbox $isSelected")
        }
      }

      button("Let's begin!") {
        setOnAction {
          load()
        }
      }
    }
  }

  private fun loadMidi(): Boolean {
    logger.info("Load midi begin ...")
    //player = RealTimePlayer(model.path.value)
    return true
  }

  private fun Button.load() {
    if (model.commit()) {
      runAsync {
        loadMidi()
      } ui { success ->
        if (success) {
          val v = MainView(model.path.value)
          v.openModal(modality = Modality.NONE)
        } else {
          alert(WARNING, "Load soundfont failed", "Check path")
        }
      }
    }
  }
}