import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// two methods presented, one returning a single string and one returning the entire arraylist of equal length strings
public class LongestCommonSubstring {
    public static String solve(List < String > input_list) {
        // store list size
        int n = input_list.size();

        // use first word as base
        String reference_string = input_list.get(0);
        int len = reference_string.length();

        // start comparisons with other substrings in other words in list
        String res = "";
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j <= len; j++) {
                // generating all possible substrings of our reference string arr[0] i.e s 
                String substring = reference_string.substring(i, j);
                int k = 1;
                for (k = 1; k < n; k++) {
                    // leverage contains to check if the reference substring exists in other list elements
                    if (!input_list.get(k).contains(substring)) {
                        break;
                    }
                }
                // if current substring is present in all strings
                if (k == n) {
                    // if its length is greater than current result, set it to the result
                    if (res.length() < substring.length()) {
                        {
                            res = substring;
                        }
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList < String > solve_list(List < String > input_list) {
        // store list size
        int n = input_list.size();

        // use first word as base
        String reference_string = input_list.get(0);
        int len = reference_string.length();

        // start comparisons with other substrings in other words in list
        int longest = 0;
        ArrayList < String > res = new ArrayList < String > ();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j <= len; j++) {
                // generating all possible substrings of our reference string arr[0] i.e s 
                String substring = reference_string.substring(i, j);
                int k = 1;
                for (k = 1; k < n; k++) {
                    // leverage contains to check if the reference substring exists in other list elements
                    if (!input_list.get(k).contains(substring)) {
                        break;
                    }
                }
                // if current substring is present in all strings
                if (k == n) {
                    if (substring.length() > longest) {
                        longest = substring.length();
                    }
                    res.add(substring);
                }
            }
        }

        // create a new arraylist for the longest words only
        ArrayList < String > longest_words = new ArrayList < String > ();
        for (String tempstr: res) {
            if (tempstr.length() == longest) {
                longest_words.add(tempstr);
            }
        }

        // sort the list alphabetically
        Collections.sort(longest_words);

        return longest_words;
    }
}