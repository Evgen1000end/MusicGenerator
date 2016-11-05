package ru.demkin.core.data

import java.util.HashMap
import java.util.LinkedList
import java.util.Random

import jm.music.data.Phrase
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.structure.MotiveStructureStorage
import ru.demkin.core.data.structure.PhraseStructure
import ru.demkin.core.data.tonality.Tonality
import ru.demkin.core.generators.BaseVoiceGenerator
import ru.demkin.core.generators.MotivesGenerator
import ru.demkin.core.generators.multigen.AccompanimentGenerator
import ru.demkin.core.generators.multigen.SecondVoiceGenerator
import ru.demkin.utils.octaveIndexToShift

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class Voice {
  var role: Int = 0
  var name: String = ""
  var channel: Int = 0
  //public int 					instrument;
  var volume: Int = 0
  var octaveSummand: Int = 0
  private var pinstrument: PInstrument = DataStorage.getInstrumentByName("Piano")!!
  private var voiceGenerator: BaseVoiceGenerator? = null
  var motive: Motive? = null
    private set
  private var lastNote: Int = 0
  var isMuted: Boolean = false

  private var allPhraseStructures: LinkedList<PhraseStructure>? = null

  /**
   * @param role
   * *
   * @param name
   * *
   * @param instrument
   * *
   * @param channel
   * *
   * @param volume
   * *
   * @param octaveSummand
   */
  //	public Voice(
  //			int role,
  //			String name,
  //			int instrument,
  //			int channel,
  //			int volume,
  //			int octaveSummand) {
  //		initVoice(role, name, instrument, channel, volume, octaveSummand);
  //	}

  /**
   * @param role
   * *
   * @param name
   * *
   * @param pInstrument
   * *
   * @param channel
   * *
   * @param volume
   */
  constructor(
          role: Int,
          name: String,
          pInstrument: PInstrument,
          channel: Int,
          volume: Int) {
    initVoice(role, name, pInstrument, channel, volume, 0)
  }

  /**
   * @param role
   * *
   * @param name
   * *
   * @param instrument
   * *
   * @param channel
   */
  constructor(
          role: Int,
          name: String,
          instrument: PInstrument,
          channel: Int) {
    initVoice(role, name, instrument, channel, 100, 0)
  }

  constructor() {
    initVoice(MELODY, "Main Melody", DataStorage.getInstrumentByName("Piano")!!, 1, 0, 100)
  }

  private fun initVoice(
          role: Int,
          name: String,
          instrument: PInstrument,
          channel: Int,
          volume: Int,
          octaveSummand: Int) {
    this.role = role
    this.name = name
    this.pinstrument = instrument
    this.channel = channel
    this.volume = volume
    this.octaveSummand = octaveIndexToShift(instrument.octaveIndex)
    this.isMuted = false
    allPhraseStructures = LinkedList<PhraseStructure>()

    updateRole()
  }

  val nextPhrase: Phrase?
    get() = null

  /** Generates a motive (main melody)
   * @param pitch
   * *
   * @param cT
   */
  fun generateMotive(cT: Tonality) {

    //If there are no phrases at all, or the last phrase is finished
    if (allPhraseStructures!!.size == 0 || allPhraseStructures!!.getLast().hasAllMotives()) {
      //get random motive structure
      val mStructure = MotiveStructureStorage.MOTIVES_STRUCTURES.get(Random().nextInt(MotiveStructureStorage.MOTIVES_STRUCTURES.size))
      //create new phraseStructure
      allPhraseStructures!!.add(PhraseStructure(mStructure))
      println("Multi Voice Generator > getScore() > New Phrase Structure created! Phrase: " + allPhraseStructures!!.getLast().toString())
    }

    val currentPStructure = allPhraseStructures!!.getLast()

    when (currentPStructure.requiredGenerationType) {
      PhraseStructure.MOTIVE_EXISTS -> motive = currentPStructure.existingMotive

      PhraseStructure.NEW_MOTIVE -> {
        motive = (voiceGenerator as MotivesGenerator).generateMotive(lastNote, cT, octaveSummand)
        currentPStructure.addMotive(motive!!)
      }

      PhraseStructure.SIMILAR_MOTIVE -> {
        motive = (voiceGenerator as MotivesGenerator).generateSimilarMotive(currentPStructure.motiveForSimilar, lastNote, cT, octaveSummand)
        currentPStructure.addMotive(motive!!)
      }

      PhraseStructure.LAST_BAR -> {
        motive = (voiceGenerator as MotivesGenerator).generateLastBarMotive(lastNote, cT, octaveSummand)
        currentPStructure.addMotive(motive!!)
      }

      else -> motive = (voiceGenerator as MotivesGenerator).generateMotive(lastNote, cT, octaveSummand)
    }

    setMotiveVolume(volume)

    currentPStructure.iterate() //Iterate pstructure, so next time it will work with another motive

  }


  /** Generates motive (second voice & accompaniment)
   * @param harmony
   * *
   * @param currentTonality
   */
  fun generateMotive(harmony: HashMap<Double, Chord>, currentTonality: Tonality) {
    when (role) {
      MELODY -> {
      }

      SECOND_VOICE -> motive = (voiceGenerator as SecondVoiceGenerator).generateMotive(harmony, currentTonality, octaveSummand)

      ACCOMPANIMENT -> motive = (voiceGenerator as AccompanimentGenerator).generateMotive(harmony, currentTonality, octaveSummand, lastNote)

      else -> voiceGenerator = MotivesGenerator()
    }//Do nothing. For main melody motive already exists

    setMotiveVolume(volume)
  }

  fun getPhrase(harmony: HashMap<Double, Chord>, currentTonality: Tonality): Phrase {
    return motive!!.getPhrase(harmony, currentTonality)
  }

  fun setLastNote(lastNote: Int) {
    this.lastNote = lastNote
  }

  fun setMotiveVolume(volume: Int) {
    motive!!.setOverallVolume((volume * (Settings.OVERALL_VOLUME as Double / 100)).toInt())
  }

  fun updateRole() {
    when (role) {
      MELODY -> voiceGenerator = MotivesGenerator()

      SECOND_VOICE -> voiceGenerator = SecondVoiceGenerator()

      ACCOMPANIMENT -> voiceGenerator = AccompanimentGenerator()

      else -> voiceGenerator = MotivesGenerator()
    }
    voiceGenerator!!.setPossiblePatterns(pinstrument)
  }

  fun setPinstrument(_pinstrument: PInstrument) {
    this.pinstrument = _pinstrument
    this.octaveSummand = _pinstrument.octaveIndex
    this.voiceGenerator!!.setPossiblePatterns(_pinstrument)
  }

  fun refreshAccompanimentPattern() {
    if (voiceGenerator is AccompanimentGenerator)
      (voiceGenerator as AccompanimentGenerator).setCurrentRandomPattern()
  }

  companion object {

    val MELODY = 0
    val SECOND_VOICE = 1
    val ACCOMPANIMENT = 2
  }
}