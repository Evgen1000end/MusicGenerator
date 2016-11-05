package ru.demkin.core.data.patterns

import ru.demkin.core.data.PConstants
import ru.demkin.core.data.PNote
import java.util.*
import java.util.List

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
open class Pattern {
  var notes: LinkedList<PNote>? = null //All the notes and patterns inside this one
  var name: String? = null //Name of the pattern.

  constructor() {
  }

  constructor(_p: Pattern) {
    notes = LinkedList<PNote>()
    for (pn in _p.notes!!) {
      notes!!.add(PNote(pn))
    }
  }

  constructor(inputdata: String) {
    notes = LinkedList<PNote>()
    val rows = inputdata.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    for (s in rows)
      notes!!.add(PNote(s))
  }

  open fun multiplyAllLengths(multiplier: Double) {
    for (pnote in notes!!) {
      pnote.multiplyLength(multiplier)
    }
  }

  open fun multiplyFirstNoteLength(multiplier: Double) {
    if (notes != null && notes!!.size > 0)
      notes!![0].multiplyLength(multiplier)
  }

  override fun toString(): String {
    var result = "[Pattern]"
    for (pnote in notes!!) {
      result += pnote.toString()
    }
    return result
  }

  open val overallPatternJumpLength: Int
    get() {
      var overallJump = 0
      for (pnote in notes!!) {
        if (pnote.transitionType !== PConstants.COMPLEX) {
          overallJump += PConstants.PATTERN_WEIGHTS[pnote.transitionType] * pnote.transitionValue!![0]
        } else {
          overallJump += PConstants.PATTERN_WEIGHTS[PConstants.CHORD_JUMP] * pnote.transitionValue!![0]
          overallJump += PConstants.PATTERN_WEIGHTS[PConstants.TONALITY_JUMP] * pnote.transitionValue!![1]
        }
      }
      return overallJump
    }

  open val patternRelativeLength: Double
    get() {
      var relativeLength = 0.0
      var prevLength = 1.0
      for (pnote in notes!!) {
        relativeLength += prevLength * pnote.length
        prevLength = prevLength * pnote.length
      }
      return relativeLength
    }

  open val firstNote: PNote
    get() = notes!![0]

  open fun size(): Int {
    return notes!!.size
  }
}