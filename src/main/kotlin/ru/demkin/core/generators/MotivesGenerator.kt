package ru.demkin.core.generators

import jm.JMC
import ru.demkin.core.data.*
import ru.demkin.core.data.patterns.CPattern
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.Tonality
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MotivesGenerator : BaseVoiceGenerator() {

  private var previousMotive: Motive? = null
  private var firstNotePitch: Int = 0
  internal var lastBarRhythmLengths: LinkedList<List<Double>>

  init {
    lastBarRhythmLengths = LinkedList<List<Double>>()
    lastBarRhythmLengths.add(Arrays.asList(JMC.WHOLE_NOTE))                              // O
    lastBarRhythmLengths.add(Arrays.asList(JMC.HALF_NOTE, JMC.HALF_NOTE))                        // o + o
    lastBarRhythmLengths.add(Arrays.asList(JMC.QUARTER_NOTE, JMC.QUARTER_NOTE, JMC.HALF_NOTE))
  }

  fun generateMotive(pitch: Int, cT: Tonality, octaveSummand: Int): Motive {
    currentMotive = Motive()
    firstNotePitch = pitch + octaveSummand
    currentTonality = cT


    generateFirstRun(rhythmLengths)
    convertFirstRunToStables()

    generateSecondRun()
    convertSecondRunToStables()

    generateThirdRun()

    return currentMotive!!
  }

  fun generateLastBarMotive(pitch: Int, cT: Tonality, octaveSummand: Int): Motive {
    //TODO: Here we can also add some volume things or specific stuff for last bar. For now it differs only by patterns lengths
    currentMotive = Motive()
    firstNotePitch = pitch + octaveSummand
    currentTonality = cT
    generateFirstRun(lastBarRhythmLengths)
    convertFirstRunToStables()

    generateSecondRun()
    convertSecondRunToStables()

    generateThirdRun()

    return currentMotive!!
  }

  /**
   * Creates main structure, skeleton which consists only of simple patterns of same length
   */
  private fun generateFirstRun(rhytms: LinkedList<List<Double>>) {
    //Define length of items in one bar
    val rhythm = rhytms[Random().nextInt(rhytms.size)]

    //Add first note as SNPattern and set it as beginning of bar
    val firstNote = PNote(firstNotePitch, rhythm[0])
    firstNote.rhythmValue = 0.0

    currentMotive!!.firstRunPatterns.add(SNPattern(firstNote)) //First note in first run pattern.

    //Now loop until we fill whole bar with notes
    for (i in 1..rhythm.size - 1) {
      //TODO: Here goes balancing of upward and downward
      currentMotive!!.firstRunPatterns.add(
              Pattern(
                      DataStorage.getRandomPatternFromList(
                              DataStorage.SIMPLE_PATTERNS_FOR_MOTIVE)))
    }
    currentMotive!!.applyRhythm(rhythm, Motive.FIRST_RUN)
  }

  private fun convertFirstRunToStables() {
    //Check if firstRunPatterns exists
    if (currentMotive!!.firstRunPatterns == null || currentMotive!!.firstRunPatterns.size === 0) {
      println("Motives generator > convertFirstRunToStables() > Error! First run patterns list is undefined or empty!")
      return
    }

    //Put first note without any changes. We believe that first note was STABLE
    currentMotive!!.firstRunStables.add(SNPattern(currentMotive!!.firstRunPatterns.getFirst()))

    //If there was only one note in pattern.. exit
    if (currentMotive!!.firstRunPatterns.size === 1)
      return

    //If there were more than one note - loop until end of firstRunPatterns, converting them to stables. Start from id = 1, because first one is already copied
    for (i in 1..currentMotive!!.firstRunPatterns.size - 1) {
      //Elements used to create current Stable pattern: prev stable and current pattern
      val currPattern = currentMotive!!.firstRunPatterns.get(i)
      val prevStablePattern = currentMotive!!.firstRunStables.get(i - 1)

      //Get curr pattern's values
      val firstNoteInCurrentPattern = currPattern.notes!!.get(0)
      val transitionType = firstNoteInCurrentPattern.transitionType
      val transitionValue = firstNoteInCurrentPattern.transitionValue
      val newStableNotePitch = DataStorage.getPitchByTransition(transitionType, transitionValue!!, prevStablePattern.pitch, currentTonality)

      val lengthChange = firstNoteInCurrentPattern.length
      val newStableNoteLength = DataStorage.getAbsoluteLength(lengthChange, prevStablePattern.absoluteLength)

      val newStableNote = PNote(newStableNotePitch, newStableNoteLength)
      newStableNote.rhythmValue = prevStablePattern.firstNote.rhythmValue + newStableNoteLength //Calculate new position in bar
      currentMotive!!.firstRunStables.add(SNPattern(newStableNote))
    }
  }

  private fun generateSecondRun() {
    for (i in 0..currentMotive!!.firstRunStables.size - 1) {
      val currentPattern = CPattern()

      if (Math.random() < 0.4 && currentMotive!!.firstRunStables.get(i).absoluteLength === 1.0)
      //TODO: Horrible checking but it should work
      {
        //Calculate difference between notes
        var jumpValue = 0

        //For last note - just get random one between -12 and 12
        if (i == currentMotive!!.firstRunStables.size - 1) {
          jumpValue = Random().nextInt(25) - 12
        } else {
          //This will work because these patterns are actually SNPatterns
          jumpValue = currentMotive!!.firstRunStables.get(i + 1).firstNote.getPitch() - currentMotive!!.firstRunStables.get(i).firstNote.getPitch()
        }

        //We don't need odd numbers, like 7 or 3 inside. Replace them with even ones: 6 or 2.
        if (jumpValue % 2 != 0) {
          jumpValue -= 1
        }

        //If we don't have any entries for current jump - substract 1 until good one is found
        while (!DataStorage.MOVEMENT_PATTERNS_VALUED.containsKey(jumpValue)) {
          jumpValue -= 1
        }

        val randomPatternForJump = DataStorage.getRandomPatternByJumpValue(jumpValue, DataStorage.MOVEMENT_PATTERNS_VALUED)

        currentPattern.addPattern(randomPatternForJump)
        //currentPattern.multiplyFirstNoteLength((1.0 / (randomPatternForJump.getNotes().size() + 1)));

      } else {
        //Else we leave note as is. We don't add any notes inside cpattern, so it will be empty = no figures will be added to SNPattern at convertSecondToStables
      }

      currentMotive!!.secondRunPatterns.add(currentPattern)
    }
  }


  private fun convertSecondRunToStables() {
    for (i in 0..currentMotive!!.firstRunStables.size - 1) {
      val pattern = currentMotive!!.firstRunStables.get(i)

      //Merge together stable notes and CPatterns.
      val newPattern = CPattern()
      newPattern.addPattern(SNPattern(pattern))
      newPattern.addPattern(CPattern(currentMotive!!.secondRunPatterns.get(i)))

      //Adjust note length
      newPattern.multiplyFirstNoteLength(1.0 / (currentMotive!!.secondRunPatterns.get(i).notes!!.size + 1))

      //Finally insert this pattern into SecondRunStables
      currentMotive!!.secondRunStables.add(newPattern)
    }
  }

  private fun generateThirdRun() {
    for (i in 0..currentMotive!!.secondRunStables.size - 1) {
      //Copy note to third run
      val newPattern = CPattern(currentMotive!!.secondRunStables.get(i) as CPattern)
      currentMotive!!.thirdRunPatterns.add(newPattern)

      //And add some melismas. Just a little bit.
      if (Math.random() < 0.0005 * Settings.MELISMAS_CHANCE && Settings.USE_MELISMAS) {
        newPattern.firstNote.melismas = Melisma(DataStorage.getRandomMelismaFromList(DataStorage.MELISMAS))
      }
    }
  }


  fun generateSimilarMotive(prevMotive: Motive, pitch: Int, cT: Tonality, octaveSummand: Int): Motive {
    currentMotive = Motive()
    previousMotive = prevMotive
    firstNotePitch = pitch + octaveSummand
    currentTonality = cT

    //We copy old first run patterns to new motive, then change them a bit..
    duplicatePatternsList(previousMotive!!.firstRunPatterns, currentMotive!!.firstRunPatterns)

    //Now change first note's pitch: add something between -2/+2
    //TODO Add some random to pitch
    firstNotePitch = currentTonality!!.getTonalityNoteByStep(Random().nextInt(5) - 2, firstNotePitch)
    currentMotive!!.firstRunPatterns.get(0).firstNote.setPitch(firstNotePitch)

    //Now convert to Stables
    convertFirstRunToStables()

    //Now we copy old second run patterns to new motive, then change them a bit (or maybe leave them as is)
    duplicateCPatternsList(previousMotive!!.secondRunPatterns, currentMotive!!.secondRunPatterns)

    //Now convert second to Stables
    convertSecondRunToStables()

    //currentMotive.testNotate(currentMotive.firstRunPatterns);
    //currentMotive.testNotate(currentMotive.secondRunPatterns);

    generateThirdRun()

    return currentMotive!!
  }
}