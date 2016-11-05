package ru.demkin.core.data.chords


import java.util.Arrays
/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
abstract class Chord(pitch: Int, chord: IntArray) {
  protected var pitch: Int = 0
  var chord: IntArray
    protected set
  var allChordNotes: IntArray
    protected set

  init {
    this.pitch = pitch % 12
    this.chord = IntArray(chord.size)
    for (i in chord.indices)
      this.chord[i] = (chord[i] + pitch) % 12
    Arrays.sort(this.chord)

    //На входе имеем массив chord виде [1,4,9] (в случае ля-мажорного аккорда)
    //По нему строим массив всех возможных нот, т.е. ми, додиезов и ля во всех возможных октавах
    //И в итоге получаем allChordNotes вида

    this.allChordNotes = IntArray(9 * chord.size)
    for (i in 0..8) {
      for (j in chord.indices) {
        allChordNotes[i * chord.size + j] = 12 + i * 12 + chord[j]
      }
    }
  }

  override fun toString(): String {
    return "[" + pitch + " " + Arrays.toString(chord) + "]"
  }
}