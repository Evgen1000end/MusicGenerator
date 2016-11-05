package ru.demkin.core.generators.multigen

import jm.JMC
import ru.demkin.core.data.DataStorage
import ru.demkin.core.data.Motive
import ru.demkin.core.data.PInstrument
import ru.demkin.core.data.PNote
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.patterns.CPattern
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.Tonality
import ru.demkin.core.generators.BaseVoiceGenerator
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class SecondVoiceGenerator : BaseVoiceGenerator() {

  private var harmony: HashMap<Double, Chord>? = null
  private var lastNote: Int = 0

  init {
    rhythmLengths = LinkedList<List<Double>>()
    rhythmLengths.add(Arrays.asList(JMC.WHOLE_NOTE))                              // O
    rhythmLengths.add(Arrays.asList(JMC.HALF_NOTE, JMC.HALF_NOTE))                        // o + o
    rhythmLengths.add(Arrays.asList(JMC.HALF_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE))            // o + . + .
    rhythmLengths.add(Arrays.asList(JMC.DOTTED_HALF_NOTE, JMC.QUARTER_NOTE))                  // o. + .
    rhythmLengths.add(Arrays.asList(JMC.QUARTER_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE))  // . + . + . + .
  }

  override fun setPossiblePatterns(instrument: PInstrument) {
    possiblePatterns = DataStorage.SECOND_VOICE_PATTERNS //TODO: Pinstrument can be used here to randomize patterns
  }

  fun generateMotive(
          harm: HashMap<Double, Chord>,
          currentTonality: Tonality,
          octaveSummand: Int,
          lNote: Int): Motive {
    currentMotive = Motive()

    this.currentTonality = currentTonality
    this.harmony = harm
    this.lastNote = lNote

    //If we don't know how to start a phrase - we select from random note
    if (lNote == 0) {
      this.lastNote = harmony!![0.0]!!.chord[Random().nextInt(3)]
      +60
      +octaveSummand
    }

    generateSecondRun()
    convertSecondRunToStables()

    generateThirdRun()
    return currentMotive!!
  }

  fun generateMotive(
          harmony: HashMap<Double, Chord>,
          currentTonality: Tonality,
          octaveSummand: Int): Motive {
    return generateMotive(harmony, currentTonality, octaveSummand, 0)
  }

  fun generateMotive(
          harmony: HashMap<Double, Chord>,
          currentTonality: Tonality): Motive {
    return generateMotive(harmony, currentTonality, 0, 0)
  }

  private fun generateSecondRun() {
    val rhythm = rhythmLengths.get(Random().nextInt(rhythmLengths.size))

    //Add first note to second voice
    //http://cdn.memegenerator.net/instances/400x/35334325.jpg
    val firstNote = PNote(lastNote, rhythm.get(0))
    firstNote.rhythmValue = 0.0

    val firstPattern = CPattern()
    firstPattern.addPattern(SNPattern(firstNote))
    currentMotive!!.secondRunPatterns.add(firstPattern) //First note in pattern.

    //Iterate through array of rhythms
    for (i in 1..rhythm.size - 1) {

      val currentPattern = CPattern()
      currentPattern.addPattern(
              Pattern(
                      DataStorage.getRandomPatternFromList(possiblePatterns!!)))

      currentMotive!!.secondRunPatterns.add(currentPattern)
    }

    currentMotive!!.applyRhythm(rhythm, Motive.SECOND_RUN)
  }


  private fun convertSecondRunToStables() {
    for (i in 0..currentMotive!!.secondRunPatterns.size - 1) {
      //Finally insert this pattern into SecondRunStables
      currentMotive!!.secondRunStables.add(currentMotive!!.secondRunPatterns.get(i))
    }
  }


  private fun generateThirdRun() {
    for (i in 0..currentMotive!!.secondRunStables.size - 1)
      currentMotive!!.thirdRunPatterns.add(CPattern(currentMotive!!.secondRunStables.get(i) as CPattern))
  }
}