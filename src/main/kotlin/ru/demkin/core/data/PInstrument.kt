package ru.demkin.core.data

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class PInstrument {
  var name: String
  var RussianName: String
  var imageName: String
  var midiId: Int = 0
  var octaveIndex: Int = 0

  constructor(name: String, russianName: String, imageName: String,
              midiId: Int, octIndex: Int) : super() {
    this.name = name
    RussianName = russianName
    this.imageName = imageName
    this.midiId = midiId
    this.octaveIndex = octIndex
  }

  constructor(name: String, russianName: String, midiId: Int, octIndex: Int) : super() {
    this.name = name
    RussianName = russianName
    this.imageName = name
    this.midiId = midiId
    this.octaveIndex = octIndex
  }

  override fun toString(): String {
    return "PInstrument [name=$name, midiId=$midiId"+", octaveIndex=" + octaveIndex + "]"
  }


}