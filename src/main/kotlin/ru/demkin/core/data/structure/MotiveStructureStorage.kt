package ru.demkin.core.data.structure

import java.io.*
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
object MotiveStructureStorage {

  var MOTIVES_STRUCTURES: LinkedList<MotiveStructure> = LinkedList<MotiveStructure>()

  fun init() {
    parseFileIntoList("database/MotivesStructures.txt", MOTIVES_STRUCTURES)
  }

  private fun parseFileIntoList(filename: String, list: LinkedList<MotiveStructure>) {
    val f = File(filename)
    val `in`: BufferedReader
    try {
      `in` = BufferedReader(FileReader(f))
      var inputLine: String? = null
      while ({inputLine = `in`.readLine(); inputLine}() != null) {
        if (inputLine!!.startsWith("//"))
        //Skip comments
          continue
        if (inputLine!!.startsWith("Name~"))
        //Start of new pattern; Parse till end
        {
          val name = inputLine!!.substring(inputLine!!.indexOf("~") + 1) //Get name
          var data = ""
          while (!{inputLine = `in`.readLine(); inputLine}()!!.startsWith("End")) {
            data = data + inputLine
          }
          val p = MotiveStructure(data)
          p.setName(name)
          list.add(p)
        }
      }
    } catch (e: FileNotFoundException) {
      e.printStackTrace()
    } catch (e: IOException) {
      e.printStackTrace()
    }

  }
}