package ru.demkin.core.data

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
object PConstants {
  val CHROMATIC_JUMP = 0
  val TONALITY_JUMP = 1
  val CHORD_JUMP = 2
  val SAME_PITCH = 3
  val STABLE_NOTE = 4
  val COMPLEX = 5 //Первая часть - прыжок по аккорду, вторая - по тональности;

  val PATTERN_WEIGHTS = intArrayOf(1, 2, 4, 0, 0)
  fun getTransitionName(type: Int): String {
    when (type) {
      0 -> return "Chromatic Jump"
      1 -> return "Tonality Jump"
      2 -> return "Chord Jump"
      3 -> return "Same pitch"
      4 -> return "Stable note"
      else -> return "Unknown jump"
    }
  }
}