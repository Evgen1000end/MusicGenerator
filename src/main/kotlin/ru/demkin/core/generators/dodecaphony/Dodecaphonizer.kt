package ru.demkin.core.generators.dodecaphony

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class Dodecaphonizer(notes: IntArray) {
  val pToneRow: IntArray
  val iToneRow: IntArray
  val rToneRow: IntArray
  val irToneRow: IntArray
  private val pitch: Int

  init {
    this.pitch = notes[0]
    pToneRow = IntArray(notes.size)
    iToneRow = IntArray(notes.size)
    rToneRow = IntArray(notes.size)
    irToneRow = IntArray(notes.size)
    for (i in notes.indices)
      pToneRow[i] = notes[i] - pitch
    for (i in pToneRow.indices) {
      rToneRow[i] = pToneRow[pToneRow.size - i - 1]
      iToneRow[i] = pToneRow[i] * -1
      irToneRow[i] = pToneRow[pToneRow.size - i - 1] * -1
    }
    //printIntArray(PToneRow);
    //printIntArray(IToneRow);
    //printIntArray(RToneRow);
    //printIntArray(IRToneRow);
  }

  fun getRandomRowPitched(p: Int): IntArray {
    val rnd = Math.random()
    val tempRow = IntArray(pToneRow.size)
    for (i in tempRow.indices) {
      if (rnd < 0.25)
        tempRow[i] = pToneRow[i] + p
      else if (rnd >= 0.25 && rnd < 0.5)
        tempRow[i] = rToneRow[i] + p
      else if (rnd >= 0.5 && rnd < 0.75)
        tempRow[i] = iToneRow[i] + p
      else
        tempRow[i] = irToneRow[i] + p
    }
    return tempRow
  }

  companion object {
    fun printIntArray(arr: IntArray) {
      println(arr.map { it.toString() }.reduce { s1, s2 -> s1+" "+s2 })
    }

    fun printDoubleArray(arr: DoubleArray) {
      println(arr.map { it.toString() }.reduce { s1, s2 -> s1+" "+s2 })
    }
  }
}