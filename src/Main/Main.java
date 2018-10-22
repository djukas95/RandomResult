package Main;

import java.net.URL;

public class Main {

	public static void main(String[] args) throws Exception{
		URL url = new URL("https://www.rezultati.com/kosarka/francuska/lnb/");
		TeamFunc teamFunc = new TeamFunc(url);
	}

}