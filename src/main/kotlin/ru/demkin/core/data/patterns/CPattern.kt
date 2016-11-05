package ru.demkin.core.data.patterns

import ru.demkin.core.data.PNote
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class CPattern : Pattern {

  private var patterns: LinkedList<Pattern>? = null

  constructor(inputdata: String) : super(inputdata) {
    patterns = LinkedList<Pattern>()
    initNotes()
  }

  constructor(_patterns: LinkedList<Pattern>) {
    patterns = _patterns
    initNotes()
  }


  constructor() {
    patterns = LinkedList<Pattern>()
    initNotes()
  }

  constructor(cp: CPattern) {
    patterns = LinkedList<Pattern>()

    for (p in cp.getPatterns()) {
      patterns!!.add(Pattern(p))
    }

    initNotes()
  }

  fun addPattern(p: Pattern) {
    if (patterns == null)
      patterns = LinkedList<Pattern>()

    patterns!!.add(p)
    initNotes()
  }

  fun initNotes() {
    notes = LinkedList<PNote>()
    for (p in patterns!!) {
      notes!!.addAll(p.notes!!)
    }
  }

  override fun multiplyAllLengths(multiplier: Double) {
    for (p in patterns!!) {
      p.multiplyAllLengths(multiplier)
    }
    initNotes()
  }

  override fun multiplyFirstNoteLength(multiplier: Double) {
    if (patterns != null && patterns!!.size > 0) {
      patterns!![0].multiplyFirstNoteLength(multiplier)
    }
  }

  override val overallPatternJumpLength: Int
    get() {
      var overallJump = 0
      for (p in patterns!!) {
        overallJump += p.overallPatternJumpLength
      }
      return overallJump
    }

  fun getPatterns(): LinkedList<Pattern> {
    return patterns!!
  }

  fun sizePatterns(): Int {
    return patterns!!.size
  }

  override fun size(): Int {
    var totalSize = 0
    for (p in patterns!!) {
      totalSize += p.size()
    }
    return totalSize
  }

}