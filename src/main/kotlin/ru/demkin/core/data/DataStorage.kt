package ru.demkin.core.data

import jm.JMC
import jm.music.data.Note
import jm.music.data.Phrase
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.Tonality
import ru.demkin.utils.getElementInArrayByStep
import ru.demkin.utils.nearestKey
import java.io.*
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
object DataStorage {
  var SIMPLE_PATTERNS= LinkedList<Pattern>()
  var SIMPLE_TONAL_PATTERNS = LinkedList<Pattern>()
  var SIMPLE_CHORD_PATTERNS = LinkedList<Pattern>()
  var SIMPLE_CHROMATIC_PATTERNS = LinkedList<Pattern>()
  var SIMPLE_SAME_PATTERNS = LinkedList<Pattern>()
  var SIMPLE_PATTERNS_FOR_MOTIVE = LinkedList<Pattern>()
  var BASE_ACCOMPANIMENT_PATTERNS = LinkedList<Pattern>()
  var INSTRUMENTS_ACCOMPANIMENTS = HashMap<PInstrument, LinkedList<Pattern>>()

  var SECOND_VOICE_PATTERNS = LinkedList<Pattern>()
  var MOVEMENT_PATTERNS = LinkedList<Pattern>()
  var MOVEMENT_PATTERNS_VALUED = HashMap<Int, LinkedList<Pattern>>()
  var INSTRUMENTS = LinkedList<PInstrument>()
  var MELISMAS = LinkedList<Melisma>()

