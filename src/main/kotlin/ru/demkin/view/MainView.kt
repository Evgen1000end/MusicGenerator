package ru.demkin.view

import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import jm.constants.Durations
import jm.constants.ProgramChanges
import jm.music.data.Note
import jm.music.data.Part
import jm.music.data.Phrase
import jm.music.data.Score
import ru.demkin.models.RealTimePlayer
import tornadofx.View

/**
 * Description of ru.demkin.view
 * @author evgen1000end
 * @since 07.08.2016
 */
class MainView(val soundbankPath: String) : View() {
  override val root = StackPane(Label("You are logged in"))

  init {
    val player = RealTimePlayer(soundbankPath)
    Thread(player).start()
    player.playScore(generateTestScore())
    with(root) {
    }
  }

  private fun generateTestScore(): Score {
    val score = Score("Chaos", 120.0)
    val guitarPart = Part("Choir", ProgramChanges.CHOIR, 0)
    val phr = Phrase(0.0)
    var xold = 0.0
    var x: Double
    var y: Double
    var yold = 0.0

    var a = 1.4
    var b = 0.3

    for (i in 0..999) {
      x = 1 + yold - a * xold * xold
      y = b * xold
      val note = Note((x * 24 + 48).toInt(), Durations.C)
      phr.addNote(note)
      xold = x
      yold = y
    }
    guitarPart.addPhrase(phr)
    score.addPart(guitarPart)

    return score
  }
}
