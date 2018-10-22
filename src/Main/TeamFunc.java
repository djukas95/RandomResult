package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.TableRowGroup;

public class TeamFunc {
	private URL groupUrl = null;

	public TeamFunc(URL groupUrl) throws Exception {
		this.groupUrl = groupUrl;

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		getTeamMark();

	}

	private void getTeamMark() throws Exception {

		final WebClient webClient = new WebClient(BrowserVersion.CHROME);

		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setRedirectEnabled(false);
		webClient.getOptions().setAppletEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setPopupBlockerEnabled(true);
		webClient.getOptions().setActiveXNative(true);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());

		List<Result> results = getSecondMark(webClient, null);

		HtmlDivision tableDiv2 = null;
		HtmlDivision tableDiv = null;
		try {

			HtmlPage page = webClient.getPage(groupUrl);
			webClient.waitForBackgroundJavaScript(20000);
			// System.out.println(page.asText());
			tableDiv = page.getHtmlElementById("glib-stats-data");
			// System.out.println(tableDiv.asText());

			HtmlAnchor contacts = page.getAnchorByHref("#table;home");
			page = contacts.click();
			webClient.waitForBackgroundJavaScript(8000);
			tableDiv = page.getHtmlElementById("glib-stats-data");
			// System.out.println(tableDiv.asText());

			HtmlPage page2 = webClient.getPage(groupUrl);
			webClient.waitForBackgroundJavaScript(20000);
			HtmlAnchor contacts2 = page2.getAnchorByHref("#table;away");
			page2 = contacts2.click();
			webClient.waitForBackgroundJavaScript(8000);
			tableDiv2 = page2.getHtmlElementById("glib-stats-data");
			// System.out.println(tableDiv.asText());
		} catch (Exception e) {
		}
		List<TeamMark> teamMarks = getTeamMarkList(tableDiv, tableDiv2);

		updateTeamMark(teamMarks, results);

		List<Match> matchs = getSchedule(webClient, teamMarks);

		webClient.close();

		for (TeamMark tm : teamMarks) {
			System.out.println(tm.getName() + " " + tm.getmHome() + " " + tm.getmHomeAch() + " " + tm.getmHomeRec()
					+ " " + tm.getmGuest() + " " + tm.getmGuestAch() + " " + tm.getmGuestRec());
		}

