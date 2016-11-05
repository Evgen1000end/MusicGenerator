package ru.demkin.core.generators.dodecaphony

import jm.JMC
import jm.constants.Instruments
import jm.music.data.Note
import jm.music.data.Part
import jm.music.data.Phrase
import jm.music.data.Score
import ru.demkin.core.data.Voice
import ru.demkin.core.generators.IMusicGenerator

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class DodecaphonySeriesGenerator : IMusicGenerator {

  private var dodecaphonizer: Dodecaphonizer? = null
  private val VOICES_COUNT = 1

  override fun init() {
    dodecaphonizer = Dodecaphonizer(intArrayOf(JMC.B4, JMC.BF4, JMC.G4, JMC.CS5, JMC.EF5, JMC.C5, JMC.D5, JMC.A4, JMC.FS4, JMC.E4, JMC.AF4, JMC.F4))
  }

  override val score: Score
    get() {
      val dodecScore = Score()
      val totalNotes = 12
      for (k in 0..VOICES_COUNT - 1) {
        val longRow = generateLongRow(dodecaphonizer!!, totalNotes)
        val longRowLengths = generateLongRowLengths(totalNotes)
        val pitch = (Math.random() * 2 + Math.floor(k * 0.5) + 60.0).toInt()

        val inst = Part("Piano", Instruments.PIANO, 0)
        val dodekPhrase = Phrase()
        for (i in 0..totalNotes - 1) {
          dodekPhrase.addNote(Note(longRow[i] + pitch, longRowLengths[i], 85 + if (i < 35) 35 - i else 0))
        }
        inst.add(dodekPhrase)
        dodecScore.addPart(inst)
      }
      return dodecScore
    }

  private fun generateLongRowLengths(totalNotes: Int): DoubleArray {
    val longRowLengths = DoubleArray(totalNotes)
    for (i in 0..totalNotes - 1) {
      if (i % 3 == 0) {
        longRowLengths[i] = JMC.QUAVER
      } else {
        longRowLengths[i] = JMC.SIXTEENTH_NOTE
      }
    }
    return longRowLengths
  }

  private fun generateLongRow(df: Dodecaphonizer, totalNotes: Int): IntArray {
    val longRow = IntArray(totalNotes)
    var i = 0
    while (i < totalNotes - 1) {
      val rndSerie = df.getRandomRowPitched((Math.random() * 11).toInt())
      //Dodekafonizer.printIntArray(rndSerie);
      for (j in rndSerie.indices) {
        if (i >= totalNotes)
          break
        longRow[i] = rndSerie[j]
        i++
      }
    }
    return longRow
  }

  override fun addVoice(v: Voice) {
    // TODO Auto-generated method stub

  }

  override fun removeVoice(v: Voice) {
    // TODO Auto-generated method stub

  }
}