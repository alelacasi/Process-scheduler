
public class Process {
	
	private int processNum;
	private double arrTime;
	private double executTime;
	private boolean started;
	
	//constructor
	Process(int p, double arrT, double execT) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
		this.started = false;
	}
	
	public void printProcess() {
		System.out.println("Process number #"+ processNum +" containt an arrival time of "
				+ processNum + "secs, and executes for "+ executTime+" secs");
	}
	
	
	public void setProcess(int p, double arrT, double execT) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
	}
	public void setProcess(int p, double arrT, double execT, boolean s) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
		this.started = s;
	}
	
	public boolean isStarted() {
		return started;
	}
	public int getProcessNumber() {
		return processNum;
	}
	
	public double getArrivalTime() {
		return arrTime;
	}
	
	public double getExecuteTime() {
		return executTime;
	}

}