  fun init() {

    parseFileIntoList("database/SimplePatterns.txt", SIMPLE_PATTERNS)

    for (p in SIMPLE_PATTERNS) {
      if (p.name!!.toLowerCase().contains("same"))
        SIMPLE_SAME_PATTERNS.add(p)

      if (p.name!!.toLowerCase().contains("chord"))
        SIMPLE_CHORD_PATTERNS.add(p)

      if (p.name!!.toLowerCase().contains("chromatic"))
        SIMPLE_CHROMATIC_PATTERNS.add(p)

      if (p.name!!.toLowerCase().contains("tonal"))
        SIMPLE_TONAL_PATTERNS.add(p)
    }

    //SIMPLE_PATTERNS_FOR_MOTIVE.addAll(SIMPLE_CHROMATIC_PATTERNS);
    SIMPLE_PATTERNS_FOR_MOTIVE.addAll(SIMPLE_TONAL_PATTERNS)
    SIMPLE_PATTERNS_FOR_MOTIVE.addAll(SIMPLE_SAME_PATTERNS)


    parseFileIntoList("database/MovementPatterns.txt", MOVEMENT_PATTERNS)

    for (i in MOVEMENT_PATTERNS) {
      val p = Pattern(i)
      val patternJump = p.overallPatternJumpLength
      if (MOVEMENT_PATTERNS_VALUED.containsKey(patternJump)) {
        MOVEMENT_PATTERNS_VALUED[patternJump]!!.add(p)
      } else {
        val newList = LinkedList<Pattern>()
        newList.add(p)
        MOVEMENT_PATTERNS_VALUED.put(patternJump, newList)
      }
    }


    parseFileIntoList("database/accompaniments/BaseAccompanimentPatterns.txt", BASE_ACCOMPANIMENT_PATTERNS)


    parseFileIntoList("database/SecondVoicePatterns.txt", SECOND_VOICE_PATTERNS)


    MELISMAS.add(Melisma(Melisma.GRACE, -1))
    MELISMAS.add(Melisma(Melisma.GRACE, 1))

    MELISMAS.add(Melisma(Melisma.GRACE, -2))
    MELISMAS.add(Melisma(Melisma.GRACE, 2))

    MELISMAS.add(Melisma(Melisma.TRILL, -1))
    MELISMAS.add(Melisma(Melisma.TRILL, 1))

    //Filling list of available instruments

    INSTRUMENTS.add(PInstrument("Piano", "Фортепиано", JMC.PIANO, 3))
    INSTRUMENTS.add(PInstrument("Harpsichord", "Клавесин", JMC.HARPSICHORD, 3))
    INSTRUMENTS.add(PInstrument("Organ", "Орган", JMC.CHURCH_ORGAN, 2))
    INSTRUMENTS.add(PInstrument("Vibraphone", "Вибрафон", JMC.VIBES, 4))
    INSTRUMENTS.add(PInstrument("Xylophone", "Ксилофон", JMC.XYLOPHONE, 4))
    INSTRUMENTS.add(PInstrument("Glockenspiel", "Колокольчики", JMC.GLOCKENSPIEL, 4))
    INSTRUMENTS.add(PInstrument("Chimes", "Колокола", JMC.TUBULAR_BELL, 2))
    INSTRUMENTS.add(PInstrument("Harp", "Арфа", JMC.HARP, 3))
    INSTRUMENTS.add(PInstrument("Violin", "Скрипка", JMC.VIOLIN, 4))
    INSTRUMENTS.add(PInstrument("Viola", "Альт", JMC.VIOLA, 3))
    INSTRUMENTS.add(PInstrument("Cello", "Виолончель", JMC.CELLO, 2))
    INSTRUMENTS.add(PInstrument("Contrabass", "Контрабасс", JMC.DOUBLE_BASS, 1))
    INSTRUMENTS.add(PInstrument("Strings", "Струнные", JMC.STRING_ENSEMBLE_1, 3))
    INSTRUMENTS.add(PInstrument("Guitar", "Гитара", JMC.GUITAR, 3))
    INSTRUMENTS.add(PInstrument("Flute", "Флейта", JMC.FLUTE, 4))
    INSTRUMENTS.add(PInstrument("Clarinet", "Кларнет", JMC.CLARINET, 3))
    INSTRUMENTS.add(PInstrument("Oboe", "Гобой", JMC.OBOE, 3))
    INSTRUMENTS.add(PInstrument("Bassoon", "Фагот", JMC.BASSOON, 2))
    INSTRUMENTS.add(PInstrument("Bagpipe", "Волынка", JMC.BAGPIPE, 3))
    INSTRUMENTS.add(PInstrument("Shakuhachi", "Сякухати", JMC.SHAKUHACHI, 3))
    INSTRUMENTS.add(PInstrument("Saxophone", "Саксофон", JMC.SAX, 2))
    INSTRUMENTS.add(PInstrument("Trumpet", "Труба", JMC.TRUMPET, 3))
    INSTRUMENTS.add(PInstrument("Trombone", "Тромбон", JMC.TROMBONE, 2))
    INSTRUMENTS.add(PInstrument("Tuba", "Туба", JMC.TUBA, 2))
    INSTRUMENTS.add(PInstrument("Choir", "Хор", JMC.AAH, 3))

    //		INSTRUMENTS.add(new PInstrument("88", "88", JMC.FANTASIA, 3));
    //		INSTRUMENTS.add(new PInstrument("89", "89", 89, 3));
    //		INSTRUMENTS.add(new PInstrument("90", "90", 90, 3));
    //		INSTRUMENTS.add(new PInstrument("91", "91", 91, 3));
    //		INSTRUMENTS.add(new PInstrument("92", "92", 92, 3));
    //		INSTRUMENTS.add(new PInstrument("93", "93", 93, 3));
    //		INSTRUMENTS.add(new PInstrument("94", "94", 94, 3));
    //		INSTRUMENTS.add(new PInstrument("95", "95", 95, 3));
    //		INSTRUMENTS.add(new PInstrument("96", "96", 96, 3));
    //		INSTRUMENTS.add(new PInstrument("97", "97", 97, 3));
    //		INSTRUMENTS.add(new PInstrument("98", "98", 98, 3));
    //		INSTRUMENTS.add(new PInstrument("99", "99", 99, 3));
    //		INSTRUMENTS.add(new PInstrument("100", "100", 100, 3));
    //		INSTRUMENTS.add(new PInstrument("101", "101", 101, 3));
    //		INSTRUMENTS.add(new PInstrument("102", "102", 102, 3));
    //		INSTRUMENTS.add(new PInstrument("103", "103", 103, 3));



    for (pi in INSTRUMENTS) {
      addInstrumentAccompanimentPattern(pi)
    }

  }

