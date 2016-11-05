package ru.demkin.core.data.structure

import ru.demkin.core.data.Motive
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class PhraseStructure(private val motiveStructure: MotiveStructure) {

  private val motives: HashMap<String, Motive>
  private var currMotiveType: String? = null
  private var iterator: Int = 0

  init {
    motives = HashMap<String, Motive>()
    iterator = 0
    currMotiveType = motiveStructure.motiveTypes.get(iterator)
  }

  /** Returns null if current motiveType doesn't exit
   * @return
   */
  //if it's almost end - return last bar type
  //if hashmap has exact same
  //return element
  //if has same without commas
  //call generateSimilarMotive based on elementwithout commas
  //call generateMotive
  val requiredGenerationType: Int
    get() {
      if (iterator == motiveStructure.motiveTypes.size - 1) {
        return LAST_BAR
      }
      if (motives.containsKey(currMotiveType)) {
        return MOTIVE_EXISTS
      } else {
        val withoutQuotes = if (currMotiveType!!.contains("'")) currMotiveType!!.substring(0, currMotiveType!!.indexOf("'")) else currMotiveType
        if (motives.containsKey(withoutQuotes)) {
          return SIMILAR_MOTIVE
        } else {
          return NEW_MOTIVE
        }
      }
    }

  val motiveForSimilar: Motive
    get() {
      val withoutQuotes = if (currMotiveType!!.contains("'")) currMotiveType!!.substring(0, currMotiveType!!.indexOf("'")) else currMotiveType
      return motives.get(withoutQuotes)!!
    }

  val existingMotive: Motive
    get() = motives.get(currMotiveType)!!

  fun addMotive(m: Motive) {
    motives.put(currMotiveType!!, m)
  }


  /**
   * @return
   */
  fun iterate() {
    iterator++
    if (iterator != motiveStructure.motiveTypes.size)
      currMotiveType = motiveStructure.motiveTypes.get(iterator)
  }

  fun hasAllMotives(): Boolean {
    return iterator == motiveStructure.motiveTypes.size
  }

  override fun toString(): String {
    return "[" + "Structure:" + motiveStructure.toString() + " Hashmap:" + motives.toString() + "]"
  }

  companion object {

    val MOTIVE_EXISTS = 0
    val NEW_MOTIVE = 1
    val SIMILAR_MOTIVE = 2
    val LAST_BAR = 3
  }
}