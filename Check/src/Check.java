import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;


public class Check {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folderPath = "";
		String fileExt = ".out";
		String fullPathAD;
		String fullPathExcel = folderPath + "Excel Test.csv";
		String fullPathADList = folderPath + "AD ID.csv";
		int countSn = 0;
		int countGivenName = 0;
		int countPhone = 0;
		int countEmail = 0;
		int countPort = 0;
		int countCompany = 0;
		int countGroup = 0;
		int countMissing = 0;

		
		try { 
			PrintStream out = new PrintStream(new FileOutputStream("Check Result.txt"));
			System.setOut(out);
			
			System.out.println("******Starting AD Entry Check******\n");
			//			Read AD ID List
			List<String> adListLines= Files.readAllLines(Paths.get(fullPathADList),
					Charset.defaultCharset());
			adListLines.remove(0);
			//						for (String lineADList: adListLines){
			//							System.out.println(lineADList.split(",",2)[0]);
			//						}
			//			Read Excel File
			List <String> excelListLines= Files.readAllLines(Paths.get(fullPathExcel),
					Charset.defaultCharset());
			excelListLines.remove(0);

			while(!excelListLines.isEmpty()){
				//			Create ADEntry
				ADEntry entry;
				String[] temp;
				//			Read AD File
				for(String adList: adListLines){
					while(excelListLines.get(0).equals("\"")||excelListLines.get(0).equals("")){
						excelListLines.remove(0);
						//					System.out.println("Removed \"");
					}
					temp = excelListLines.get(0).split(",");
					//				System.out.println(excelListLines.get(0));
//					System.out.println(excelListLines.get(0));
					entry = new ADEntry(temp[1].trim(),temp[2].trim(),temp[3].trim(),
							temp[5].trim(),temp[11].trim(),"+("+temp[12].trim()+")"+temp[13].trim()+temp[14].trim());
//					System.out.println(temp[15]);
					entry.addGroup(temp[15].split("\"",2)[1]);
					excelListLines.remove(0);
					//				entry.print();
					//				System.out.println(excelListLines.get(0));
					while(true){
						if(!excelListLines.isEmpty() && Pattern.matches("(ISD).*",excelListLines.get(0)) ){
							entry.addGroup(excelListLines.get(0).split("\"",2)[0].trim());
							excelListLines.remove(0);
						}
						else 
							break;
					}
					//				entry.print();
					String ID = adList.split(",",2)[0];
					fullPathAD = folderPath + "AD/" + ID + fileExt;
					try{
						List<String> adLines = Files.readAllLines(Paths.get(fullPathAD),
								Charset.defaultCharset());

						for (String lineAD : adLines) {
							//					System.out.println(adLines);
							if (Pattern.matches("(sn:).*",lineAD)){
								//						System.out.println(lineAD.substring(4));
								if(!entry.surname.equals(lineAD.substring(4))){
									System.out.println(ID +": Surname is incorrect");
									countSn++;
								}
								entry.surname = null;
							}
							if (Pattern.matches("(givenName:).*",lineAD)){
								//						System.out.println(lineAD.substring(11));
								if(!entry.givenName.equals(lineAD.substring(11))){
									System.out.println(ID+": GivenName is incorrect");
									countGivenName++;
								}
								entry.givenName = null;
							}
							if (Pattern.matches("(company:).*",lineAD)){
								//					System.out.println(lineAD.substring(9));
								if(!entry.company.equals(lineAD.substring(9))){
									System.out.println(ID+": Company is incorrect");
									countCompany++;
								}
								entry.company = null;

							}
							if (Pattern.matches("(cpatrueportcode:).*",lineAD)){
								//					System.out.println(lineAD.substring(17));
								if(!entry.port.equals(lineAD.substring(17))){
									System.out.println(ID+": port is incorrect");
									countPort++;
								}
								entry.port = null;

							}
							if (Pattern.matches("(mail:).*",lineAD)){
								//					System.out.println(lineAD.substring(6));
								if(!entry.email.equalsIgnoreCase(lineAD.substring(6))){
									System.out.println(ID+": email is incorrect");
									countEmail++;
								}
								entry.email = null;

							}
							if (Pattern.matches("(telephoneNumber:).*",lineAD)){
								//						System.out.println(lineAD.substring(17));
								//						System.out.println(lineAD.substring(17));
								//						System.out.println(entry.phone);
								if(!entry.phone.equals(lineAD.substring(17))){
									System.out.println(ID+ ":phone is incorrect");
									countPhone++;
								}
								entry.phone = null;
							}
							if (Pattern.matches("(memberOf: CN=ISD).*",lineAD)){
								//						System.out.println(lineAD.substring(13).split(",",2)[0]);
								if(entry.group.contains(lineAD.substring(13).split(",",2)[0])){
									//							System.out.println(lineAD.substring(13).split(",",2)[0]);
									entry.group.remove(lineAD.substring(13).split(",",2)[0]);
								}
//								else{
//									System.out.println(ID+": Group is incorrect");
//									//							System.out.println(lineAD.substring(13).split(",",2)[0]);
//									countGroup++;
//								}
							}
						}
						if(!entry.group.isEmpty()){
							System.out.println(ID+ ": Group attribute incorrect");
							countGroup++;
						}
						if(entry.surname != null){
							System.out.println(ID +": Surname is missing");
							countSn++;
						}
						if(entry.givenName != null){
							System.out.println(ID +": GivenName is missing");
							countGivenName++;
						}
						if(entry.company != null){
							System.out.println(ID +": Company is missing");
							countCompany++;
						}
						if(entry.port != null){
							System.out.println(ID +": port is missing");
							countPort++;
						}
						if(entry.email != null){
							System.out.println(ID +": Email is missing");
							countEmail++;
						}
						if(entry.phone != null){
							System.out.println(ID +": Phone is missing");
							countPhone++;
						}
						}catch (IOException e1){
						System.out.println(ID + ": AD file not found");
						countMissing++;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

}
