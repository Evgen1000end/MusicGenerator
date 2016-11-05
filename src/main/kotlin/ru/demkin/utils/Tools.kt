package ru.demkin.utils

import java.util.*

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
fun getElementInArrayByStep(step: Int, array: IntArray, startNote: Int): Int {
  var tonalityElementId = 0
  for (i in 0..array.size - 1 - 1) {
    if (array[i] > startNote) {
      tonalityElementId = if (Math.signum(step.toFloat()) > 0) i - 1 else i //If we are going down the scale - we take note which is after.
      break
    } else if (array[i] == startNote) {
      tonalityElementId = i
      break
    }
  }

  if (tonalityElementId + step < 0 || tonalityElementId + step > array.size) {
    return 0
  }

  return array[tonalityElementId + step]
}

infix fun Double.lessOrEquals(other: Double) = this.compareTo(other)<=0


fun nearestKey(map: HashMap<Double, *>, target: Double?): Double? {
  var minDiff = java.lang.Double.MAX_VALUE
  var nearest: Double? = null
  for (key in map.keys) {
    if (key lessOrEquals target!!) {
      val diff = Math.abs(target - key)
      if (diff < minDiff) {
        nearest = key
        minDiff = diff
      }
    }
  }
  return nearest
}

fun octaveIndexToShift(octaveIndex: Int): Int {
  return octaveIndex * 12 - 36
}

fun pick(valuesHash: HashMap<Int, Double>): Int? {
  // Compute the total weight of all items together
  var totalWeight = 0.0
  for (i in valuesHash.values) {
    totalWeight += i
  }
  // Now choose a random item
  var random = Math.random() * totalWeight

  for (hashKey in valuesHash.keys) {
    random -= valuesHash.get(hashKey)!!
    if (random <= 0.0) {
      return hashKey
    }
  }
  return 0 //Shouldn't happen
}
