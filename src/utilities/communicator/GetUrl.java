package utilities.communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUrl{
	private String url;
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
	
}
