package ru.demkin.core.generators.multigen

import jm.JMC
import ru.demkin.core.data.DataStorage
import ru.demkin.core.data.Motive
import ru.demkin.core.data.PInstrument
import ru.demkin.core.data.PNote
import ru.demkin.core.data.chords.Chord
import ru.demkin.core.data.patterns.CPattern
import ru.demkin.core.data.patterns.Pattern
import ru.demkin.core.data.patterns.SNPattern
import ru.demkin.core.data.tonality.Tonality
import ru.demkin.core.generators.BaseVoiceGenerator
import ru.demkin.utils.nearestKey
import ru.demkin.utils.pick
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class AccompanimentGenerator : BaseVoiceGenerator() {

  private var harmony: HashMap<Double, Chord>? = null
  private var octaveSummand: Int = 0
  private var lastNote: Int = 0
  private val CHANCES_OF_NOTE_JUMP = arrayOf(0.5, 0.15, 0.15, 0.07, 0.07, 0.06)//TODO: Move somewhere else;
  private val SHIFTED_OCTAVE_ELEMENTS = intArrayOf(0, 1, 2, 3, 4, 5, 6, -5, -4, -3, -2, -1)
  private var randomPatternForAccomp: Pattern? = null

  override fun setPossiblePatterns(instrument: PInstrument) {
    possiblePatterns = DataStorage.INSTRUMENTS_ACCOMPANIMENTS.get(instrument)
    if (possiblePatterns!!.size === 0)
      possiblePatterns = DataStorage.BASE_ACCOMPANIMENT_PATTERNS
    setCurrentRandomPattern()
  }

  fun generateMotive(harmony: HashMap<Double, Chord>, currentTonality: Tonality, octaveSummand: Int, lNote: Int): Motive {
    currentMotive = Motive()

    this.currentTonality = currentTonality
    this.harmony = harmony
    this.octaveSummand = octaveSummand
    this.lastNote = lNote

    if (this.lastNote == 0)
      this.lastNote = currentTonality.pitch

    generateSecondRun()
    convertSecondRunToStables()

    generateThirdRun()
    return currentMotive!!
  }

  fun setCurrentRandomPattern() {
    //Выбираем произвольный паттерным которым будет вестись наполнение
    //Он будет жить аж 4 такта
    randomPatternForAccomp = possiblePatterns!!.get(Random().nextInt(possiblePatterns!!.size))
  }

  private fun generateSecondRun() {
    //Переводим последнюю ноту в абсолютную величину в диапазоне от -5 до 6
    var absLastNote = lastNote % 12
    if (absLastNote > 6)
      absLastNote = absLastNote - 12

    //Выбираем первый аккорд в гармонии для текущего такта
    var chord = harmony!![0.0]

    //Основываясь на предыдущей ноте и текущем аккорде произвольным образом выбираем с какой ноты будем начинать аккомпанемент
    //Нота должна принадлежать аккорду, быть в абсолютном виде (0-11) и при этом стоять не далее чем на 6 полутонов от предыдущей ноты
    //Если подходящих нот несколько - между ними бросается жребий согласно вероятностям. Чем больше расстояние до новой ноты - тем меньше шанс что ее выберут
    //Вероятности берутся из CHANCES_OF_NOTE_JUMP
    var firstNoteInAccomp = pickNoteCloseToExisting(chord!!, absLastNote)

    //При наполнении нотами аккомпанемента отталкиваемся от идее, что длина нот в аккомпанементе должна равняться шестнадцатой.
    //Так как у нас всюду 4/4, то считаем сколько нот у нас в паттерне. Если 4 - до делаем 4 раза по 4. Если 8 - то два раза по 8.
    //Итого итерируемся от 0 до 16 / (кол-во нот в паттерне + 1) (т.к. еще первую ноту добавлять надо к паттерну)
    val maxIterations = 16 / (randomPatternForAccomp!!.patternRelativeLength + 1) as Int
    for (i in 0..maxIterations - 1) {
      val currentPattern = CPattern()

      //Считаем какая это по счету четверть в такте, основываясь на i
      val rhythmValue = (i * (4 / maxIterations)).toDouble()
      chord = harmony!![nearestKey(harmony!!, rhythmValue)]

      var chordContainsFirstNote = false
      for (k in 0..chord!!.chord.size - 1) {
        if (chord.chord[k] === (firstNoteInAccomp + 12) % 12) {
          chordContainsFirstNote = true
          break
        }
      }

      if (!chordContainsFirstNote) {
        firstNoteInAccomp = pickNoteCloseToExisting(chord!!, firstNoteInAccomp)
      }

      currentPattern.addPattern(
              SNPattern(
                      PNote(
                              firstNoteInAccomp + 60 + octaveSummand,
                              JMC.QUARTER_NOTE)))


      currentPattern.addPattern(randomPatternForAccomp!!)

      currentPattern.multiplyFirstNoteLength(0.25)

      currentMotive!!.secondRunPatterns.add(currentPattern)
    }

  }

  private fun pickNoteCloseToExisting(chord: Chord, prevNote: Int): Int {
    //Заводим дополнительный массив для аккорда. Мы должны дополнить аккорд еще и отрицательными значениями - чтобы подойти к предыдущей ноте с двух сторон
    //Если chord = [0,4,7] то мы строим doubledChord как [-12, -8, -5, 0, 4, 7], т.е. первая половина = [chord(i) - 12], а вторая половина неизменный chord
    val doubledChord = IntArray(chord.chord.size * 2)

    val chordLength = chord.chord.size

    //Заполняем doubledChord нужными значениями
    for (k in 0..chordLength - 1) {
      doubledChord[k] = chord.chord[k] - 12
      doubledChord[k + chordLength] = chord.chord[k]
    }


    //Нота не должна отстоять слишком далеко от основного тона тональности, т.е. если основная нота си, то должно все быть в диапазоне -5..6 от этой ноты
    //Необходимо перевести высоту тона в диапазон -5..6 чтобы при расчете нот doubleChords правильно брать расстояние
    val pitchValue = SHIFTED_OCTAVE_ELEMENTS[currentTonality!!.pitch % 12]

    //Заводим мап в который будем записывать кандидатов в стартовые ноты и их вероятности
    val firstNoteCandidates = HashMap<Int, Double>()
    for (k in doubledChord.indices) {

      //Note shouldn't be far from main note in tonality
      if (Math.abs(doubledChord[k] - pitchValue) > 6)
        continue

      //Вычисляем расстояние между нотами
      val distanceBetweenNotes = Math.abs(doubledChord[k] - prevNote)
      if (distanceBetweenNotes <= 5)
      //5 = quart is maximum to jump.
      {
        //Если расстояние меньше кварты - добавляем его к кандидатам
        firstNoteCandidates.put(doubledChord[k], CHANCES_OF_NOTE_JUMP[distanceBetweenNotes])
      }
    }

    //Вызываем функцию, которая рандомно возьмет кандидата из мапа (с учетом весов) и возвращаем его
    return pick(firstNoteCandidates)!!
  }

  private fun convertSecondRunToStables() {
    for (i in 0..currentMotive!!.secondRunPatterns.size - 1) {
      //Finally insert this pattern into SecondRunStables
      currentMotive!!.secondRunStables.add(currentMotive!!.secondRunPatterns.get(i))
    }
  }


  private fun generateThirdRun() {
    for (i in 0..currentMotive!!.secondRunStables.size - 1)
      currentMotive!!.thirdRunPatterns.add(CPattern(currentMotive!!.secondRunStables.get(i) as CPattern))
  }
}//Nothing here yet :)