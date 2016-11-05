package ru.demkin.core.data.tonality

import ru.demkin.core.data.chords.DimChord
import ru.demkin.core.data.chords.MajorChord
import ru.demkin.core.data.chords.MinorChord

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MajorTonality(pitch: Int) : Tonality(pitch, intArrayOf(0, 2, 4, 5, 7, 9, 11)) {

  init {
    this.degrees.put("I", MajorChord(pitch + intervals[0]))
    this.degrees.put("II", MinorChord(pitch + intervals[1]))
    this.degrees.put("III", MinorChord(pitch + intervals[2]))
    this.degrees.put("IV", MajorChord(pitch + intervals[3]))
    this.degrees.put("V", MajorChord(pitch + intervals[4]))
    this.degrees.put("VI", MinorChord(pitch + intervals[5]))
    this.degrees.put("VII", DimChord(pitch + intervals[6]))

    this.type = "major"
  }

}