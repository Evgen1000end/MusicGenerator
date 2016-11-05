package ru.demkin.core.generators

import jm.JMC
import ru.demkin.core.data.Motive
import ru.demkin.core.data.PInstrument
import ru.demkin.core.data.patterns.CPattern
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.tonality.Tonality
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
open class BaseVoiceGenerator {

  protected var currentMotive: Motive? = null
  protected var currentTonality: Tonality? = null
  protected var rhythmLengths: LinkedList<List<Double>>
  protected var possiblePatterns: LinkedList<Pattern>? = null

  init {
    rhythmLengths = LinkedList<List<Double>>()
    //rhythmLengths.add(Arrays.asList(JMC.WHOLE_NOTE)); 														// O
    rhythmLengths.add(Arrays.asList(JMC.HALF_NOTE, JMC.HALF_NOTE))                        // o + o
    rhythmLengths.add(Arrays.asList(JMC.HALF_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE))            // o + . + .
    rhythmLengths.add(Arrays.asList(JMC.DOTTED_HALF_NOTE, JMC.QUARTER_NOTE))                  // o. + .
    rhythmLengths.add(Arrays.asList(JMC.QUARTER_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE, JMC.QUARTER_NOTE))  // . + . + . + .
  }

  fun generateMotive(vararg arguments: Any): Motive? {
    println("Base Motive Generator > Error! Method generateMotive() is unimplemented!")
    return null
  }

  open fun setPossiblePatterns(instrument: PInstrument) {
    possiblePatterns = null
  }

  protected fun duplicatePatternsList(oldList: LinkedList<Pattern>, newList: LinkedList<Pattern>) {
    for (p in oldList) {
      newList.add(Pattern(p))
    }
  }

  protected fun duplicateCPatternsList(oldList: LinkedList<CPattern>, newList: LinkedList<CPattern>) {
    for (p in oldList) {
      newList.add(CPattern(p))
    }
  }

  protected fun duplicatePatternsListAsComplexPattern(oldList: LinkedList<*>, newList: LinkedList<Pattern>) {
    for (p in oldList) {
      val newPattern = CPattern()
      newPattern.addPattern(Pattern(p as Pattern))
      newList.add(newPattern)
    }
  }

}