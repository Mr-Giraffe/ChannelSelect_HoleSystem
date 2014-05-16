public class Device {
	private int device_number;
	private double occupy_time;
	private String mac_address;
	private int current_channel;
	private int cell_number;
	public double[] interference_of_outer = new double[3];
	public int[] idle_time = new int[3];
	public Device(){
		device_number = 0;
		occupy_time = 0;
		mac_address = "";
		current_channel = 0;
		cell_number = 0;
		for(int i=0;i<3;i++)
			interference_of_outer[i] = 0;
	}
	
	public int getDevice_number() {
		return device_number;
	}
	public void setDevice_number(int device_number) {
		this.device_number = device_number;
	}
	public double getOccupy_time() {
		return occupy_time;
	}
	public void setOccupy_time(double occupy_time) {
		this.occupy_time = occupy_time;
	}
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public int getCurrent_channel() {
		return current_channel;
	}

	public void setCurrent_channel(int current_channel) {
		this.current_channel = current_channel;
	}

	public int getCell_number() {
		return cell_number;
	}

	public void setCell_number(int cell_number) {
		this.cell_number = cell_number;
	}
	
	
	

}
