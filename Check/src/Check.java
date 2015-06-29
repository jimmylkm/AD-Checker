import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Check {

	public static void main(String[] args) {
		String folderPath = "";
		String adFileExt = ".out";
		String entryFileExt = ".csv";
		String fullPathAD;
		String fullPathExcel;
		String excelName;
		String adFilePath = "";
		
		int countSn = 0;
		int countGivenName = 0;
		int countPhone = 0;
		int countEmail = 0;
		int countPort = 0;
		int countCompany = 0;
		int countGroup = 0;
		int countMissing = 0;

		JFrame frame = new JFrame();
		
		excelName = JOptionPane.showInputDialog(frame,"Enter filename for Excel Entries",
				"AD Checker", JOptionPane.QUESTION_MESSAGE);
		fullPathExcel = folderPath + excelName.trim() + entryFileExt;
		
		adFilePath = JOptionPane.showInputDialog(frame,"Enter child filename containing AD files (Press Enter if AD files are in the same directory)",
				"AD Checker", JOptionPane.QUESTION_MESSAGE).trim();
		if(!adFilePath.equals(""))
			adFilePath = adFilePath + "/";
		
		try { 
			PrintStream out = new PrintStream(new FileOutputStream("Check Result.txt"));
			System.setOut(out);
			
			System.out.println("******Starting AD Entry Check******\n");
			
//			Read Excel File
			List <String> excelListLines= Files.readAllLines(Paths.get(fullPathExcel),
					Charset.defaultCharset());
			
//			Parse the position for item to be compared
			int position = 0;
			int surname = -1;
			int givenName = -1;
			int company = -1;
			int port = -1;
			int countryCode = -1;
			int areaCode = -1;
			int phone = -1;
			int email = -1;
			int group = -1;
			int galaxyID = -1;
			
			String[] adAttribute = excelListLines.get(0).split("\\|");
			for(String attribute: adAttribute){
				attribute = attribute.trim();
				switch(attribute){
				case("Surname"):
					surname = position;
					break;
				case("First Name"):
					givenName = position;
					break;
				case("Working Company"):
					company = position;
					break;
				case("Port"):
					port = position;
					break;
				case("Country Code"):
					countryCode = position;
					break;
				case("Area Code"):
					areaCode = position;
					break;
				case("Phone Num"):
					phone = position;
					break;
				case("Email"):
					email = position;
					break;
				case("User Group (11. Group Profile)"):
					group = position;
					break;
				case("GalaCXy ID"):
					galaxyID = position;
					break;
				default:
				}
				position++;
			}
			if(surname == -1 || givenName == -1 || company == -1 || port == -1 ||
					countryCode == -1 || areaCode == -1 || phone == -1 || email == -1 || group == -1 || galaxyID == -1){
				System.out.println("One of the required attribute not found in excel file!");
				System.out.println(surname);
				System.out.println(givenName);
				System.out.println(company);
				System.out.println(port);
				System.out.println(countryCode);
				System.out.println(areaCode);
				System.out.println(phone);
				System.out.println(email);
				System.out.println(group);
				System.out.println(galaxyID);
				System.exit(-1);
			}
			excelListLines.remove(0);
			
//			Counter to show which row is wrong
			int rowCount = 0;

			while(!excelListLines.isEmpty()){
//				Create ADEntry
				ADEntry entry;
				String[] temp;
				rowCount++;
				//			Read AD File
					while(Pattern.matches("\".*",excelListLines.get(0).trim())||excelListLines.get(0).equals("")){
						excelListLines.remove(0);
					}
					temp = excelListLines.get(0).split("\\|");
					entry = new ADEntry(temp[surname].trim(),temp[givenName].trim(),temp[company].trim(),
							temp[port].trim(),temp[email].trim(),"+("+temp[countryCode].trim()+")"+temp[areaCode].trim()+temp[phone].trim());
					entry.addGroup(temp[group].split("\"",2)[1].trim());
					excelListLines.remove(0);
					
					while(true){
						if(!excelListLines.isEmpty() && Pattern.matches("(ISD).*",excelListLines.get(0)) ){
							entry.addGroup(excelListLines.get(0).split("\"",2)[0].trim());
							excelListLines.remove(0);
						}
						else 
							break;
					}
					String ID = temp[galaxyID];
					fullPathAD = folderPath + adFilePath + ID + adFileExt;
					try{
						List<String> adLines = Files.readAllLines(Paths.get(fullPathAD),
								Charset.defaultCharset());

						for (String lineAD : adLines) {
							lineAD = lineAD.trim();
							if (Pattern.matches("(sn:).*",lineAD)){
								if(!entry.surname.equals(lineAD.substring(4).trim())){
									System.out.println(ID + " (Row " + rowCount +") : Surname is incorrect");
									countSn++;
								}
								entry.surname = null;
							}
							if (Pattern.matches("(givenName:).*",lineAD)){
								if(!entry.givenName.equals(lineAD.substring(11).trim())){
									System.out.println(ID + " (Row " + rowCount +") : GivenName is incorrect");
									countGivenName++;
								}
								entry.givenName = null;
							}
							if (Pattern.matches("(company:).*",lineAD)){
								if(!entry.company.equals(lineAD.substring(9).trim())){
									System.out.println(ID + " (Row " + rowCount +") : Company is incorrect");
									countCompany++;
								}
								entry.company = null;

							}
							if (Pattern.matches("(cpatrueportcode:).*",lineAD)){
								if(!entry.port.equals(lineAD.substring(17).trim())){
									System.out.println(ID + " (Row " + rowCount +") : Port is incorrect");
									countPort++;
								}
								entry.port = null;

							}
							if (Pattern.matches("(mail:).*",lineAD)){
								if(!entry.email.equalsIgnoreCase(lineAD.substring(6).trim())){
									System.out.println(ID + " (Row " + rowCount +") : Email is incorrect");
									countEmail++;
								}
								entry.email = null;

							}
							if (Pattern.matches("(telephoneNumber:).*",lineAD)){
								if(!entry.phone.equals(lineAD.substring(17).trim())){
									System.out.println(ID + " (Row " + rowCount +") : Phone is incorrect");
									countPhone++;
								}
								entry.phone = null;
							}
							if (Pattern.matches("(memberOf: CN=ISD).*",lineAD)){
								if(entry.group.contains(lineAD.substring(13).split(",",2)[0].trim())){
									entry.group.remove(lineAD.substring(13).split(",",2)[0]);
								}
						}
						}
						if(!entry.group.isEmpty()){
							System.out.println(ID + " (Row " + rowCount +") : Group attribute incorrect");
							countGroup++;
						}
						if(entry.surname != null){
							System.out.println(ID + " (Row " + rowCount +") : Surname is missing");
							countSn++;
						}
						if(entry.givenName != null){
							System.out.println(ID + " (Row " + rowCount +") : GivenName is missing");
							countGivenName++;
						}
						if(entry.company != null){
							System.out.println(ID + " (Row " + rowCount +") : Company is missing");
							countCompany++;
						}
						if(entry.port != null){
							System.out.println(ID + " (Row " + rowCount +") : Port is missing");
							countPort++;
						}
						if(entry.email != null){
							System.out.println(ID + " (Row " + rowCount +") : Email is missing");
							countEmail++;
						}
						if(entry.phone != null){
							System.out.println(ID + " (Row " + rowCount +") : Phone is missing");
							countPhone++;
						}
						}catch (IOException e1){
						System.out.println(ID + " (Row " + rowCount +") : AD file not found");
						countMissing++;
					}
			}
		} catch (IOException e) {
			System.out.println("Excel file not found");
			System.out.println("\n******Ending AD Entry Check******");
			System.exit(-1);
		}
		System.out.println("\nMissing File: "+countMissing);
		System.out.println("Error in Surname: "+countSn);
		System.out.println("Error in GivenName: "+countGivenName);
		System.out.println("Error in Port: "+countPort);
		System.out.println("Error in Email: "+countEmail);
		System.out.println("Error in Phone: "+countPhone);
		System.out.println("Error in Company: "+countCompany);
		System.out.println("Error in Group: "+countGroup);
		System.out.println("\n******Ending AD Entry Check******");
		System.exit(0);
	}
}

