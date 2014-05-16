import java.io.*;
import java.util.*;

public class HandleRawData {
	//
	private final static int HOLE_LENGTH = 4000; 
	private final static int DEVICE_NUM = 4;
	private final static int AP_NUM = 2;
	//private final double OCCUPY_FROM_TSHARK = 4.9522;//tshark 
	private final static int CHANNEL_NUM = 3;
	private final static double INTEFER_THRESHHOLD = 0.4;
	//
	private final static int map_delta = 2;//rssi
	private final static int time_delta = 100;//
	private final static double cor_delta = 0.5;//
	//telosb
	private int nodeNum = 0;
	private int[][] rawData;
	private int[][] hotData;
	private int[] nodeList;
	//radiomap
	private int[][] radioMap;
	//
	private double[] weight;//
	private int[] wCount;//
	
	//
	private int[] occupy_count;//
	private int[][] idle_time;
    private double[][] correlation;//
    private double occupy_percentage;//
    //
    //HashMap<Integer, HashSet<Integer>> interferenceSet = new HashMap<Integer, HashSet<Integer>>();
	ArrayList<HashSet<Integer>> interferenceSet =  new ArrayList<HashSet<Integer>>(DEVICE_NUM);
	//private int[] theta; 
	Device[] devices;
	
	private int currentChannel;
	private int[] bestChannel;
	private int[] tempAssignment;
	private double[] tempCellInterference;
	private double[] minCellInterference;
	
