package ru.demkin.core.data.structure

import java.util.*
import java.util.regex.Pattern

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class MotiveStructure(inputData: String) {

  val motiveTypes: LinkedList<String>
  private var name: String? = null

  init {
    motiveTypes = LinkedList<String>()
    val MOTIVE_PATTERN = Pattern.compile("\\w[']*")
    val m = MOTIVE_PATTERN.matcher(inputData)
    while (m.find()) {
      val s = m.group()
      motiveTypes.add(s)
    }
  }

  fun setName(name: String) {
    this.name = name
  }

  override fun toString(): String {
    return "[" + name + " " + motiveTypes.toString() + "]"
  }

  /** Returns size of motiveTypes list.
   * @return
   */
  fun size(): Int {
    return motiveTypes.size
  }

}