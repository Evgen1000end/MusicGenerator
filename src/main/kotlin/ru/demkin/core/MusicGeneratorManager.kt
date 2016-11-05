package ru.demkin.core

import jm.music.data.Tempo
import ru.demkin.core.data.Settings
import ru.demkin.core.data.Voice
import ru.demkin.core.generators.IMusicGenerator
import ru.demkin.models.RealTimePlayer
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MusicGeneratorManager {

  private var players = LinkedList<RealTimePlayer>()
  private var generator: IMusicGenerator? = null
  private var timeSignature: Double
  //private var gui: GUI
  //var recordsManager: RecordsManager

  /** Class constructor

   */
  init {
    timeSignature = (4 / 4).toDouble()
    Settings.TEMPO = Tempo(Tempo.DEFAULT_TEMPO) //60
    //recordsManager = RecordsManager()

   // gui = GUI()
   // gui.init(this)

    //Only after initializing the gui we can init recordsManager.
   // recordsManager.init(gui)
  }

  fun addPlayer(player: RealTimePlayer) {
    if (!players.contains(player)) {
      players.add(player)
      player.start()

      //Wait for player to be ready.
      while (!player.isReady) {
        try {
          Thread.sleep(100)
        } catch (e: InterruptedException) {
        }

      }
    } else {

    }
  }

  fun addVoice(v: Voice) {
    generator!!.addVoice(v)
  }

  fun removeVoice(v: Voice) {
    generator!!.removeVoice(v)
  }

  fun setGenerator(_generator: IMusicGenerator) {
    generator = _generator
    generator!!.init()
  }

  fun start() {
   // gui.show()
    //LoadingWindow.hideLoadingWindow()

    //RandomManager rm = new RandomManager(gui);
    //rm.start();
    var score = generator!!.score

    for (player in players)
      player.playScore(score)

    var startTime: Long = 0
    while (true) {
      val barLength = Settings.TEMPO!!.getPerSecond() * timeSignature * 4 * 1000 // Using current tempo and time signature calculate how many seconds does one bar last.
      if (System.nanoTime() / 1000000 - startTime < barLength) {
        try {
          Thread.sleep(10)
          continue
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }

      } else {
        //recordsManager.playBar(RecordsManager.PLAYBACK_BAR_COUNTER)
        score = generator!!.score

        score.setTempo(Settings.TEMPO!!.getPerMinute())

        for (player in players)
          player.playScore(score)
        startTime = System.nanoTime() / 1000000
      }
    }
  }

}