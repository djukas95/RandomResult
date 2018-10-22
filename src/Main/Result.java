package Main;

public class Result {
	private String first = "";
	private String second = "";
	private int fScore = 0;
	private int sScore = 0;
	public Result(String first, String second, int fScore, int sScore)
	{
		this.first = first;
		this.second = second;
		this.fScore = fScore;
		this.sScore = sScore;
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

}
