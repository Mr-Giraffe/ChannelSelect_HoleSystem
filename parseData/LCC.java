import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LCC {
	
	private static final int CHANNEL_COUNT = 3;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName = "";
		String folderPath = "/home/beacon/ChannelSelect_wholeSystem/data/e"+args[0]+"/";
		int[] idleCount = new int[CHANNEL_COUNT];
		for(int i=0;i<CHANNEL_COUNT;i++){
			idleCount[i] = 0;
		}
		for(int i=1;i<args.length;i++){
			fileName = args[i]+".txt";
			String filePath = folderPath + fileName;
			BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
			String tempLine = "";
			while((tempLine = br.readLine()) != null){
				String[] tempArray = tempLine.split(" ");
				for(int j=0;j<tempArray.length;j++){
					int tempData = Integer.parseInt(tempArray[j]);
					if(tempData<-90){
						idleCount[i-1]++;
					}
				}
			}
			br.close();
		}
		int maxCount = idleCount[0];
		int maxIndex = 0;
		System.out.println(maxIndex + " : " + maxCount);
		for(int i=1;i<CHANNEL_COUNT;i++){
			System.out.println(i+":"+idleCount[i]);
			if(idleCount[i]>maxCount){
				maxCount = idleCount[i];
				maxIndex = i;
			}
		}
		System.out.println(maxIndex + " : " + maxCount);
		
	}

}
