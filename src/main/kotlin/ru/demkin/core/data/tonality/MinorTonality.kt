package ru.demkin.core.data.tonality

import ru.demkin.core.data.chords.DimChord
import ru.demkin.core.data.chords.MajorChord
import ru.demkin.core.data.chords.MinorChord

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MinorTonality(pitch: Int) : Tonality(pitch, intArrayOf(0, 2, 3, 5, 7, 8, 10)) {

  init {
    this.degrees.put("I", MinorChord(pitch + intervals[0]))
    this.degrees.put("II", DimChord(pitch + intervals[1]))
    this.degrees.put("III", MajorChord(pitch + intervals[2]))
    this.degrees.put("IV", MinorChord(pitch + intervals[3]))
    this.degrees.put("V", MinorChord(pitch + intervals[4]))
    this.degrees.put("VI", MajorChord(pitch + intervals[5]))
    this.degrees.put("VII", MajorChord(pitch + intervals[6]))

    this.type = "minor"
  }
}