package ru.demkin.core.data.tonality

import ru.demkin.core.data.chords.Chord
import ru.demkin.utils.getElementInArrayByStep
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
open class Tonality(pitch: Int, var intervals: IntArray //Main intervals for this tonality. 0, 2, 4, etc..
) {
  var pitch: Int = 0
    protected set
  protected var allTonalitySteps: LinkedList<Int>

  protected var degrees: HashMap<String, Chord>
  var type: String
    protected set

  init {
    this.pitch = pitch
    this.type = "nontonal"

    degrees = HashMap<String, Chord>()


    allTonalitySteps = LinkedList<Int>()
    val absolute_pitch = pitch % 12 //Get pitch value from 0 to 11 - to start from.
    for (i in 0..9) {
      for (j in intervals.indices) {
        allTonalitySteps.add(intervals[j] + absolute_pitch + 12 * i)
      }
    }
  }

  /** Returns either I note or V note of tonality

   */
  val randomMainNotePitched: Int
    get() {
      if (Math.random() > 0.7)
        return pitch
      else
        return pitch + intervals[4]
    }

  /** Returns pitch of note which is at a distance of `step` from `noteValue`. E.g. [2, 60] for C-maj tonality will return 64. [2,61] => 64. [2,62] => 65.
   * @param step Step - how many degrees we should leave before reaching required note
   * *
   * @param noteValue Starting point - note pitch.
   * *
   * @return
   */
  fun getTonalityNoteByStep(step: Int, noteValue: Int): Int {
    val array = IntArray(allTonalitySteps.size)
    for (i in allTonalitySteps.indices) {
      array[i] = allTonalitySteps[i]
    }
    return getElementInArrayByStep(step, array, noteValue)
  }

  /** Returns chord as int[] by degree name
   * @param deg Degree Name
   * *
   * @return Chord as int[]
   */
  fun getChordByDegree(deg: String): Chord {
    return degrees[deg]!!
  }

  val randomDegreeChord: Chord
    get() {
      val generator = Random()
      val values = degrees.values.toTypedArray()
      val randomValue = values[generator.nextInt(values.size)]
      return randomValue as Chord
    }

  fun getPichedScale(scaleId: Int): Int {
    return intervals[scaleId] + pitch
  }


  fun switchTonality(id: Int) {
    // TODO Implement

  }
}