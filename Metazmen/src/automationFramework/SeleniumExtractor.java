package automationFramework;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.HashMap;
import java.io.*;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.AWTException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import archivesTools.IArchive;
import archivesTools.SevenZip.SevenZipUtil;
import archivesTools.zip.ZipUtil;

/**
 * Class of Extractor by Selenium Technology.
 * Extract all files from the METAZNEM_URL all files of specific exam.
 * In addition extract all files from the archives.
 */
public class SeleniumExtractor implements IExtractor{
	
	private final String _studentDeployPath = "AlgoAutomationTest\\ExternalStudentFiles";
	private final String CHROME_DRIVER = "chromedriver_win32\\chromedriver.exe";
	private final String METAZNEM_URL = "https://exs.ariel.ac.il/";
	private String _testerId;
	private String _testerPassword;
	private String _examinationDate;
	private String _examNum;
	private File _examDir;
	private String _filesPath;
	private RemoteWebDriver _remoteDriver;
	
	public SeleniumExtractor(String testerId, String testerPassword,
			String examinationDate, String examNum)
	{
		initDeployPath();
		_testerId = testerId;
		_testerPassword = testerPassword;
		_examinationDate = examinationDate;
		_examNum = examNum;
	}

	private void initDeployPath() {
		String workingDirectory = System.getProperty("user.dir");
		String seperator = "\\";
		int index = workingDirectory.lastIndexOf(seperator);
		String projectName = "";
		if(index > 0 ){
			projectName = workingDirectory.substring(index + seperator.length());
		}
		_filesPath = workingDirectory.substring(0, workingDirectory.indexOf(projectName)) + _studentDeployPath;
		_examDir = new File(_filesPath);
	}

