package de.olech2412.mensahub.datadispatcher.data.tools;

import de.olech2412.mensahub.models.Leipzig.Allergene;

import java.util.Comparator;

public class AllergeneComparator implements Comparator<Allergene> {
    @Override
    public int compare(Allergene a1, Allergene a2) {
        String token1 = a1.getToken();
        String token2 = a2.getToken();

        String numPart1 = token1.replaceAll("[^0-9]", "");
        String numPart2 = token2.replaceAll("[^0-9]", "");

        int numComparison = Integer.compare(Integer.parseInt(numPart1), Integer.parseInt(numPart2));
        if (numComparison != 0) {
            return numComparison;
        } else {
            String alphaPart1 = token1.replaceAll("[0-9]", "");
            String alphaPart2 = token2.replaceAll("[0-9]", "");
            return alphaPart1.compareToIgnoreCase(alphaPart2);
        }
    }
}
