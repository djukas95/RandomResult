package Main;

public class TeamMark {
	private String name = "";
	
	private double mHome = 0;
	private double mHomeRec = 0;
	private double mHomeAch = 0;

	private double mGuest = 0;
	private double mGuestRec = 0;
	private double mGuestAch = 0;
	public TeamMark(String name, int mPlayed, int rec, int ach, int points)
	{
		setName(name);
		setmHome(((1.0f*points/mPlayed)*50 + (1.0f*rec/mPlayed - 1.0f*ach/mPlayed)*25 + 75)/3);
	}
	public void setGuest(String name, int mPlayed, int rec, int ach, int points)
	{
		setmGuest(((1.0f*points/mPlayed)*50 + (1.0f*rec/mPlayed - 1.0f*ach/mPlayed)*25 + 75)/3);
	}
	public double getmHome() {
		return mHome;
	}
	public void setmHome(double mHome) {
		this.mHome = mHome;
	}
	public double getmHomeRec() {
		return mHomeRec;
	}
	public void setmHomeRec(double mHomeRec) {
		this.mHomeRec = mHomeRec;
	}
	public double getmHomeAch() {
		return mHomeAch;
	}
	public void setmHomeAch(double mHomeAch) {
		this.mHomeAch = mHomeAch;
	}
	public double getmGuest() {
		return mGuest;
	}
	public void setmGuest(double mGuest) {
		this.mGuest = mGuest;
	}
	public double getmGuestRec() {
		return mGuestRec;
	}
	public void setmGuestRec(double mGuestRec) {
		this.mGuestRec = mGuestRec;
	}
	public double getmGuestAch() {
		return mGuestAch;
	}
	public void setmGuestAch(double mGuestAch) {
		this.mGuestAch = mGuestAch;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