	@Override
	public void Extract(){
		// Set exam directory
		Boolean isFileCreated = true;
		if (!_examDir.exists()) {
			isFileCreated = isFileCreated && _examDir.mkdirs();
		}
		// if there exist directory for this specific exam - delete
		if (!isFileCreated) {
			JOptionPane.showMessageDialog(new JFrame(),
					"The 'Exam Directory' folder was not created.", "Dialog",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		DownloadFilesFromWebsite();

		ExtractFilesToDestinationFolder();

		RemoveAllFilesDownloaded();

	}

	/**
	 * Download all files of the exam from the server by Selenium Tech.
	 */
	private void DownloadFilesFromWebsite() {
		// Set web driver Chrome property
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);

		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 1);
		chromePrefs.put("download.default_directory", _filesPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-extensions"); // * disable driver
		// extensions *//
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);

		// Create chrome driver instance();
		_remoteDriver = new ChromeDriver(cap);
		
		// Set time out of 20 seconds
		_remoteDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		_remoteDriver.manage().window().maximize();

		// Navigate to exam management web page
		_remoteDriver.get(METAZNEM_URL);
		System.out.println("The browser was navigate to page '"
				+ METAZNEM_URL + "' successfully");

		// Log in the system
		WebElement userIdNumber = _remoteDriver.findElement(By.id("txtUsrId"));
		WebElement testerPasswordword = _remoteDriver.findElement(By.id("txtUsrPassw"));

		userIdNumber.sendKeys(_testerId);
		testerPasswordword.sendKeys(_testerPassword);

		testerPasswordword.sendKeys(Keys.RETURN);
		System.out.println("The user with id '" + _testerId + "' with password '"
				+ _testerPassword + "' was logged into the system");

		// Open the 'exam to over' page
		WebElement examToGoOverBtn = _remoteDriver.findElement(By
				.xpath("//*[@id='pageHead']/span[3]"));
		Actions action = new Actions(_remoteDriver);
		action.moveToElement(examToGoOverBtn).perform();
		examToGoOverBtn.click();
		System.out
		.println("The button 'Exam to over' was click and the 'Exam to over' page was open");

		// Find exam date
		WebElement dateRecord2 = null;
		String currentDate2 = null;
		Boolean isFound = false;
		int index = 1;
		while(!isFound){
			String nodeDateXPath = "//*[@id='node" + index + "']";
			try{
				dateRecord2 = _remoteDriver.findElement(By.xpath(nodeDateXPath));
				if(dateRecord2 == null ) continue;
				currentDate2 = dateRecord2.getText().substring(0, 10).trim();
//				System.out.println(currentDate2);
				if (currentDate2.equals(_examinationDate)) {
					WebElement dateToggle2 = _remoteDriver.findElement(By
							.xpath(nodeDateXPath + "/div[1]"));
					dateToggle2.click();
					System.out.println("The toggle button on date '"
							+ currentDate2 + "' was open");
					isFound = true;
					break;
				}
				else{
					index++;
				}
			}
			catch(NoSuchElementException e){
				break;
			}
		}

		// Find exam number
		if (isFound == true) {
			isFound = false;
			WebElement examRecord2 = null;
			String current_examNum2 = null;
			for (int i = 1; i < 200; i++) {
				String nodeExamXPath = "//*[@id='node" + i + "']";
				_remoteDriver.manage().timeouts()
				.implicitlyWait(50, TimeUnit.NANOSECONDS);
				try {
					examRecord2 = dateRecord2.findElement(By
							.xpath(nodeExamXPath + "/div[2]/span"));
				} catch (Exception e) {
					continue;
				}
				if (examRecord2 != null) {
					current_examNum2 = examRecord2.getText().trim();
					if (current_examNum2.equals(_examNum)) {
						System.out.println("The Exam number '" + _examNum
								+ "' was found");
						WebElement examToggle2 = examRecord2.findElement(By
								.xpath("//*[@id='node" + i + "']/div[1]"));
						examToggle2.click();
						System.out.println("The toggle button of exam number '"
								+ _examNum + "' was open");
						isFound = true;
						break;
					}
				}
			}
			if (isFound == false){
				CloseBrowser("The Exam number '" + _examNum	+ "' was not found");
				return;
			}
		} else {
			CloseBrowser("The Exam date '" + _examinationDate + "'was not found'");
			return;
		}
		_remoteDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		// Find list of classes
		List<WebElement> numOfClasses = _remoteDriver.findElements(By
				.className("nodeContFrame"));
		System.out.println("Number of classes: " + numOfClasses.size());

		// Find students in the class
		for (int i = 0; i < numOfClasses.size(); i++) {
			// Find all class students
			List<WebElement> studentsInClass = numOfClasses.get(i)
					.findElements(By.className("fwNodeLink"));
			int classNumber = i + 1;
			System.out.println("Number of students in class " + classNumber
					+ ": " + studentsInClass.size());

			// Scan all class students
			for (int j = 0; j < studentsInClass.size(); j++) {
				// Select student
				studentsInClass.get(j).click();
				WebElement studentFileWindow = _remoteDriver.findElement(By
						.className("fwDialogFrame"));
				if (studentFileWindow != null) {
					System.out.println("Student '"
							+ studentsInClass.get(j).getText()
							+ "' file window was open");
					// Download exam file
					try {
						WebElement examFileRecoed = studentFileWindow
								.findElement(By
										.xpath("//*[@id='dlg0UpldList']/div/div[1]"));
						examFileRecoed.click();
						System.out.println("Exam file was download");
					} catch (Exception e) {
						System.out.println("For Student '"
								+ studentsInClass.get(j).getText()
								+ "' there was not file exam found!");
					}

					// close student file window by pressing Escape button
					_remoteDriver.manage().timeouts()
					.implicitlyWait(5, TimeUnit.SECONDS);
					try {
						Thread.sleep(1000);
						Robot robot = new Robot();
						robot.keyPress(KeyEvent.VK_ESCAPE);
						robot.keyRelease(KeyEvent.VK_ESCAPE);
						System.out
						.println("The student exam file window was close successfully");
						_remoteDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
						Thread.sleep(1000);
					} catch (AWTException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}
		CloseBrowser(null);
	}

	/**
	 * Close the Browser and print message before(Optional)
	 * @param Message to print before close 
	 */
	private void CloseBrowser(String Message){
		if(Message != null && !Message.equals("")){
			System.out.println(Message);
		}
		_remoteDriver.close();
		_remoteDriver.quit();
	}

	/**
	 * Remove all files that download from the server
	 */
	private void RemoveAllFilesDownloaded() {
		File[] listOfFiles = _examDir.listFiles();
		for (File file : listOfFiles) {
			if(file.isDirectory()) continue;
			if(!file.delete())
				System.out.println(file.getName() + " not deleted!");
		}
	}

	/**
	 * Extract the Archive files.
	 * Currently support of only 7z and ZIP files. 
	 */
	private void ExtractFilesToDestinationFolder() {
		// Extract all files to destination folder
		File[] listOfFiles = _examDir.listFiles();
		IArchive unzipUtil = new ZipUtil();
		IArchive sevenZipUtil = new SevenZipUtil();
		for (File file : listOfFiles) {
			String fileName = file.getName();
			String studentId ;
			String outputPredifx = _filesPath + "\\" ;
			if (fileName.endsWith(".zip")) {
				studentId = fileName.substring(0 , fileName.indexOf(".zip"));
				unzipUtil.Decompress(file.getAbsolutePath() , outputPredifx + studentId);
			} 
			else if(fileName.endsWith(".7z")){
				studentId = fileName.substring(0 , fileName.indexOf(".7z"));
				sevenZipUtil.Decompress(file.getAbsolutePath(), outputPredifx + studentId);
			}
		}
	}
}