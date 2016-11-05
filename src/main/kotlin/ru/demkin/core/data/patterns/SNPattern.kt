package ru.demkin.core.data.patterns

import ru.demkin.core.data.PNote
import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
class SNPattern : Pattern {


  constructor(pitch: Int, length: Double) {
    //Creates a pattern with single note, which is beginning of phrase
    val note = PNote(pitch, length)
    notes = LinkedList<PNote>()
    notes!!.add(note)
  }

  //	public SNPattern (int pitch, double length, int volume)
  //	{
  //		//Creates a pattern with single note, which is beginning of phrase
  //		PNote note = new PNote(pitch, length, volume);
  //		notes = new LinkedList<PNote>();
  //		notes.add(note);
  //	}

  constructor(note: PNote) {
    //Creates a pattern with single note, which is beginning of phrase
    notes = LinkedList<PNote>()
    notes!!.add(note)
  }

  constructor(first: Pattern) {
    val firstNoteInPattern = first.notes!!.get(0)

    notes = LinkedList<PNote>()
    notes!!.add(PNote(firstNoteInPattern.getPitch(), firstNoteInPattern.absolute_length))
  }

  constructor(snPattern: SNPattern) {
    notes = LinkedList<PNote>()
    notes!!.add(PNote(snPattern.pitch, snPattern.absoluteLength))
  }

  val pitch: Int
    get() = notes!!.get(0).getPitch()

  val absoluteLength: Double
    get() = notes!!.get(0).absolute_length

  val rhythmValue: Double
    get() = notes!!.get(0).rhythmValue

  override val overallPatternJumpLength: Int
    get() = 0
}