package Main;

public class Match {
	private String first = "";
	private String second = "";
	private String date = "";
	private int fScore = 0;
	private int sScore = 0;
	private String result = "";
	public Match(String first, String second, String date, String result) {
		this.first = first;
		this.second = second;
		this.setDate(date);
		this.setResult(result);
		this.fScore = Integer.parseInt(result.substring(0,result.indexOf(":")));
		this.sScore = Integer.parseInt(result.substring(result.indexOf(":")+1, result.length()));
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public int getfScore() {
		return fScore;
	}
	public void setfScore(int fScore) {
		this.fScore = fScore;
	}
	public int getsScore() {
		return sScore;
	}
	public void setsScore(int sScore) {
		this.sScore = sScore;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
