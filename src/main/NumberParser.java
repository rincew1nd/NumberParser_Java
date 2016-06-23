package main;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberParser {
	public Hashtable<String, String> ErrorDic;
    private Hashtable<String, Integer> NumberDic;
    private Hashtable<Integer, String> StarorusskiiDic;
    private Hashtable<String, String> ResultDic;
    private ArrayList<String> AfterWords;
    private ArrayList<String> DozenWords;
    private ArrayList<String> UnitsWords;
    
    public NumberParser()
    {
    	ErrorDic = new Hashtable<String, String>() {{
    		put("empty" , "Пустая строка");
    		put("spaces" , "В строке только пробелы");
    		put("undefinedsymbols" , "Строка содержит не только латинские символы");
    		// Проверки слов
    		put("notfinded" , "Не удалось распознать слово ");
    		put("hundreds" , "hundreds - существительное, а не число.");
    		// Проверки слов между дэшей
    		put("toomanydashes" , "Тире разделяет боллее двух слов.\r\n");
    		put("wrongdash" , "Не удалось распознать слово \r\n");
    		put("dashedword" , "Тире разделяет неверные слова.\r\n");
    		// Проверка слов между and и zero
    		put("wrongand" , "Неверные слова между And.\r\n");
    		put("lastand" , "And находится в конце.\r\n");
    		put("firstand" , "And находится в начале.\r\n");
    		put("zero" , "Ничего не может идти перед и после 0.");
    		// Новые проверки
    		put("hundredAfterHundred" , "Два слова hundred подряд.");
    		put("afterWordsAfterAfterWords" , "Два числа десятичного формата подряд.\r\n");
    		put("dozenWordsAfterAfterWords" , "Числа формата 10-19 после чисел десятичного формата.\r\n");
    		put("hundredAfterAfterWords" , "hundred после чисел десятичного формата.\r\n");
    		put("unitsWordsAfterDozenWords" , "Числа единичного формата после чисел формата 10-19.\r\n");
    		put("afterWordsAfterDozenWords" , "Числа десятичного формата после чисел формата 10-19.\r\n");
    		put("dozenWordsAfterDozenWords" , "Два числа формата 10-19 подряд.\r\n");
    		put("hundredAfterDozenWords" , "hundred после чисел формата 10-19.\r\n");
    		put("unitsWordsAfterUnitsWords" , "Два числа единичного формата подряд.\r\n");
    		put("afterWordsAfterUnitsWords" , "Числа десятичного формата после чисел единичного формата.\r\n");
    		put("dozenWordsAfterUnitsWords" , "Числа формата 10-19 после чисел единичного формата.\r\n");
    	}};
    	
    	AfterWords = new ArrayList<String>() {{
			add("twenty"); add("thirty"); add("forty"); add("fifty"); add("sixty"); add("seventy"); add("eighty"); add("ninety");
    	}};
    	
    	DozenWords = new ArrayList<String>() {{
			add("ten"); add("eleven"); add("twelve"); add("thirteen"); add("fourteen"); add("fifteen"); add("sixteen"); add("seventeen"); add("eighteen"); add("nineteen");
    	}};
    	
    	UnitsWords = new ArrayList<String>() {{
			add("zero"); add("one"); add("two"); add("three"); add("four"); add("five"); add("six"); add("seven"); add("eight"); add("nine");
    	}};
    	
    	NumberDic = new Hashtable<String, Integer>(){{
			put("and", 0); put("zero", 0); put("one", 1); put("two", 2); put("three", 3); put("four", 4); put("five", 5); put("six", 6); put("seven", 7); put("eight", 8); put("nine", 9); put("ten", 10); put("eleven", 11); put("twelve", 12); put("thirteen", 13); put("fourteen", 14); put("fifteen", 15); put("sixteen", 16); put("seventeen", 17); put("eighteen", 18); put("nineteen", 19); put("twenty", 20); put("thirty", 30); put("forty", 40); put("fifty", 50); put("sixty", 60); put("seventy", 70); put("eighty", 80); put("ninety", 90); put("hundred", 100);
    	}};

    	StarorusskiiDic = new Hashtable<Integer, String>(){{
			put( 0, " "); put(1, "А"); put(2, "В"); put(3, "Г"); put(4, "Д"); put(5, "Е"); put(6, "S"); put(7, "З"); put(8, "Е"); put(9, "Ѳ"); put(10, "I"); put(20, "К"); put(30, "Л"); put(40, "М"); put(50, "Н"); put(60, "Ѯ"); put(70, "О"); put(80, "П"); put(90, "Ч"); put(100, "Р"); put(200, "С"); put(300, "Т"); put(400, "У"); put(500, "Ф"); put(600, "Ч"); put(700, "Ѱ"); put(800, "Ѡ"); put(900, "Ц");
    	}};
    	
    	ResultDic = new Hashtable<String, String>() {{
            put("result", "");
            put("error", "");
        }};
    }
    
    public String ConvertToOldRussianNumber(int num)
    {
        String result = "";

        if (num >= 500)
        {
            num -= 500;
            result += 'Ф';
        }
        if (num >= 100)
        {
            int j = num;
            for (int i = 0; i < j / 100; i++)
            {
                num -= 100;
                result += 'Р';
            }
        }
        if (num >= 30)
        {
            int j = num;
            for (int i = 0; i < j / 30; i++)
            {
                num -= 30;
                result += 'Л';
            }
        }
        if (num >= 8)
        {
            int j = num;
            for (int i = 0; i < j / 8; i++)
            {
                num -= 8;
                result += 'И';
            }
        }
        if (num >= 2)
        {
        	int j = num;
            for (int i = 0; i < j / 2; i++)
            {
                num -= 2;
                result += 'В';
            }
        }
        if (num == 1)
            result += 'А';

        return result;
    }
    
    public Hashtable<String, String> TryParse(String stringIn)
    {
        // Задаём дефолтное значение для результата
        ResultDic.put("result", "error");
        ResultDic.put("error", "");

        // Чекаем на пустую строку, только пробелы в строке и неверные символы
        if (stringIn.equals(""))
        {
            ResultDic.put("error", ErrorDic.get("empty"));
        }
        else if (stringIn.trim().length() == 0)
        {
            ResultDic.put("error", ErrorDic.get("spaces"));
            return ResultDic;
        }
        else if (!checkWithRegExp("^[a-zA-Z -]+$", stringIn))
        {
            ResultDic.put("error", ErrorDic.get("undefinedsymbols"));
            return ResultDic;
        }

        // Переводим строку в ловеркейс
        stringIn = stringIn.toLowerCase();

        // Переводим строку в массив строк. Сплитаем по пробелу
        String[] words = stringIn.split(" ");

        // Пробегаемся по каждому элементу и ищем элементы содержащие '-'. Проверяем их через CheckDashValues
        for(String word : words)
            if (word.contains("-"))
                ResultDic.put("error", CheckDashValues(word));
        if (ResultDic.get("error") != "")
            return ResultDic;

        // Переводим строку в массив строк. Сплитаем по пробелу и по дэшу
        words = stringIn.split("( |-)");

        // Пробегаемся по каждому элементу и ищем элементы содержащие 'and', 'hundreds' и 'zero'. Проверяем их через CheckAndValues
        for(String word : words)
        {
            if (UnitsWords.contains(word) || DozenWords.contains(word) || AfterWords.contains(word) || word.equals("hundred") || word.equals("and")) continue;
            if (word.equals("hundreds"))
                ResultDic.put("error", ErrorDic.get("hundreds"));
            else
                ResultDic.put("error", ErrorDic.get("notfinded") + word);
            return ResultDic;
        }

        // Пробегаемся по каждому элементу и ищем элементы содержащие 'and', 'hundreds' и 'zero'. Проверяем их через CheckAndValues
        for (int i = 0; i < words.length; i++)
        {
            if (i > 0 && words[i - 1].equals("zero"))
                ResultDic.put("error", ErrorDic.get("zero"));
            else if (words[i].equals("and"))
                ResultDic.put("error", CheckAndValues(words, i, words.length - 1));

            if (ResultDic.get("error") != "")
                return ResultDic;
        }

        // Правильно ли расположен "zero"
        for (int i = 0; i < words.length; i++)
        {
            if (i > 0 && words[i].equals("zero"))
            {
                ResultDic.put("error", ErrorDic.get("zero"));
                return ResultDic;
            }
        }

        // Проверяем порядок слов
        for (int i = 0; i < words.length; i++)
        {
            ResultDic.put("error", CheckOrder(words, i, words.length - 1));
            if (ResultDic.get("error") != "")
                return ResultDic;
        }

        int result = 0;
        for (int i = 0; i < words.length; i++)
        {
            String word = words[i];
            result += ParseWord(words, i, words.length - 1);
        }

        ResultDic.put("result", Integer.toString(result));
        return ResultDic;
    }
    
    private String CheckDashValues(String dashedWord)
    {
        // Проверяем количество дэшей в строке. Можно только один
        if (CountChar(dashedWord, '-') > 1)
            return ErrorDic.get("toomanydashes") + dashedWord;

        // Нет ли лишних символов в строке с дэшем. Доступны только a-z
        if (!checkWithRegExp("^[a-z]+[-][a-z]+$", dashedWord))
            return ErrorDic.get("wrongdash") + dashedWord;

        // Строку в массив
        String[] words = dashedWord.split("-");

        // Проверяем 2 элемента между дэшами
        if (UnitsWords.contains(words[0]) && words[1].equals("hundred"))
            return "";
        else if (AfterWords.contains(words[0]) && UnitsWords.contains(words[1]))
            return "";
        else
            return ErrorDic.get("dashedword") + dashedWord;
    }
    
    private String CheckAndValues(String[] words, int position, int lastposition)
    {
        if (position == 0 && words[position].equals("and"))
            return ErrorDic.get("firstand");
        if (position == lastposition)
            return ErrorDic.get("lastand");

        //ArrayList<String> wordsList = new ArrayList<String>() {{ arr("and"); add("hundred"); }};
        
        // Проверяем 2 элемента между дэшами
        if (words[position - 1].equals("hundred") && !(new ArrayList<String>() {{ add("and"); add("hundred"); }}).contains(words[position + 1]))
            return "";
        else
            return ErrorDic.get("wrongand") + words[position - 1] + " " + words[position] + " " + words[position + 1];
    }

    private String CheckOrder(String[] words, int position, int lastPosition)
    {
        if (position == lastPosition) return "";

        if (words[position].equals("hundred"))
        {
            if (words[position + 1].equals("hundred"))
                return ErrorDic.get("hundredAfterHundred") + words[position] + " " + words[position + 1];
        }
        
        if (AfterWords.contains(words[position]))
        {
            if (AfterWords.contains(words[position+1]))
                return ErrorDic.get("afterWordsAfterAfterWords") + words[position] + " " + words[position + 1];
            if (DozenWords.contains(words[position+1]))
                return ErrorDic.get("dozenWordsAfterAfterWords") + words[position] + " " + words[position + 1];
            if (words[position + 1].equals("hundred"))
                return ErrorDic.get("hundredAfterAfterWords") + words[position] + " " + words[position + 1];
        }

        if (DozenWords.contains(words[position]))
        {
            if (UnitsWords.contains(words[position + 1]))
                return ErrorDic.get("unitsWordsAfterDozenWords") + words[position] + " " + words[position + 1];
            if (AfterWords.contains(words[position + 1]))
                return ErrorDic.get("afterWordsAfterDozenWords") + words[position] + " " + words[position + 1];
            if (DozenWords.contains(words[position + 1]))
                return ErrorDic.get("dozenWordsAfterDozenWords") + words[position] + " " + words[position + 1];
            if (words[position + 1].equals("hundred"))
                return ErrorDic.get("hundredAfterDozenWords") + words[position] + " " + words[position + 1];
        }

        if (UnitsWords.contains(words[position]))
        {
            if (UnitsWords.contains(words[position + 1]))
                return ErrorDic.get("unitsWordsAfterUnitsWords") + words[position] + " " + words[position + 1];
            if (AfterWords.contains(words[position + 1]))
                return ErrorDic.get("afterWordsAfterUnitsWords") + words[position] + " " + words[position + 1];
            if (DozenWords.contains(words[position + 1]))
                return ErrorDic.get("dozenWordsAfterUnitsWords") + words[position] + " " + words[position + 1];
        }

        return "";
    }

    private int ParseWord(String[] wordArray, int position, int lastposition)
    {
        if (wordArray[position].equals("hundred"))
        {
            if (position == 0)
                return NumberDic.get(wordArray[position]);
            else
                return NumberDic.get(wordArray[position]) * NumberDic.get(wordArray[position - 1]) - NumberDic.get(wordArray[position - 1]);
        }
        else
        {
            return NumberDic.get(wordArray[position]);
        }
    }
    
    public static boolean checkWithRegExp(String regex, String userNameString){  
    	Pattern p = Pattern.compile(regex);
    	Matcher m = p.matcher(userNameString);
    	return m.matches();
    }
    
    public int CountChar(String str, char chr)
    {
    	int count = 0;
    	for (int i = 0; i < str.length(); i++)
    		if (str.charAt(i) == chr)
    			count++;
    	return count;
    }
}
