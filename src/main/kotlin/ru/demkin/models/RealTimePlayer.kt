package ru.demkin.models

import com.sun.media.sound.SF2Soundbank
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
class RealTimePlayer(val soundBankPath:String):Runnable {

    var isReady = false
    private var sequencer: Sequencer
    private var synthesizer: Synthesizer
    private var playbackSequencesQueue: LinkedList<Sequence>

    init {
        playbackSequencesQueue = LinkedList<Sequence>()
        synthesizer = MidiSystem.getSynthesizer()
        synthesizer.open()

        val soundBankFile = File(soundBankPath)
        if (soundBankFile.exists()) {
            val soundbank = SF2Soundbank(FileInputStream(soundBankFile))
            synthesizer.loadAllInstruments(soundbank)
        }
        sequencer = MidiSystem.getSequencer()
        val seqTrans = sequencer.getTransmitter()
        sequencer.getTransmitters()[0].close()

        val synthRec = synthesizer.getReceiver()
        seqTrans.receiver = synthRec
        sequencer.open()

        isReady = true
    }

    override fun run() {

    }
}