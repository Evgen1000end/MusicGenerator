package ru.demkin.models

import com.sun.media.sound.SF2Soundbank
import jm.midi.SMF
import jm.music.data.Score
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence
import javax.sound.midi.Sequencer
import javax.sound.midi.Synthesizer

/**
 * Description of ru.demkin.models
 * @author evgen1000end
 * @since 07.08.2016
 */
class RealTimePlayer : Runnable {
  val logger = LoggerFactory.getLogger(RealTimePlayer::class.java)
  private val soundBankPath: String
  var isReady = false
  private val sequencer: Sequencer
  private val synthesizer: Synthesizer
  private val playbackSequencesQueue = LinkedList<Sequence>()

  constructor(soundBankPath: String) {
    this.soundBankPath = soundBankPath
    synthesizer = MidiSystem.getSynthesizer()
    synthesizer.open()
    val soundBankFile = File(soundBankPath)
    if (soundBankFile.exists()) {
      logger.info("Soundfonts has been found. Try to load that.")
      val soundbank = SF2Soundbank(FileInputStream(soundBankFile))
      synthesizer.loadAllInstruments(soundbank)
    }
    sequencer = MidiSystem.getSequencer()
    val seqTrans = sequencer.getTransmitter()
    sequencer.getTransmitters()[0].close()
    val receiver = synthesizer.getReceiver()
    seqTrans.receiver = receiver
    sequencer.open()
    isReady = true
  }

  fun playScore(score: Score) {
    if (score.partList.size == 0)
      return
    playbackSequencesQueue.add(scoreToSequence(score))
    logger.info("Play queue size: " + playbackSequencesQueue.size)
  }

  override fun run() {
    while (true) {
      if (playbackSequencesQueue.isEmpty() || !isReady) {
        Thread.sleep(10)
        continue
      }

      val sq = playbackSequencesQueue.removeFirst()
      sequencer.sequence = sq
      logger.info("try to plat sequence: " + sq.microsecondLength)
      sequencer.start()

      while (true) {
        if (sequencer.isRunning) {
          Thread.sleep(100)
        } else {
          break
        }
      }
    }
  }

  private fun scoreToSequence(s: Score): Sequence {
    val smf = SMF()
    smf.clearTracks()
    jm.midi.MidiParser.scoreToSMF(s, smf)
    val os = ByteArrayOutputStream()
    smf.write(os)
    return MidiSystem.getSequence(ByteArrayInputStream(os.toByteArray()))
  }
}