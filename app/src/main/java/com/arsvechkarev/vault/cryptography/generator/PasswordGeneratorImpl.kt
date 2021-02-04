package com.arsvechkarev.vault.cryptography.generator

import com.arsvechkarev.vault.core.model.PasswordCharacteristics
import com.arsvechkarev.vault.core.model.PasswordCharacteristics.NUMBERS
import com.arsvechkarev.vault.core.model.PasswordCharacteristics.SPECIAL_SYMBOLS
import com.arsvechkarev.vault.core.model.PasswordCharacteristics.UPPERCASE_SYMBOLS
import java.security.SecureRandom

class PasswordGeneratorImpl(private val random: SecureRandom) : PasswordGenerator {
  
  private val uppercaseSymbolsGenerator: SymbolsGenerator = UppercaseSymbolsGenerator(random)
  private val lowercaseSymbolsGenerator: SymbolsGenerator = LowercaseSymbolsGenerator(random)
  private val numbersGenerator: SymbolsGenerator = NumbersGenerator(random)
  private val specialSymbolsGenerator: SymbolsGenerator = SpecialSymbolsGenerator(random)
  
  override fun generatePassword(
    length: Int,
    characteristics: Collection<PasswordCharacteristics>
  ): String {
    val array = CharArray(length)
    val generators = getGeneratorsFrom(characteristics)
    repeat(length) { i ->
      val randomGenerator = generators[random.nextInt(generators.size)]
      array[i] = randomGenerator.generate()
    }
    return String(array)
  }
  
  private fun getGeneratorsFrom(
    characteristics: Collection<PasswordCharacteristics>
  ): List<SymbolsGenerator> {
    val generators = ArrayList<SymbolsGenerator>()
    generators.add(lowercaseSymbolsGenerator)
    if (characteristics.contains(UPPERCASE_SYMBOLS)) generators.add(uppercaseSymbolsGenerator)
    if (characteristics.contains(NUMBERS)) generators.add(numbersGenerator)
    if (characteristics.contains(SPECIAL_SYMBOLS)) generators.add(specialSymbolsGenerator)
    return generators
  }
}