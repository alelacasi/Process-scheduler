import java.util.concurrent.Semaphore;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

/*
	Still need to implement Write to File
	Still need to implement proper display of output
	Still need to improve the array comparison
	Currently only done as RR based on coming in and does not do priority
*/
public class Main {
	static Semaphore AllowRobin=new Semaphore(1);
	static Semaphore AllowQ = new Semaphore(1);
	static LinkedList<Process> readyQueue = new LinkedList<>();	//Queue with whole thread info, make class? Arrive time, Run time, Wait time, Completion time
	static LinkedList<Process> waitQ = new LinkedList<>();
	// critical time of system 
	static double time = 0;

	
	public static void main(String[] args) {
		int length = 4;
		
		String s="";
		try {
			FileReader fr =  new FileReader ("Input.txt");
			BufferedReader br = new BufferedReader(fr);
			
			while((br.readLine()) !=null) {
				length++;
			}			
				br.close();
				
			}catch (IOException e) {
				out.println("File not found");
			}
		
		int[] arr_execut = new int[length];
		int[] arr_arrive = new int[length];
			
		try {
			FileReader fr =  new FileReader ("Input.txt");
			BufferedReader br = new BufferedReader(fr);
			String str;
			int count=0;
			out.println("Input values: ");
			while((str = br.readLine()) !=null) {
				String tokens[]=str.split("\\s+");
				/*for(int i=0;i<tokens.length;i++) {
					array[count]=Integer.parseInt(tokens[i]);
					count++;
				}*/
				arr_arrive[count]=Integer.parseInt(tokens[0]);
				arr_execut[count]=Integer.parseInt(tokens[1]);
				
				//s+=str+"\n";
				out.println(str);
				count++;
			}
			
			br.close();
			}catch (IOException e) {
				out.println("File not found");
			}
			
			arr_arrive[0] = 1;
			arr_execut[0] = 50;
			arr_arrive[1] = 2;
			arr_execut[1] = 3;
			arr_arrive[2] = 3;
			arr_execut[2] = 1;
			arr_arrive[3] = 45;
			arr_execut[3] = 20;
			
			//Check values of Arrive and Execute
			out.println("Array: ");
		
			for(int i=0; i<arr_arrive.length;i++) {
				out.println(arr_arrive[i] +" "+arr_execut[i]);
			}out.print("\n");
		
			//Make processes in the and add them to the queue
	    	Process [] p = new Process[length];
		    for(int i=0; i<length; i++) {
		    	p[i]=  new Process(i+1,arr_arrive[i],arr_execut[i]);
		    	waitQ.add(p[i]);
		    	
		    }out.println("\n");
		    
		    double smallest = waitQ.get(0).getArrivalTime();
		    for(int i = 0;i<waitQ.size();i++) {
		    	if(waitQ.get(i).getArrivalTime()<smallest) {
		    		smallest = waitQ.get(i).getArrivalTime();
		    	}
		    }
		    out.println("Time: "+ time);
		    while(time<smallest) {
		    	time++;
		    	out.println("Time: "+ time);
		    }
		    for(int i=0;i<waitQ.size();i++) {
		    	//waitQ.get(i).printProcess();
		    	if(waitQ.get(i).getArrivalTime()<=time) {
		    		readyQueue.add(waitQ.get(i));
		    		//printQ();
		    	}
		    	
		    }
		    
		    //Thread declaration for multiple threads
		   Thread [] t= new Thread [length];
		   for(int i=0; i<waitQ.size(); i++) {
		    	t[i] = new Thread(new MyThread(waitQ.get(i)));
		    }

		    
		    //Thread start for all the threads
		    for(int i=0; i<length; i++) {
		    	t[i].start();
		    }

		    
		  //Join all threads
		    try {
			    for(int i=0; i<length; i++) {
			    	t[i].join();
			    }

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    		   
	}
	
	public static class MyThread implements Runnable{
		
		int process;
		double arrivalTime;
		double runTime;
		double quantum = 0.5; //finish thread
		//Process t;
		//process number, arrival time, run time
		public MyThread(Process p) {

			this.process = p.getProcessNumber();
			this.runTime = p.getExecuteTime();
			this.arrivalTime = p.getArrivalTime();
			//this.t = new Process(p.getProcessNumber(),p.getArrivalTime(),p.getExecuteTime())
		}
		public void run() {
			
			while(runTime!=0) {
				
				//Check execution queue every time
				try {
					AllowQ.acquire();
					checkArrival();
				}catch (InterruptedException e) {e.printStackTrace();}finally{
					AllowQ.release();
				}
				
				if(!readyQueue.isEmpty()) {	
				//should this field be inside the semaphore block?
				if(readyQueue.getFirst().getProcessNumber()==process) {
					try {				
						//Round Robin logic
						AllowRobin.acquire();
						
						double q = runTime*0.1;
						// check that if there come before qtime 
						if (arrivalTime <= time) {
							//Starvation avoidance, do RR until
						  if (q<0.6) {  //runTime > 0 OR q<quantum
							  //Let process finish
						      //flag = false; 
						      if (runTime > 0.1) { 
						
						          // make decrease the b time 
						          time = time + q; 
						          runTime = runTime - q; 
						          //arrTime = arrTime + q; 
						          //seq += "->" + p[i]; 
						      } 
						      else { 
						
						          // for last time 
						          time = time + runTime; 
						
						          // store comp time 
						          //comp[i] = time - a[i]; 
						
						          // store wait time 
						          //wait[i] = time - b[i] - a[i]; 
						          runTime = 0; 
						
						          // add sequence 
						          //seq += "->" + p[i]; 
						      }
						      
						      readyQueue.getFirst().setProcess(process, arrivalTime, runTime);
						      
						  }else {
							  //do priority on Size
							 
							  //if(readyQueue.getFirst().getProcessNumber()==process) {
							      if (runTime > 0.1) { 
							    	  //readyQueue.getFirst().printProcess();
							          // make decrease the b time 
							          time = time + q; 
							          runTime = runTime - q; 
							          //arrTime = arrTime + q; 
							          //seq += "->" + p[i]; 
							          //readyQueue.getFirst().printProcess();
							      } 
							      else { 
							
							          // for last time 
							          time = time + runTime; 
							
							          // store comp time 
							          //comp[i] = time - a[i]; 
							
							          // store wait time 
							          //wait[i] = time - b[i] - a[i]; 
							          runTime = 0; 
							
							          // add sequence 
							          //seq += "->" + p[i]; 
							      }
							      //readyQueue.getFirst().printProcess();
							      readyQueue.getFirst().setProcess(process, arrivalTime, runTime);
							      readyQueue.getFirst().printProcess();
							      readyQueue.getFirst().printProcess();
								
								out.println("Time "+(int)time+ ", Process "+process+ 
										" , Execution time "+ /*rounded3+"-"+ rounded2+" = "+*/ q);
							  
							  checkQ();
						  } 
						// if no process arrived
						}else if (arrivalTime > time) { 
						time++; 
						}
						
						if(runTime == 0) {
							out.println("Time "+(int)time+ ", Process "+readyQueue.getFirst().getProcessNumber()+" is Done.");
							printQ();
							readyQueue.removeFirst();
							printQ();
							AllowRobin.release();
							//Thread.sleep(1000000000);
						}else if(runTime>0.1) {
							
							//printQ();
							//checkArrival();

							
							BigDecimal bd1 = new BigDecimal(readyQueue.getFirst().getExecuteTime());
							bd1 = bd1.round(new MathContext(3));
							double rounded1 = bd1.doubleValue();
							BigDecimal bd2 = new BigDecimal(q);
							bd2 = bd2.round(new MathContext(3));
							double rounded2 = bd2.doubleValue();
							
							BigDecimal bd3 = new BigDecimal(q+ readyQueue.getFirst().getExecuteTime());
							bd3 = bd3.round(new MathContext(3));
							double rounded3 = bd3.doubleValue();
							
							
							out.println("Time "+(int)time+ ", Process "+readyQueue.getFirst().getProcessNumber()+ 
									" , Execution time "+ /*rounded3+"-"+ rounded2+" = "+*/ rounded1);
						}

						
						
					}catch (InterruptedException e) {e.printStackTrace();}finally {
						// calling release() after a successful acquire()
						AllowRobin.release();
					}
				}
				//if()
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {e.printStackTrace();}
				
			}else{
				time++;
			}
			}
			
			if(readyQueue.isEmpty()) {
				return;
			}
		}
	}
	
	//Checks if the process has arrived and creates a thread for it.
	public static void checkArrival() {
		//printQ();
		Thread  t = new Thread();
		for(int i=0;i<waitQ.size();i++) {
			if(time>=waitQ.get(i).getArrivalTime()) {
				
				//if the value is empty don't add
				if(waitQ.get(i).getExecuteTime()==0) {
					//out.println("BREAK");
					//break;
				}
				//If priority does not contain value, add to queue
				else if(!readyQueue.contains(waitQ.get(i))) {
					readyQueue.addFirst(waitQ.get(i));
					//printQ();
					
					t = new Thread(new MyThread(readyQueue.getFirst()));
				}else {
					//out.println("ELSE " +waitQ.get(i).getProcessNumber());
				}
			}
					//out.println("NOT ELSE " +time+" "+ waitQ.get(i).getArrivalTime());
		}
	}
	
	//Algorithm that puts the values in order, currently inefficient Needs improvement
	public static void checkQ() {
		printQ();
		//out.println("Check Q");
		for(int i=1;i<readyQueue.size();i++) {
			//If time for 1>2, switch
			if(readyQueue.get(i-1).getExecuteTime()>readyQueue.get(i).getExecuteTime()) {
				Process holder = readyQueue.get(i);
				readyQueue.remove(i);
				printQ();
				
				for(int j=0;j<readyQueue.size();j++) {	
					if(holder.getExecuteTime()<readyQueue.get(j).getExecuteTime()) {
						readyQueue.add(j, holder);
						printQ();
						break;
					}
				}
			}
		}
	}
	
	public static void printQ() {
		//out.println("Ready Queue: ");
	    for(int i=0; i<readyQueue.size(); i++) {
	    	//priorityQ.get(i).printProcess();
	    }
		for (int i=0;i<readyQueue.size();i++) {
			out.print(readyQueue.get(i).getProcessNumber());
			if(i+1!=readyQueue.size()) {
				out.print(" -> ");
			}
		}out.println();
	} 

	public static void printW() {
		//out.println("Wait Queue: ");
	    for(int i=0; i<waitQ.size(); i++) {
	    	//priorityQ.get(i).printProcess();
	    }
		for (int i=0;i<waitQ.size();i++) {
			out.print(waitQ.get(i).getProcessNumber());
			if(i+1!=waitQ.size()) {
				out.print(" -> ");
			}
		}out.println();
	}
}

/*
public class Process {
	
	private int processNum;
	private double arrTime;
	private double executTime;
	
	//constructor
	Process(int p, double arrT, double execT) {
		this.processNum = p;
		this.arrTime = arrT;
		this.executTime = execT;
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
	
	public int getProcessNumber() {
		return processNum;
	}
	
	public double getArrivalTime() {
		return arrTime;
	}
	
	public double getExecuteTime() {
		return executTime;
	}
}*/
