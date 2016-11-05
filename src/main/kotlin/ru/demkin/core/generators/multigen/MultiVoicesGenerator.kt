package ru.demkin.core.generators.multigen

import jm.JMC
import jm.music.data.Part
import jm.music.data.Score
import ru.demkin.core.data.Settings
import ru.demkin.core.data.Voice
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.tonality.MajorTonality
import ru.demkin.core.generators.Harmonizer
import ru.demkin.core.generators.IMusicGenerator
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MultiVoicesGenerator : IMusicGenerator {



  //private Tonality currentTonality;
  private var harmonizer: Harmonizer? = null
  private var voices: LinkedList<Voice>? = null


  override fun init() {
    Settings.CURRENT_TONALITY = MajorTonality(JMC.C4)
    //currentTonality 			= new MinorTonality(JMC.C4);
    harmonizer = Harmonizer()
    //harmonizer 				= new HarmonizerDummy();
    initVoices()
  }

  private fun initVoices() {
    voices = LinkedList<Voice>()
    //voices.add(new Voice(Voice.MELODY, "Piano", JMC.PIANO, 1, 100));
    //voices.add(new Voice(Voice.SECOND_VOICE, "Violin", JMC.VIOLIN, 2, 12));
    //voices.add(new Voice(Voice.SECOND_VOICE, "Cello", JMC.CELLO, 3, -12));
    //voices.add(new Voice(Voice.ACCOMPANIMENT, "Piano accomp", JMC.PIANO, 4));
    //voices.add(new Voice(Voice.MELODY, "Piano", JMC.PIANO, 5));
  }

  private val voicesHarmony: HashMap<Double, Chord>?
    get() {
      for (v in voices!!) {
        if (v.role === Voice.MELODY) {
          return harmonizer!!.getHarmonyForMotive(v.motive!!, Settings.CURRENT_TONALITY!!)
        }
      }
      println("Multi Voice Generator > getVoicesHarmony() > Error! Can't find main melody in voices list!")
      return null
    }

  private fun generateMainMotive() {
    for (v in voices!!) {
      if (v.role === Voice.MELODY) {
        v.setLastNote(Settings.CURRENT_TONALITY!!.randomMainNotePitched)
        v.generateMotive(Settings.CURRENT_TONALITY!!)
        break
      }
    }
  }

  private fun needGenerate(): Boolean {
    for (v in voices!!) {
      if (!v.isMuted)
        return true
    }

    //No voices or all are muted
    return false
  }

  fun updateBarCounterAndRefresh() {
    //Инкременирует счетчик тактов.
    Settings.accompanimentBarCounter++
    //RecordsManager.incrementCounter()

    //Если прошло 4 такта то у всех аккомпанементов меняет структуру
    if (Settings.accompanimentBarCounter >= 4) {
      for (v in voices!!) {
        v.refreshAccompanimentPattern()
      }
      Settings.accompanimentBarCounter = 0
    }
  }

  //Create new score
  //Generate main melody (search for main voice and ask it to generate)
  //Harmonize main melody (search for main voice and ask it to harmonize)
  //Skip muted voices
  //Generate motive for each voice
  //Get phrase from that motive
  //Set last note in voice
  //And finally add to score
  override val score: Score
    get() {
      val s = Score()

      if (!needGenerate())
        return s
      generateMainMotive()
      val harmony = voicesHarmony

      for (v in voices!!) {
        if (v.isMuted)
          continue
        v.generateMotive(harmony!!, Settings.CURRENT_TONALITY!!)
        val phr = v.getPhrase(harmony, Settings.CURRENT_TONALITY!!)
        v.setLastNote(phr.getNote(0).getPitch())
        s.add(Part(phr, v.name, v.pinstrument.midiId, v.channel))
      }

      updateBarCounterAndRefresh()

      return s
    }

  override fun addVoice(v: Voice) {
    voices!!.add(v)
  }

  override fun removeVoice(v: Voice) {
    voices!!.remove(v)
  }

}