package ru.demkin.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.WARNING
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Window
import ru.demkin.app.Styles
import ru.demkin.controllers.GitHub
import ru.demkin.models.RealTimePlayer
import ru.demkin.models.SoundbankPath
import ru.demkin.models.UserModel
import tornadofx.*

class MainView : View() {
    override val root = Form().addClass(Styles.login)
    val model =UserModel(SoundbankPath())
    val github: GitHub by inject()

    var player:RealTimePlayer? = null

    val checkValue = SimpleBooleanProperty()

    init {
        title = "Soundbank loading"
        model.path.value = "C:\\PROJECTS\\JMG\\soundfonts\\fluid.sf2"
        with (root) {

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

            button("Log in") {
                setOnAction {
                    load()
                }
            }
        }
    }

    private fun loadMidi():Boolean {
        player = RealTimePlayer(model.path.value)

        return true
    }

    private fun Button.load(){
        if (checkValue.value) {
            if (model.commit()){
                runAsync {
                    loadMidi()
                } ui { success ->
                    if (success){
                        replaceWith(ProtectedView::class, ViewTransition.SlideIn)
                    } else {
                        alert(WARNING, "Load soundfont failed", "Check path")
                    }
                }
            }
        }
    }
}