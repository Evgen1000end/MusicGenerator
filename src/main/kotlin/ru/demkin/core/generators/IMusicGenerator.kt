package ru.demkin.core.generators

import jm.music.data.Score
import ru.demkin.core.data.Voice

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
interface IMusicGenerator {

  fun init()
  val score: Score
  fun addVoice(v: Voice)
  fun removeVoice(v: Voice)
}