  private fun addInstrumentAccompanimentPattern(instrument: PInstrument) {
    val list = LinkedList<Pattern>()
    parseFileIntoList("database/accompaniments/" + instrument.name + "Patterns.txt", list)
    INSTRUMENTS_ACCOMPANIMENTS.put(instrument, list)
  }

  fun getInstrumentByName(name: String): PInstrument? {
    for (pi in INSTRUMENTS) {
      if (pi.name.equals(name))
        return pi
    }
    return null
  }

  private fun parseFileIntoList(filename: String, list: LinkedList<Pattern>) {
    val f = File(filename)
    if (!f.exists())
      return

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
            data = data + inputLine + ";"
          }
          val p = Pattern(data)
          p.name = name
          list.add(p)
        }
      }
    } catch (e: FileNotFoundException) {
      e.printStackTrace()
    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  fun patternsToPhrase(
          patterns: List<Pattern>,
          harmony: HashMap<Double, Chord>,
          currentTonality: Tonality): Phrase? {
    val phr = Phrase()

    //Get all the PNotes from all patterns
    val notes = LinkedList<PNote>()
    for (p in patterns)
      notes.addAll(p.notes!!)

    if (notes.first.transitionType !== PConstants.STABLE_NOTE) {
      println("DataStorage > patternsToPhrase > Error converting to phrase. First note is not beginning.")
      return null
    }

    var lastNote: Note? = null
    var barPosition = 0.0
    for (note in notes) {
      if (note.transitionType === PConstants.STABLE_NOTE) {
        if (note.hasMelismas()) {
          val melisma = note.melismas
          when (melisma!!.type) {
            Melisma.GRACE -> {
              //First, we add a grace note
              val melismaNote = Note(note.getPitch() + melisma.pitchValue, JMC.THIRTYSECOND_NOTE, note.volume - 5)
              phr.add(melismaNote)

              //Then we add main note
              phr.add(Note(note.getPitch(), getAbsoluteLength(note.length, note.absolute_length) - JMC.THIRTYSECOND_NOTE, note.volume))
            }

            Melisma.TRILL -> {
              var trillLength = getAbsoluteLength(note.length, note.absolute_length)
              var trillMultiplier = 0
              while (trillLength > 0) {
                phr.add(Note(note.getPitch() + melisma.pitchValue * trillMultiplier, JMC.THIRTYSECOND_NOTE, note.volume))
                trillMultiplier = (trillMultiplier + 1) % 2
                trillLength -= JMC.THIRTYSECOND_NOTE
              }
            }
          }
        } else {
          phr.add(Note(note.getPitch(), getAbsoluteLength(note.length, note.absolute_length), note.volume))
        }

        //Define last note independently. Any melismas doesn't affect this var.
        lastNote = Note(note.getPitch(), getAbsoluteLength(note.length, note.absolute_length), note.volume)

      } else {
        val cd = harmony[nearestKey(harmony, barPosition)]
        val n = Note(getPitchByTransition(note.transitionType,
                note.transitionValue!!,
                lastNote!!.pitch, cd!!, currentTonality),
                getAbsoluteLength(note.length, lastNote.rhythmValue),
                lastNote.dynamic)
        phr.add(n)
        lastNote = n
      }
      barPosition += lastNote.rhythmValue
    }

    return phr
  }

  /** Get pitch based on transition parameters and other stuff.
   * @param transitionType
   * *
   * @param transitionValue
   * *
   * @param pitch
   * *
   * @param cd
   * *
   * @param currentTonality
   * *
   * @return
   */
  fun getPitchByTransition(
          transitionType: Int,
          transitionValue: IntArray,
          pitch: Int,
          cd: Chord,
          currentTonality: Tonality): Int {
    when (transitionType) {
      PConstants.SAME_PITCH -> return pitch

      PConstants.CHORD_JUMP -> return getElementInArrayByStep(transitionValue[0], cd.allChordNotes, pitch)

      PConstants.CHROMATIC_JUMP -> return pitch + transitionValue[0]

      PConstants.TONALITY_JUMP -> return currentTonality.getTonalityNoteByStep(transitionValue[0], pitch)

      PConstants.COMPLEX -> {
        val chordResult = getElementInArrayByStep(transitionValue[0], cd.allChordNotes, pitch)
        val tonalityResult = currentTonality.getTonalityNoteByStep(transitionValue[0], chordResult)
        return tonalityResult
      }

      else -> return pitch
    }
  }

  /** Get pitch based on transition parameters and other stuff except a chord.
   * @param transitionType
   * *
   * @param transitionValue
   * *
   * @param pitch
   * *
   * @param currentTonality
   * *
   * @return
   */
  fun getPitchByTransition(
          transitionType: Int,
          transitionValue: IntArray,
          pitch: Int,
          currentTonality: Tonality): Int {
    when (transitionType) {
      PConstants.SAME_PITCH -> return pitch

      PConstants.CHORD_JUMP //This situation doesn't appear because when calling this method - we never use chords.
      -> return pitch

      PConstants.CHROMATIC_JUMP -> return pitch + transitionValue[0]

      PConstants.TONALITY_JUMP -> return currentTonality.getTonalityNoteByStep(transitionValue[0], pitch)

      PConstants.COMPLEX //This situation doesn't appear either because when calling this method - we never use chords.
      -> return pitch

      else -> return pitch
    }
  }


  /** Returns a random pattern from hashMap which has required jumpvalue.
   * @param jumpValue
   * *
   * @param patternsHashMap
   * *
   * @return New instance of pattern (no needs to wrap into new Pattern());
   */
  fun getRandomPatternByJumpValue(
          jumpValue: Int,
          patternsHashMap: HashMap<Int, LinkedList<Pattern>>): Pattern {
    if (!patternsHashMap.containsKey(jumpValue)) {
      println("DataStorage > getRandomPatternByJumpValue > No entries for this jump value! Value = " + jumpValue)
      return SNPattern(JMC.D4, JMC.QUARTER_NOTE) // I'm a kind person, I always can give asker a D ;)
    }

    val goodPatterns = patternsHashMap[jumpValue]

    val randomId = (Math.random() * (goodPatterns!!.size - 1)).toInt()
    return Pattern(goodPatterns.get(randomId))
  }

  fun getAbsoluteLength(
          relativeLength: Double,
          absoluteLength: Double): Double {
    return relativeLength * absoluteLength
  }


  fun getRandomPatternFromList(list: LinkedList<Pattern>): Pattern {
    return list[Random().nextInt(list.size)]
  }

  fun getRandomMelismaFromList(list: LinkedList<Melisma>): Melisma {
    return list[Random().nextInt(list.size)]
  }


  fun getTonalityTypeByName(name: String): String {
    if (name == "минор")
      return "minor"

    if (name == "мажор")
      return "major"

    println("DataStorage > Unknown tonality: " + name)

    return "major"
  }

  val instrumentsImagesList: Array<String?>
    get() {
      val array = arrayOfNulls<String>(INSTRUMENTS.size)
      for (i in INSTRUMENTS.indices) {
        array[i] = INSTRUMENTS[i].name
      }
      return array
    }

  val instrumentsRussianNamesList: Array<String?>
    get() {
      val array = arrayOfNulls<String>(INSTRUMENTS.size)
      for (i in INSTRUMENTS.indices) {
        array[i] = INSTRUMENTS[i].RussianName
      }
      return array
    }

  fun getInstrumentIdByMidiId(midiId: Int): Int {
    for (i in INSTRUMENTS.indices) {
      val p = INSTRUMENTS[i]
      if (p.midiId === midiId)
        return i
    }
    return -1
  }
}