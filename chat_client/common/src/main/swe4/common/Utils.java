package swe4.common;

public class Utils {
	public static int hashString(String str) {
		// Prime numbers for hashing
		int[] primes = {31, 37, 41, 43, 47, 53, 59};

		int hash = 1;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			hash = hash * primes[i % primes.length] + ch;
		}
		return hash;
	}
}
