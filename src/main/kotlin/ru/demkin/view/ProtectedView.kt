    package ru.demkin.view

import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import tornadofx.View

/**
 * Description of ru.demkin.view
 * @author evgen1000end
 * @since 07.08.2016
 */
class ProtectedView : View() {
    override val root = StackPane(Label("You are logged in"))



    init {
        with(root){

        }
    }
}
