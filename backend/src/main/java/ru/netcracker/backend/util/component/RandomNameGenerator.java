package ru.netcracker.backend.util.component;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Random;

@Component
public class RandomNameGenerator {
    private static final int diffBetweenAtoZ = 25;
    private static final int charValueOfa = 97;
    private String lastGeneratedName = "";
    int length;

    char[] vowels = {
            'a', 'e', 'i', 'o', 'u'
    };

    public String getName(int lengthOfName) {
        handleSettingLengthOfName(lengthOfName);
        for (;;) {
            Random randomNumberGenerator = new Random(Calendar.getInstance()
                    .getTimeInMillis());

            char[] nameInCharArray = new char[length];

            for (int i = 0; i < length; i++) {
                if (positionIsOdd(i)) {
                    nameInCharArray[i] = getVowel(randomNumberGenerator);
                } else {
                    nameInCharArray[i] = getConsonant(randomNumberGenerator);
                }
            }
            nameInCharArray[0] = Character.toUpperCase(nameInCharArray[0]);
            String currentGeneratedName = new String(nameInCharArray);

            if (!currentGeneratedName.equals(lastGeneratedName)) {
                lastGeneratedName = currentGeneratedName;
                return currentGeneratedName;
            }
        }
    }

    private void handleSettingLengthOfName(int lengthOfName) {
        if (lengthOfName < 5 || lengthOfName > 10) {
            lengthOfName = 7;
        }

        this.length = lengthOfName;
    }

    private boolean positionIsOdd(int i) {
        return i % 2 == 0;
    }

    private char getConsonant(Random randomNumberGenerator) {
        for (;;) {
            char currentCharacter = (char) (randomNumberGenerator
                    .nextInt(diffBetweenAtoZ) + charValueOfa);
            if (currentCharacter == 'a'
                    || currentCharacter == 'e'
                    || currentCharacter == 'i'
                    || currentCharacter == 'o'
                    || currentCharacter == 'u')
                continue;
            else
                return currentCharacter;
        }

    }

    private char getVowel(Random randomNumberGenerator) {
        return vowels[randomNumberGenerator.nextInt(vowels.length)];
    }
}
