package ru.demkin.core.data

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class PNote {

  private var pitch: Int = 0 //Note pitch from 0 to 128

  var absolute_length: Double = 0.toDouble()
    private set //Absolute length. Used only in first note. Otherwise is 0
  var length: Double = 0.toDouble() //Relative length. Used for every note.

  var transitionType: Int = 0
    private set //Type of transition between this and previous note. Found in PConstants.

  var transitionValue: IntArray? = null
    private set //Values for transition. 0 for SAME_PITCH


  var volumeChange: Int = 0 //Decreasement/increasement in volume. TODO: Implement

  var rhythmValue: Double = 0.toDouble() //Position inside a bar. 0 - then element is beginning of bar; -1  = not attached to bar

  var melismas: Melisma? = null //Melismas. TODO: replace with lists of melismas

  constructor(data: String) {
    val values = data.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    transitionType = if (values.size > 0) Integer.parseInt(values[0]) else PConstants.STABLE_NOTE
    if (transitionType == PConstants.COMPLEX) {
      transitionValue = parseComplexTransitionValues(values[1])
    } else {
      transitionValue = intArrayOf(Integer.parseInt(values[1]))
    }

    length = if (values.size > 2) java.lang.Double.parseDouble(values[2]) else 1.0
    rhythmValue = (if (values.size > 3) Integer.parseInt(values[3]) else 0).toDouble()
    //pitch 			= (values.length > 4) ? Integer.parseInt(values[4]) : 0;
    //absolute_length = (values.length > 5) ? Integer.parseInt(values[5]) : JMC.QUARTER_NOTE;
    volumeChange = 0
    melismas = null
  }

  private fun parseComplexTransitionValues(s: String): IntArray {
    val vals = s.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val result = IntArray(vals.size)
    for (i in vals.indices)
      result[i] = Integer.parseInt(vals[i])
    return result
  }

  constructor() {

  }

  constructor(_pitch: Int, _length: Double) {
    this.pitch = _pitch
    this.absolute_length = _length
    this.length = 1.0
    this.transitionType = PConstants.STABLE_NOTE
    this.transitionValue = intArrayOf(0)
    this.volumeChange = 100
    this.rhythmValue = 0.0
    this.melismas = null
  }

  constructor(_p: PNote) {
    this.pitch = _p.getPitch()
    this.absolute_length = _p.absolute_length
    this.length = _p.length
    this.transitionType = _p.transitionType
    this.transitionValue = _p.transitionValue
    this.volumeChange = _p.volumeChange
    this.rhythmValue = _p.rhythmValue
    this.melismas = null
  }

  constructor(pitch2: Int, length2: Double, volume: Int) {
    this.pitch = pitch2
    this.absolute_length = length2
    this.length = 1.0
    this.transitionType = PConstants.STABLE_NOTE
    this.transitionValue = intArrayOf(0)
    this.volumeChange = volume
    this.rhythmValue = 0.0
    this.melismas = null
  }

  fun getPitch(): Int {
    return pitch
  }

  fun setPitch(pitch: Int) {
    this.pitch = pitch

    if (pitch <= 0) {
      println("PNote > setPitch > Invalid pitch value! Pitch = " + pitch)
    }
  }

  fun hasMelismas(): Boolean {
    return melismas != null
  }

  fun multiplyLength(multiplier: Double) {
    this.length = this.length * multiplier
  }

  override fun toString(): String {
    return "[Pnote." +
            (if (pitch != 0) " Pitch = $pitch;" else "") +
            (if (absolute_length != 0.0) " Absolute Length = $absolute_length;" else "") +
            " Length = " + length + ";" +
            " Transition Type = " + PConstants.getTransitionName(transitionType) + ";" +
            " Transition Value = " + transitionValue + ";" +
            (if (rhythmValue != 0.0) " Rhythm value = $rhythmValue;" else "") +
            (if (volumeChange != 0) " Volume change = $volumeChange;" else "") +
            (if (melismas != null) " Melisma = " + melismas!!.type else "") + "]\n"
  }

  val volume: Int
    get() {
      if (this.transitionType == PConstants.STABLE_NOTE)
        return this.volumeChange
      else
        return 100
    }
}