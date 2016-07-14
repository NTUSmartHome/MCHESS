package utilities.communicator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUrl{
	private String url;
	private String STurl;
	private String version;
	private int[] device = new int[5];
	
	public GetUrl() throws IOException{
		url = "http://140.112.49.140/cgi-bin/proxy?cmd=/v0,0";;
		get();
	}
	
	private void getLightStrength(String str){
		String checkVersion = str.substring(0, 2);
		if(!checkVersion.equals(version)){
			version = checkVersion;
			int l = (str.length()-2)/6;
			boolean[] mask = new boolean[5];
			for(int i=0; i<mask.length; i++)
				mask[i] = false;
			for(int i=0, p=2; i<l; i++, p+=6){
				String room = str.substring(p, p+2);
				if(room.equals("01")){
					int id = Integer.parseInt(str.substring(p+2, p+4),16)-16;
					if(id>=1 && id<=5 && !mask[id-1]){
						device[id-1] = Integer.parseInt(str.substring(p+4, p+6),16);
						mask[id-1] = true;
					}
				} 
			}
		}
	}
	
	public boolean controlDeviceLight(int id, int value) throws IOException{
		url = "http://140.112.49.140/cgi-bin/proxy?cmd=/r"; 
		String device = Integer.toHexString((id+16));
		String strength = Integer.toHexString(value);
		String hex = strength + device + "01";
		url += Integer.parseInt(hex,16);
		url += ",0";
		String success = get();
		if(success.equals("00"))
			return true;
		return false;
	}
	
	public int getOneDeviceLight(int id) throws IOException{
		url = "http://140.112.49.140/cgi-bin/proxy?cmd=/v0,0";
		String info = get();
		getLightStrength(info);
		return device[id-1];
	}
	
	public int[] getDeviceLight() throws IOException{
		url = "http://140.112.49.140/cgi-bin/proxy?cmd=/v0,0";
		String info = get();
		getLightStrength(info);
		return device;
	}

	private String get() throws IOException{
		URL u = new URL(url);
		HttpURLConnection http = (HttpURLConnection) u.openConnection();
		http.setRequestMethod("GET");
		String str = "";
		if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = http.getInputStream();
			Reader in = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(in);
			str = br.readLine();
			br.close();
			in.close();
			is.close();
		}
		http.disconnect();

		return str;
	}
	private String getid(String id) throws IOException{
		String ID = "";
		if(id.equals("PC"))
			ID = "116fd273-95d0-406c-a628-fcd6eba18da4";
		if(id.equals("TV"))
			ID = "3ce7b4ac-cebe-4e01-83ad-2adfeda01bea";
		if(id.equals("GLAMP"))
			ID = "11f293c3-c1ce-4b56-bacf-272db0e640c8";
		if(id.equals("LFAN"))
			ID = "4357fc90-384a-4349-b18f-b2ce4f4cfe40";
		if(id.equals("NLAMP"))
			ID = "8d1b71e0-b2dd-4d58-8c8d-9ffcf202997d";
		if(id.equals("XBOX"))
			ID = "712b6cc4-e8b9-471f-b491-81d31eb7bc8c";

		return ID;
	}
	private String controlST(String id, int value) throws IOException{
		String  ID = getid(id);
		STurl = "https://graph.api.smartthings.com/api/smartapps/installations/b5714e7a-be05-4b51-8f03-3fec10b1498a/switches/"+ID+"/toggle/?access_token=980cab46-f41b-46c2-ab1f-c26da6349c91";

		URL u = new URL(STurl);
		HttpURLConnection http = (HttpURLConnection) u.openConnection();
		http.setRequestMethod("GET");
		String str = "";
		if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = http.getInputStream();
			Reader in = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(in);
			str = br.readLine();
			br.close();
			in.close();
			is.close();
		}
		http.disconnect();

		return str;
	}
	
}