	public HandleRawData(int num){
		nodeNum = num/CHANNEL_NUM;
		nodeList = new int[nodeNum];
		devices = new Device[DEVICE_NUM];
		for(int i=0;i<DEVICE_NUM;i++){
			devices[i] = new Device();
			HashSet<Integer> hs = new HashSet<Integer>();
			interferenceSet.add(hs);
		}
		radioMap = new int[nodeNum][DEVICE_NUM];
		rawData = new int[nodeNum][HOLE_LENGTH];
		hotData = new int[nodeNum][HOLE_LENGTH];
        correlation = new double[nodeNum][nodeNum];
        weight = new double[nodeNum];
        wCount = new int[nodeNum];
		occupy_count = new int[nodeNum];
		idle_time = new int[nodeNum][CHANNEL_NUM];
		bestChannel = new int[AP_NUM];
		tempAssignment = new int[AP_NUM];
		tempCellInterference = new double[AP_NUM];
		minCellInterference = new double[AP_NUM];
		//theta = new int[nodeNum];
	}
	//
	private void initialize(int channel){
		occupy_percentage = 0;
        currentChannel = channel;
        for(int i=0;i<nodeNum;i++){
        	nodeList[i] = (currentChannel+1) + i*CHANNEL_NUM;
        }
		for(int i=0;i<DEVICE_NUM;i++){
			devices[i].setDevice_number(i+1);
			devices[i].setCurrent_channel(0);
			/*if(i<3){
				devices[i].setCell_number(1);
			}else{
				devices[i].setCell_number(2);
			}*/
		}
		for(int i=0;i<AP_NUM;i++){
			bestChannel[i] = 0;
			tempAssignment[i] = 0;
			tempCellInterference[i] = 1000.1;
			minCellInterference[i] = 1000.0;
		}
		devices[0].setCell_number(0);
		devices[1].setCell_number(0);
		devices[2].setCell_number(1);
		devices[3].setCell_number(1);
		for(int i=0;i<nodeNum;i++){
			occupy_count[i] = 0;
            correlation[i][i] = 1;
            weight[i] = 0;
            wCount[i] = 0;
            for(int j=0;j<CHANNEL_NUM;j++){
            	idle_time[i][j] = 0;
            }
            for(int j=0;j<HOLE_LENGTH;j++){
            	rawData[i][j] = 0;
            	hotData[i][j] = 0;
            }
            for(int j=0;j<DEVICE_NUM;j++){
            	radioMap[i][j] = 0;
            	if(i != j)
            		correlation[i][j] = 0;
            }
		}
	}
	
	
	//
	public void readRawDataIn(String foldPath) throws IOException{
		for(int i=0;i<nodeNum;i++){
			int readIndex = 0;
			String tempLine = "";
			String fileName = foldPath + nodeList[i] + ".txt";
			BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)));
			while((tempLine = bf.readLine()) != null && readIndex < HOLE_LENGTH){
				String[] tempL = tempLine.split(" ");
				for(int j=0;j<tempL.length;j++){
					rawData[i][readIndex] = Integer.parseInt(tempL[j]);
					//System.out.print(rawData[i][readIndex]+" ");
					if(rawData[i][readIndex] < -90){
						idle_time[i][currentChannel]++;
					}
					readIndex++;
				}
			}
			bf.close();
		}
	}
	//
	public void readMapIn(String fileName) throws IOException{
		String tempLine = "";
		BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)));
		for(int i=0;i<nodeNum;i++){
			if((tempLine = bf.readLine()) != null){
				String[] tempL = tempLine.split(" ");
				int tempNode = Integer.parseInt(tempL[0]);
				if(tempNode < nodeList[i]) {i = i - 1;continue;}
				for(int j=0;j<DEVICE_NUM;j++){
					radioMap[i][j] = Integer.parseInt(tempL[j+1]);
				}
			}
		}
		bf.close();
	}
	
	//
	private boolean isNear(int a, int b){
		if((a - b) > map_delta||(b - a) > map_delta) return false;
		return true;
	}
	//
	private void changeRawToHot(int deviceNum){
		for(int i=0;i<nodeNum;i++){
			int tempMap = radioMap[i][deviceNum];
			for(int j=0;j<HOLE_LENGTH;j++){
				if(isNear(rawData[i][j], tempMap)){
					hotData[i][j] = 1;
					occupy_count[i]++;
				}else{
					hotData[i][j] = 0;
				}
			}
		}
	}

	//
    private double corr(int a, int b){
        double cor = corrEqution(hotData[a], hotData[b], occupy_count[a], occupy_count[b], HOLE_LENGTH);
        //System.out.println("a:"+a+" b:"+b+" cor: "+cor);
        int la = 0, lb = 0;
        int count = 0;
        while(count < 2){
            if(count == 1){
                int t = a;
                a = b;
                b = t;
            }
            count++;
            for(int i=1;i<=time_delta;i++){
                int[] line_a = new int[HOLE_LENGTH-i];
                System.arraycopy(hotData[a], i, line_a, 0, HOLE_LENGTH-i);
                for(int j=0;j<i;j++){
                    la = occupy_count[a] - hotData[a][j];
                    lb = occupy_count[b] - hotData[b][HOLE_LENGTH-j-1];
                }
                double tempC = corrEqution(line_a, hotData[b], la, lb, HOLE_LENGTH - i);
                //System.out.println("a:"+a+" b:"+b+" tempC: "+tempC);
                if(tempC>cor) cor = tempC;
            }
        }
        return cor;  
    }
    
    private double corrEqution(int[] a, int[] b, int la, int lb, int length){
        double ave_a, ave_b, sum_a = 0, sum_b = 0, sum_cor = 0;
        ave_a = la*1.0/length;
        ave_b = lb*1.0/length;
        for(int i=0;i<length;i++){
            sum_a += Math.pow(a[i]-ave_a, 2);
            sum_b += Math.pow(b[i]-ave_b, 2);
            sum_cor += (a[i]-ave_a)*(b[i]-ave_b);
        }
        double d_ab = Math.sqrt(sum_a*sum_b);
        if(d_ab != 0) return sum_cor/d_ab;
        else return 0;
    }

    private void oneDeviceCorrs(int device){
    	
        changeRawToHot(device);
        for(int j=0;j<nodeNum;j++){
                for(int k=0;k<nodeNum;k++){
                	if(j == k) continue;
                    correlation[j][k] = corr(j, k);
                }
        }
    }
    
    //
    private void dWeight(){
    	for(int i=0;i<nodeNum;i++){
    		int count = 0;
    		double sum = 0;
    		for(int j=0;j<nodeNum;j++){
    			if(i == j)continue;
    			if(correlation[i][j]>cor_delta){
    				count++;
    				sum += correlation[i][j];
    			}
    		}
    		wCount[i] = count;
    		if(count != 0)
    			weight[i] = sum/count;
    		else weight[i] = 0;
    	}
    }
    
    private int chooseBestNode(){
    	boolean flag = false;
		int index1 = 0;
		int tempMax = 0;
		for(int j=0;j<nodeNum;j++){
			if(tempMax < wCount[j]){
				tempMax = wCount[j];
				index1 = j;
			}
			else if(tempMax == wCount[j])
				flag = true;
		}
		if(flag){
			for(int j=0;j<nodeNum;j++){
				if(wCount[j] == tempMax && weight[j] > weight[index1])
					index1 = j;
			}
		}
		return index1;
    }
    
    

	private void occupyTime() throws IOException {
	
		for(int i=0;i<DEVICE_NUM;i++){
			for(int j=0;j<nodeNum;j++){
				occupy_count[j] = 0;
			}
			if(devices[i].getCurrent_channel() == currentChannel){
				oneDeviceCorrs(i);
				dWeight();
				int bestNodeId = chooseBestNode();
	            occupy_percentage = (occupy_count[bestNodeId]*100.0)/(HOLE_LENGTH*1.0);
	            devices[i].setOccupy_time(occupy_percentage);
	            for(int j=0;j<nodeNum;j++){
	            	if(j == i) continue;
	            	if(correlation[j][bestNodeId] > INTEFER_THRESHHOLD){
	            		interferenceSet.get(j).add(i);
	            	}
	            }       
			}	
			devices[i].idle_time[currentChannel] = idle_time[i][currentChannel];
			//System.out.println(i+" "+currentChannel+" " + idle_time[i][currentChannel]*100.0/HOLE_LENGTH*1.0);
		}
		for(int i=0;i<DEVICE_NUM;i++){
			double tempInterfer = 0;
			for(int j=0;j<DEVICE_NUM;j++){
				if(interferenceSet.get(i).contains(j)&&devices[j].getCurrent_channel() == currentChannel){
					tempInterfer += devices[j].getOccupy_time();
				}
			}
			tempInterfer += (devices[i].idle_time[currentChannel]*100.0/HOLE_LENGTH*1.0);
			devices[i].interference_of_outer[currentChannel] = 100 - tempInterfer;
		}
	}
    /*
    public void printMap(){
    	@SuppressWarnings("rawtypes")
		Iterator iter = interferenceSet.iterator();
    	while(iter.hasNext()){
			@SuppressWarnings("unchecked")
			HashSet<Integer> entry = (HashSet<Integer>) iter.next();
			Iterator<Integer> it2 = entry.iterator();
			while(it2.hasNext()){
				int dth = it2.next();
				//System.out.println(dth + " " + devices[dth].getOccupy_time());
			}
    		//System.out.println(entry);
    	}
    }
    */
	
	
	
	
	public boolean isSmaller(){
		
		double[] a = new double[AP_NUM];
		double[] b = new double[AP_NUM];
		System.arraycopy(tempCellInterference, 0, a, 0, AP_NUM);
		System.arraycopy(minCellInterference, 0, b, 0, AP_NUM);
		
		for(int i = 0; i < AP_NUM-1; ++i){
            int k = i;
            for(int j = i; j < AP_NUM; ++j){
                if(a[k] > a[j]){
                    k = j;
                }
            }
            if(k != i){//
                double temp = a[i];
                a[i] = a[k];
                a[k] = temp;
            }
        }
		for(int i = 0; i < AP_NUM-1; ++i){
            int k = i;
            for(int j = i; j < AP_NUM; ++j){
                if(b[k] > b[j]){
                    k = j;
                }
            }
            if(k != i){//
                double temp = a[i];
                b[i] = b[k];
                b[k] = temp;
            }
        }
		
		for(int i=0;i<AP_NUM;i++){
			if(a[i]>b[i])
				return false;
		}
		return true;
	}
	
	public void interferCompute(){
		for(int k=0;k<AP_NUM;k++){
			double tempInterfer = 0.0;
			for(int i=0;i<DEVICE_NUM;i++){
				if(devices[i].getCell_number()!=k) continue;
				for(int j=0;j<DEVICE_NUM;j++){
					if((k != devices[j].getCell_number())&&(interferenceSet.get(i).contains(j))
							&&(tempAssignment[k]==tempAssignment[devices[j].getCell_number()])){
						tempInterfer += devices[j].getOccupy_time();
						System.out.print("* " + devices[j].getOccupy_time()+" ");
					}
				}
				System.out.println();
				tempInterfer += devices[i].interference_of_outer[tempAssignment[k]];
			}
			tempCellInterference[k] = tempInterfer;
			System.out.println(tempInterfer);
		}
		System.out.println("********************************************");
	}
	
	public void copyDoubleArray(double[] a, double[] b){
		for(int i=0;i<AP_NUM;i++){
			a[i] = b[i];
		}
	}
	public void copyIntArray(int[] a, int[] b){
		for(int i=0;i<AP_NUM;i++){
			a[i] = b[i];
		}
	}
	
    public void channelSelecter(){
    	for(int i=0;i<AP_NUM;i++){
    		for(int j=0;j<CHANNEL_NUM;j++){
    			tempAssignment[i] = j;
    			interferCompute();
    			if(isSmaller()){
    				copyDoubleArray(minCellInterference, tempCellInterference);	
    				bestChannel[i] = j;
    				copyIntArray(tempAssignment, bestChannel);
    			}
    		}
    	}
    }
    
    
	public void mainProgress(String rawdataFilePath, String radioMapFilePath) throws IOException{
		
		for(int i=0;i<CHANNEL_NUM;i++){
			initialize(i);
			readRawDataIn(rawdataFilePath);
			readMapIn(radioMapFilePath);
			occupyTime();
			//System.exit(0);
		}
		//printMap();
		channelSelecter();
		for(int i=0;i<AP_NUM;i++){
			System.out.print(bestChannel[i]+" ");
		}
		System.out.println();
	}

}

