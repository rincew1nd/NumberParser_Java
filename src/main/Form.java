package main;

import java.util.Hashtable;

public class Form {
	
	public static void main(String [] args)
	{
		NumberParser numpar = new NumberParser();
		Hashtable<String, String> result = numpar.TryParse("hundred and one");
		System.out.println(result.get("error"));
		System.out.println(result.get("result"));
		int test = Integer.parseInt(result.get("result"));
		System.out.println(numpar.ConvertToOldRussianNumber(test));
	}
	
	public Form()
	{
		NumberParser numpar = new NumberParser();
		Hashtable<String, String> result = numpar.TryParse("one hundred seventy seven");
		System.out.println(result.get("error"));
		System.out.println(result.get("result"));
	}
}
