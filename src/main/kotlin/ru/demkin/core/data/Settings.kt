package ru.demkin.core.data

import jm.music.data.Tempo
import ru.demkin.core.data.tonality.MajorTonality
import ru.demkin.core.data.tonality.MinorTonality
import ru.demkin.core.data.tonality.Tonality

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
object Settings {

  var CURRENT_TONALITY: Tonality? = null
  var TEMPO: Tempo? = null
  var USE_MELISMAS = false
  var MELISMAS_CHANCE = 5
  var OVERALL_VOLUME = 100

  var accompanimentBarCounter = 0 //Считает такты. На каждом четвертом такте у всех абсолютно аккомпанементов меняется структура.


  fun setTonalityPitch(tonalityKey: Int) {
    //TODO: REFACTOR!!!
    if (CURRENT_TONALITY!!.type.equals("major")) {
      CURRENT_TONALITY = MajorTonality(tonalityKey + 60)
    } else if (CURRENT_TONALITY!!.type.equals("minor")) {
      CURRENT_TONALITY = MinorTonality(tonalityKey + 60)
    }
  }

  fun setTonalityType(key: String) {
    //TODO: REFACTOR!!!
    if (key == "major") {
      CURRENT_TONALITY = MajorTonality(CURRENT_TONALITY!!.pitch)
    } else if (key == "minor") {
      CURRENT_TONALITY = MinorTonality(CURRENT_TONALITY!!.pitch)
    }
  }

}