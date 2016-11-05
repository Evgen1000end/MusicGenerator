package ru.demkin.core.data

import jm.gui.cpn.Notate
import jm.music.data.Phrase
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.patterns.CPattern
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.MajorTonality
import ru.demkin.core.data.tonality.Tonality
import ru.demkin.core.generators.Harmonizer
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class Motive {


  var firstRunPatterns: LinkedList<Pattern>
  var firstRunStables: LinkedList<SNPattern>
  var secondRunPatterns: LinkedList<CPattern>
  var secondRunStables: LinkedList<Pattern>
  var thirdRunPatterns: LinkedList<Pattern>

  init {
    firstRunPatterns = LinkedList<Pattern>()
    firstRunStables = LinkedList<SNPattern>()
    secondRunPatterns = LinkedList<CPattern>()
    secondRunStables = LinkedList<Pattern>()
    thirdRunPatterns = LinkedList<Pattern>()
  }

  fun getFirstPhrase(harmony: HashMap<Double, Chord>, currentTonality: Tonality): Phrase {
    return DataStorage.patternsToPhrase(firstRunPatterns, harmony, currentTonality)!!
  }

  fun getSecondPhrase(harmony: HashMap<Double, Chord>, currentTonality: Tonality): Phrase {
    return DataStorage.patternsToPhrase(secondRunStables, harmony, currentTonality)!!
  }

  fun getPhrase(harmony: HashMap<Double, Chord>, currentTonality: Tonality): Phrase {
    return DataStorage.patternsToPhrase(thirdRunPatterns, harmony, currentTonality)!!
  }

  fun testNotate(list: LinkedList<Pattern>) {
    Notate(DataStorage.patternsToPhrase(list, Harmonizer().getHarmonyForMotive(this, MajorTonality(60)), MajorTonality(60)))
  }

  /** Returns first PNote from firstRunPattern
   * @return
   */
  val firstNote: PNote
    get() = firstRunPatterns.getFirst().firstNote

  fun applyRhythm(rhythm: List<Double>, listType: Int) {
    val list = getListByType(listType)
    //Tweaks notes lengths to make them look sound in lengths like in rhythm
    //Короче, накладывает ритмический рисунок на паттерны

    //Applied only if sizes are equal! And if there are more than 1 note
    if (list.size != rhythm.size || rhythm.size == 1) {
      return
    }

    var lastNoteAbsoluteLength = (list.first as Pattern).firstNote.absolute_length
    for (i in 1..rhythm.size - 1) {
      val curPattern = list[i] as Pattern
      //TODO: implement calculation for insides of cpattern
      curPattern.firstNote.length = rhythm[i] / lastNoteAbsoluteLength
      lastNoteAbsoluteLength = rhythm[i]
    }
  }

  private fun getListByType(listType: Int): LinkedList<*> {
    when (listType) {
      Motive.FIRST_RUN -> return firstRunPatterns

      Motive.FIRST_RUN_STABLE -> return firstRunStables

      Motive.SECOND_RUN -> return secondRunPatterns

      Motive.SECOND_RUN_STABLE -> return secondRunStables

      Motive.THIRD_RUN -> return thirdRunPatterns
    }
    return firstRunPatterns
  }

  fun setOverallVolume(volume: Int) {
    //TODO: Replace with third run patterns
    for (i in thirdRunPatterns.indices) {
      thirdRunPatterns[i].firstNote.volumeChange = volume
    }
  }

  companion object {

    //PatternArraysMarkers
    val FIRST_RUN = 0
    val FIRST_RUN_STABLE = 1
    val SECOND_RUN = 2
    val SECOND_RUN_STABLE = 3
    val THIRD_RUN = 4
    val THIRD_RUN_STABLE = 5
  }

}