package ru.demkin.core.data

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class Melisma {
  var pitchValue: Int = 0
  var type: Int = 0

  constructor(type: Int, pitchValue: Int) : super() {
    this.pitchValue = pitchValue
    this.type = type
  }

  constructor(m: Melisma) {
    this.pitchValue = m.pitchValue
    this.type = m.type
  }

  override fun toString(): String {
    return "[Melisma." +
            (if (pitchValue != 0) " Pitch value = $pitchValue;" else "") +
            (if (type != 0) " Type = " + type else "") + "]\n"
  }

  companion object {
    val GRACE = 0
    val TRILL = 1
  }
}