		for (Match match : matchs) {
			System.out.println(match.getFirst() + " " + match.getResult() + " " + match.getSecond());
		}

	}

	private List<Match> getSchedule(WebClient webClient, List<TeamMark> teamMarks) throws Exception {
		List<Match> matchs = new ArrayList<>();
		HtmlPage page = webClient.getPage(groupUrl.toString() + "raspored/");
		webClient.waitForBackgroundJavaScript(10000);
		HtmlDivision div = page.getHtmlElementById("fs-fixtures");
		List<HtmlTableRow> tableRows = (List<HtmlTableRow>) div.getByXPath("//tr");
		int i;
		String first = "";
		String second = "";
		String date = "";
		String result = "";
		int fScore = 0;
		int sScore = 0;
		for (HtmlTableRow tableRow : tableRows) {
			i = 0;
			for (HtmlTableCell tableCell : tableRow.getCells()) {
				if (tableCell.asText().endsWith("kolo"))
					break;
				if (i == 1)
					date = tableCell.asText();
				if (i == 2)
					first = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
				if (i == 3)
					second = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
				if (i == 4) {
					result = getScore(teamMarks, first, second);
					Match match = new Match(first, second, date, result);
					matchs.add(match);
				}
				i++;
			}
		}
		return matchs;
	}

	private String getScore(List<TeamMark> teamMarks, String first, String second) {
		double homeRec = 0;
		double homeAch = 0;
		double mHome = 0;
		double guestRec = 0;
		double guestAch = 0;
		double mGuest = 0;
		boolean home = false;
		boolean guest = false;
		double fScore = 0;
		double sScore = 0;
		long ffScore = 0;
		long fsScore = 0;
		double temp = 0;
		for (TeamMark teamMark : teamMarks) {
			if (teamMark.getName().equals(first) && !home) {
				homeRec = teamMark.getmHomeRec();
				homeAch = teamMark.getmHomeAch();
				mHome = teamMark.getmHome();
				home = true;
			}
			if (teamMark.getName().equals(second) && !guest) {
				guestRec = teamMark.getmGuestRec();
				guestAch = teamMark.getmGuestAch();
				mGuest = teamMark.getmGuest();
				guest = true;
			}
			if (home && guest)
				break;
		}
		fScore = (homeAch / mGuest + guestRec / mHome) / 2;
		sScore = (guestAch / mHome + homeRec / mGuest) / 2;

		while (fScore < 0 || sScore < 0) {
			fScore++;
			sScore++;
		}
		temp = (fScore - (long) fScore) - (sScore - (long) sScore);
		System.out.println(fScore + " : " + sScore);
		ffScore = (long) fScore;
		fsScore = (long) sScore;
		sScore = fsScore - temp;
		fsScore = (long) Math.round(sScore);
		if (ffScore > 110) {
			ffScore /= 2;
			fsScore /= 2;
		}
		// System.out.println(ffScore + " : " + fsScore);
		return ((int) ffScore) + ":" + ((int) fsScore);
	}

	private void updateTeamMark(List<TeamMark> teamMarks, List<Result> results) {
		for (TeamMark tm : teamMarks) {
			for (Result res : results) {
				if (res.getFirst().equals(tm.getName())) {
					tm.setmHomeAch(tm.getmHomeAch() > res.getfScore() * getScore(teamMarks, res.getSecond(), false)
							? tm.getmHomeAch()
							: ((res.getfScore() * getScore(teamMarks, res.getSecond(), false)) + tm.getmHomeAch()) / 2);
					tm.setmHomeRec(
							tm.getmHomeRec() > res.getsScore() * (100 - getScore(teamMarks, res.getSecond(), false))
									? tm.getmHomeRec()
									: (tm.getmHomeRec()
											+ (res.getsScore() * (100 - getScore(teamMarks, res.getSecond(), false))))
											/ 2);
				}
				if (res.getSecond().equals(tm.getName())) {
					tm.setmGuestAch(tm.getmGuestAch() > res.getsScore() * getScore(teamMarks, res.getFirst(), true)
							? tm.getmGuestAch()
							: (tm.getmGuestAch() + (res.getsScore() * getScore(teamMarks, res.getFirst(), true))) / 2);
					tm.setmGuestRec(
							tm.getmGuestRec() > res.getfScore() * (100 - getScore(teamMarks, res.getFirst(), true))
									? tm.getmGuestRec()
									: (tm.getmGuestRec()
											+ (res.getfScore() * (100 - getScore(teamMarks, res.getFirst(), true))))
											/ 2);
				}
			}
		}
	}

	private double getScore(List<TeamMark> teamMarks, String name, boolean isHome) {
		for (TeamMark tm : teamMarks) {
			if (tm.getName().equals(name)) {
				if (isHome)
					return tm.getmHome();
				else
					return tm.getmGuest();
			}
		}
		return 0;
	}

	private List<Result> getSecondMark(WebClient webClient, List<TeamMark> teamMarks) throws Exception {
		HtmlPage page = webClient.getPage(groupUrl.toString() + "rezultati/");
		webClient.waitForBackgroundJavaScript(10000);
		HtmlDivision div = page.getHtmlElementById("fs-results");
		List<HtmlTableRow> tableRows = (List<HtmlTableRow>) div.getByXPath("//tr");
		int i;
		String first = "";
		String second = "";
		int fScore = 0;
		int sScore = 0;
		String pom = "";
		List<Result> results = new ArrayList<>();
		for (HtmlTableRow tableRow : tableRows) {
			i = 0;

			for (HtmlTableCell tableCell : tableRow.getCells()) {
				if (tableCell.asText().endsWith("kolo"))
					break;
				if (i == 2)
					first = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
				if (i == 3)
					second = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
				if (i == 4) {
					pom = tableCell.asText();
					if (pom.contains("("))
						pom = pom.substring(0, pom.indexOf("(") - 1);
					fScore = Integer.parseInt(pom.substring(0, pom.indexOf(" : ")));
					sScore = Integer.parseInt(pom.substring(pom.indexOf(" : ") + 3, pom.length()));
				}
				if (i == 5) {
					Result result = new Result(first, second, fScore, sScore);
					results.add(result);
				}
				i++;
			}
		}

		/*
		 * for(Result result : results) { System.out.println(result.getFirst() +
		 * " " + result.getSecond() + " " +
		 * result.getfScore()+":"+result.getsScore()); }
		 */
		return results;
	}

	private List<TeamMark> getTeamMarkList(HtmlDivision statsData, HtmlDivision statsData2) {
		String name = "";
		int mPlayed = 0;
		String goals = "";
		int rec = 0;
		int ach = 0;
		String pom = "";
		int points = 0;
		int i;
		boolean is = false;
		List<TeamMark> teamMarks = new ArrayList<>();
		List<HtmlTableRow> tableRows = (List<HtmlTableRow>) statsData.getByXPath("//tr");
		for (HtmlTableRow tableRow : tableRows) {
			i = 0;
			for (HtmlTableCell tableCell : tableRow.getCells()) {
				try {

					if (tableCell.asText().startsWith("Forma"))
						is = true;
					if (!is)
						continue;
					if (i == 1) {
						name = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
					} else if (i == 2)
						mPlayed = Integer.parseInt(tableCell.asText());
					else if (i == 6) {
						goals = tableCell.asText();
						if (!goals.contains(":")) {
							i++;
							continue;
						}
						rec = Integer.parseInt(goals.substring(0, goals.indexOf(':')));
						ach = Integer.parseInt(goals.substring(goals.indexOf(':') + 1, goals.length()));
					} else if (i == 7)
						points = Integer.parseInt(tableCell.asText());
					else if (i == 8) {
						TeamMark tm = new TeamMark(name, mPlayed, rec, ach, points);
						teamMarks.add(tm);
					}
				} catch (Exception e) {
				}
				i++;
			}
		}
		is = false;
		tableRows = (List<HtmlTableRow>) statsData2.getByXPath("//tr");
		for (HtmlTableRow tableRow : tableRows) {
			i = 0;
			for (HtmlTableCell tableCell : tableRow.getCells()) {
				try {
					if (tableCell.asText().startsWith("Forma"))
						is = true;
					if (!is)
						continue;
					if (i == 1) {
						name = tableCell.asText().replaceAll("[^a-zA-Z0-9]+", "");
					} else if (i == 2)
						mPlayed = Integer.parseInt(tableCell.asText());
					else if (i == 6) {
						goals = tableCell.asText();
						rec = Integer.parseInt(goals.substring(0, goals.indexOf(':')));
						ach = Integer.parseInt(goals.substring(goals.indexOf(':') + 1, goals.length()));
					} else if (i == 7)
						points = Integer.parseInt(tableCell.asText());
					else if (i == 8) {
						for (TeamMark team : teamMarks) {
							if (team.getName().equals(name)) {
								team.setGuest(name, mPlayed, rec, ach, points);
								break;
							}
						}
					}
				} catch (Exception e) {
				}
				i++;
			}
		}
		return teamMarks;
	}

}
