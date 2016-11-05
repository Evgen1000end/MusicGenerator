package ru.demkin.core.record

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class RecordEvent: Comparable<RecordEvent> {

  var CHANGE_TONALITY_KEY = "tonalityKeyChanged"
  var CHANGE_TONALITY_TYPE = "tonalityTypeChanged" //0 - минор, 1 - мажор
  var SET_MELISMAS = "setMelismas"
  var SET_MELISMAS_CHANCE = "changeMelismaChance"
  var SET_TEMPO = "setTempo"
  var SET_OVERALL_VOLUME = "setOverallVolume"
  var ADD_INSTRUMENT = "addInstr"
  var REMOVE_INSTRUMENT = "removeInstr"
  var REMOVE_ALL_INSTRUMENTS = "removeAllInstr"
  var MUTE_ALL_INSTRUMENTS = "muteAllInstr"

  var SET_INSTRUMENT_TYPE = "setInstType"
  var SET_INSTRUMENT_ROLE = "setInstRole"
  var SET_INSTRUMENT_VOLUME = "setInstVolume"
  var SET_INSTRUMENT_OCTAVE = "setInstOctave"
  var SET_INSTRUMENT_MUTE = "setInstMute"

  private var type: String = ""
  private var value: String = ""
  private var instrumentId: Int = 0
  private var bar: Int = 0  //With blackjack and whores, of course!

  constructor(type: String, value: String, instrumentId: Int, bar: Int) {
    this.type = type
    this.value = value
    this.instrumentId = instrumentId
    this.bar = if (bar > 0) bar else 0
  }

  constructor(type: String, value: String, bar: Int) {
    this.type = type
    this.value = value
    this.bar = bar
    this.instrumentId = -1
  }


  fun getType(): String {
    return type
  }

  fun getValue(): String {
    return value
  }

  fun getInstrumentId(): Int {
    return instrumentId
  }

  fun getBar(): Int {
    return bar
  }

  override fun compareTo(o: RecordEvent): Int {
    if (this.bar < o.getBar())
      return -1
    else if (this.bar > o.getBar())
      return 1
    else if (this.instrumentId < o.getInstrumentId())
      return -1
    else if (this.instrumentId > o.getInstrumentId())
      return 1
    else if (this.type == ADD_INSTRUMENT && o.getType() != ADD_INSTRUMENT)
      return -1
    else if (this.type != ADD_INSTRUMENT && o.getType() == ADD_INSTRUMENT)
      return 1
    else
      return this.getValue().compareTo(o.getValue())
  }
}