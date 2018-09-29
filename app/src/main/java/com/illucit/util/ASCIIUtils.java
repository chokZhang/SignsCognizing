package com.illucit.util;

import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

/**
 * Utility class for access to {@link ASCIIFoldingFilter}.
 *
 * @author Christian Simon
 */
public class ASCIIUtils {

    /**
     * Converts characters above ASCII to their ASCII equivalents.  For example,
     * accents are removed from accented characters.
     *
     * @param input     The characters to fold
     * @param inputPos  Index of the first character to fold
     * @param output    The result of the folding. Should be of size &gt;= {@code length * 4}.
     * @param outputPos Index of output where to put the result of the folding
     * @param length    The number of characters to fold
     * @return length of output
     */
    public static int foldToASCII(char input[], int inputPos, char output[], int outputPos, int length) {
        return ASCIIFoldingFilter.foldToASCII(input, inputPos, output, outputPos, length);
    }

    /**
     * Maps the chars of the input string to their ascii equivalents.
     *
     * @param inputStr the input string
     * @return the normalized string
     */
    public static String foldToASCII(String inputStr) {
        char[] input = inputStr.toCharArray();
        int length = input.length;

        // Worst-case length required:
        final int maxSizeNeeded = 4 * length;
        char[] output = new char[maxSizeNeeded];

        int outputPos = foldToASCII(input, 0, output, 0, length);

        return String.copyValueOf(output, 0, outputPos);
    }

}
