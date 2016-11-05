package ru.demkin.core.generators

import ru.demkin.core.data.Motive
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.Tonality
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class Harmonizer {

  private var chordAntecendents: HashMap<String, LinkedList<String>>? = null //Indicates which chords can be before the "key" chord.
  private var chordConsequents: HashMap<String, LinkedList<String>>? = null //Indicates which chords can follow the "key" chord

  //Local variables used for harmonizing

  private var barsList: LinkedList<List<SNPattern>>? = null  //With blackjack and whores.
  private var currentTonality: Tonality? = null        //Current tonality for harmonization
  private var currentBarIndex: Int = 0          //Index of bar which is been harmonized
  private var lastChord: String? = null            //Name of last chord that was
  private var chordsUsed: Int = 0              //Number of beats in bar already having a harmony. Used to prevent many chords in bar.
  private var beatsHarmonized: Int = 0

  private var halfByHalfHarmonization = false


  init {

    //initAntecendents();
    initConsequents()
    initAntecendents()
    lastChord = ""
    currentBarIndex = 0
  }

  private fun initConsequents() {
    chordConsequents = HashMap<String, LinkedList<String>>()
    chordConsequents!!.put("I", LinkedList(Arrays.asList("I", "IV", "V", "II", "III", "VI"))) //After I can go anything
    chordConsequents!!.put("II", LinkedList(Arrays.asList("II", "V", "I", "III"))) //Which can be after II
    chordConsequents!!.put("III", LinkedList(Arrays.asList("III", "IV", "I", "V", "VI", "VII")))
    chordConsequents!!.put("IV", LinkedList(Arrays.asList("IV", "V", "I", "II")))
    chordConsequents!!.put("V", LinkedList(Arrays.asList("V", "I", "VI", "III", "VII")))
    chordConsequents!!.put("VI", LinkedList(Arrays.asList("VI", "IV", "III", "I", "II")))
    chordConsequents!!.put("VII", LinkedList(Arrays.asList("VII", "I")))
    chordConsequents!!.put("all", LinkedList(Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII")))
  }

  private fun initAntecendents() {
    chordAntecendents = HashMap<String, LinkedList<String>>()
    chordAntecendents!!.put("I", LinkedList(Arrays.asList("II", "III", "IV", "V", "VI", "VII"))) //After every chord can follow I
    chordAntecendents!!.put("II", LinkedList(Arrays.asList("I", "IV", "VI"))) //Which can be before II
    chordAntecendents!!.put("III", LinkedList(Arrays.asList("I", "II", "VI"))) //Etc
    chordAntecendents!!.put("IV", LinkedList(Arrays.asList("I", "III", "VI")))
    chordAntecendents!!.put("V", LinkedList(Arrays.asList("I", "II", "III", "IV")))
    chordAntecendents!!.put("VI", LinkedList(Arrays.asList("I", "III", "V")))
    chordAntecendents!!.put("VII", LinkedList(Arrays.asList("I", "III", "V")))
  }

  fun getHarmonyForMotive(m: Motive, cT: Tonality): HashMap<Double, Chord> {
    currentTonality = cT
    barsList = LinkedList<List<SNPattern>>()
    halfByHalfHarmonization = false
    beatsHarmonized = 0

    val harmony = HashMap<Double, Chord>()
    val harmonizedMelody = HashMap<SNPattern, Chord>()

    convertMotiveToBarsList(m)

    for (i in barsList!!.indices) {
      when (currentBarIndex) {
        0 -> {
          harmonizedMelody.putAll(harmonizeFirstBar(barsList!![i]))
        }
        3 -> {
          harmonizedMelody.putAll(harmonizeFourthBar(barsList!![i]))
        }

        7 -> {
          harmonizedMelody.putAll(harmonizeEighthBar(barsList!![i]))
        }

        else -> {
          harmonizedMelody.putAll(harmonizeBar(barsList!![i]))
        }
      }
      currentBarIndex++
      if (currentBarIndex > 8)
        currentBarIndex = 0
    }


    var nextNotePosition = 0.0
    for (patt in m.firstRunStables) {
      harmony.put(nextNotePosition, harmonizedMelody[patt]!!)
      nextNotePosition += patt.absoluteLength
    }

    //Это надо потом перенести в пост-обработку куда-нибудь

    //System.out.println("Harmony: " + harmony.toString());

    return harmony
  }

  private fun harmonizeFirstBar(stablesList: List<SNPattern>): HashMap<SNPattern, Chord> {
    val result = HashMap<SNPattern, Chord>()

    lastChord = "I"
    analyzeBar(stablesList)
    result.put(stablesList[0], currentTonality!!.getChordByDegree(lastChord!!))

    chordsUsed = 1

    result.putAll(harmonizeFurtherPart(stablesList.subList(1, stablesList.size)))

    return result
  }

  private fun harmonizeFourthBar(stablesList: List<SNPattern>): HashMap<SNPattern, Chord> {

    val result = HashMap<SNPattern, Chord>()

    lastChord = "V"
    chordsUsed = 2

    analyzeBar(stablesList)
    //result.put(stablesList.get(0), currentTonality.getChordByDegree(lastChord));
    for (pattern in stablesList) {
      result.put(pattern, currentTonality!!.getChordByDegree(lastChord!!))
    }

    return result
  }

  private fun harmonizeEighthBar(stablesList: List<SNPattern>): HashMap<SNPattern, Chord> {

    val result = HashMap<SNPattern, Chord>()

    lastChord = "I"
    chordsUsed = 2

    analyzeBar(stablesList)
    for (pattern in stablesList) {
      result.put(pattern, currentTonality!!.getChordByDegree(lastChord!!))
    }

    return result
  }

  private fun harmonizeBar(stablesList: List<SNPattern>): HashMap<SNPattern, Chord> {

    chordsUsed = 0
    val result = HashMap<SNPattern, Chord>()

    analyzeBar(stablesList)
    result.putAll(harmonizeFurtherPart(stablesList))

    return result
  }

  private fun analyzeBar(stablesList: List<SNPattern>) {
    //Если у нас 4 четверти то первую половину гармонизируем одним аккордом а вторую другим
    if (stablesList.size == 4) {
      halfByHalfHarmonization = true
    }
  }

  private fun convertMotiveToBarsList(m: Motive) {
    var startId = 0
    var i = 1

    //Iterate through list of stables in motive
    while (i < m.firstRunStables.size) {
      if (m.firstRunStables.get(i).rhythmValue === 0.0)
      //Look for beginnings of bar
      {
        barsList!!.add(m.firstRunStables.subList(startId, i)) //If found - add sublist to bars list
        startId = i
      }
      i++
    }
    barsList!!.add(m.firstRunStables.subList(startId, m.firstRunStables.size)) //Add last bar to bars list
  }

  private fun harmonizeFurtherPart(list: List<SNPattern>): HashMap<SNPattern, Chord> {
    var result = HashMap<SNPattern, Chord>()

    val furtherPart = harmonizeSublist(list)

    //If we failed to harmonize
    if (furtherPart == null) {
      //Cannot harmonize. Sorry bro!
      println("Harmonizer > harmonizeSublist() > Harmonization failed. Last chord = " + lastChord!!)

      lastChord = "V"
      for (pattern in list) {
        result.put(pattern, currentTonality!!.getChordByDegree(lastChord!!))
      }
    } else {
      result = furtherPart
    }

    return result
  }

  private fun harmonizeSublist(list: List<SNPattern>): HashMap<SNPattern, Chord>? {
    val result = HashMap<SNPattern, Chord>()

    if (list.isEmpty()) {
      return result
    }

    val notePitch = list[0].pitch
    val OCTAVE = 12

    /**
     * armaxis (17:45:18 6/04/2013)
     * это у меня есть нота допустим МИ
     * и есть тональность ля-мажор
     * и я определяю какая по счету будет нота ми в ля-мажоре =) Сперва привожу их в одну октаву, потом делаю сдвиг и вычитаю
     * ми = 52, ля = 69
     * ( ( 52 % 12 ) + 12 - (69 % 12) ) % 12
     * ( 4 + 12 - 9 ) % 12 = 7
     * и таки да - ми в ля - седьмая по счету
     */
    //TODO: Non-chord notes must be processed too!!!
    //int noteValueInTonality = ( ( (notePitch % OCTAVE) + OCTAVE ) - (currentTonality.getPitch() % OCTAVE) ) % OCTAVE;
    //Everything above was wrong, we need absolute position of the note in gamma, independent from tonality.
    val noteValueInTonality = notePitch % OCTAVE

    val possibleChords = LinkedList<String>()
    possibleChords.addAll(chordConsequents!!.get(lastChord)!!) //Get list of possible chords

    //If it's a beginning of bar we can also use any other chord, but high prioiry is on consequents
    if (chordsUsed == 0) {
      possibleChords.addAll(chordConsequents!!["all"]!!)
    }

    if (halfByHalfHarmonization && chordsUsed == 1 && beatsHarmonized == 1) {
      result.put(list[0], currentTonality!!.getChordByDegree(lastChord!!))
      beatsHarmonized++
      val furtherPart = harmonizeSublist(list.subList(1, list.size))
      if (furtherPart == null) {
        beatsHarmonized--
        return null
      }
      result.putAll(furtherPart)
      return result
    }

    //System.out.println("Notevalue = " + noteValueInTonality + " Pitch = " + notePitch + " Possible chords: " + possibleChords.toString());

    if (chordsUsed >= 2 && Math.random() > 0.7) {
      for (pattern in list) {
        result.put(pattern, currentTonality!!.getChordByDegree(lastChord!!))

      }
      //System.out.println("2 beats already, harmonized with " + lastChord);
      return result
    }

    val originalLastChord = lastChord

    for (ch in possibleChords) {
      //If note is a part of a possible chord. E.g. "7" is a part of chords [0, 3, 7] and [3, 7, 10].
      if (containsInt(currentTonality!!.getChordByDegree(ch).chord, noteValueInTonality)) {
        //System.out.println("Trying to use " + ch);

        //If the chord is same as previous, but we are at the beginning of bar we need to reharmonize
        if (ch == lastChord && chordsUsed == 0) {
          //System.out.println("Need reharmonize ");
          continue
        }

        //If this bar is no 3 - don't allow using V
        //				if(ch.equals("V") && currentBarIndex == 2)
        //				{
        //					continue;
        //				}

        //If this bar is no 7 - don't allow using I
        if (ch == "I" && currentBarIndex == 6) {
          //	System.out.println("this bar is no 7 - don't allow using I");
          continue
        }

        if (lastChord != ch)
          chordsUsed++

        beatsHarmonized++

        //Try to harmonize the rest;
        lastChord = ch
        val furtherPart = harmonizeSublist(list.subList(1, list.size))

        if (furtherPart == null)
        //If cannot be harmonized - continue to
        {
          //	System.out.println("Cannot harmonize sublist, trying another ");
          chordsUsed--
          beatsHarmonized--
          lastChord = originalLastChord
          continue
        } else {
          //System.out.println("Harmonize sublist succeeded ");
          result.put(list[0], currentTonality!!.getChordByDegree(ch))
          result.putAll(furtherPart)
          return result
        }
      }
    }

    return null
  }

  private fun containsInt(array: IntArray, value: Int): Boolean {
    for (i in array.indices) {
      if (array[i] == value)
        return true
    }

    return false
  }

}