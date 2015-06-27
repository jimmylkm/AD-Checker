import java.util.ArrayList;


public class ADEntry {
	public String surname;
	public String givenName;
	public String company;
	public String port;
	public String phone;
	public String email;
	public ArrayList<String> group;

	public ADEntry(String surname, String givenName, String company, String port, String email, String phone){
		this.surname = surname;
		this.givenName = givenName;
		this.company = company;
		this.port = port;
		this.phone = phone;
		this.email = email;
		group = new ArrayList<String>();
	}

	public void addGroup(String group){
		if(group != null){
			this.group.add(group);
		}
	}

	public void print(){
		System.out.println(surname);
		System.out.println(givenName);
		System.out.println(company);
		System.out.println(port);
		System.out.println(phone);
		System.out.println(email);
		if(group != null){
			for(String line: group){
				System.out.println(line);
			}
		}
	}

	public void printGroup(){
		if(group != null){
			for(String line: group){
				System.out.println(line);
			}
		}
	}
}
