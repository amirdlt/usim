package com.usim.ulib.notmine.alg;

//import umontreal.ssj.rng.RandomStream;
import umontreal.iro.lecuyer.rng.RandomStream;
/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Test
{
    /* INSTANCE FIELDS AND CONSTRUCTOR */
    private String instanceName;
    private float maxTime; // Maximum computing time allowed
    private float beta1; // First parameter associated with the distribution
    private int seed; // Seed value for the Random Number Generator (RNG)
    private RandomStream rng;
    private int shortSim; //short simulations
    private int longSim; //long simulations
    private float variance;
    private boolean modeExe;




	public Test(String name,  float t,  float p1, int s, int sm, int ls,float var, boolean exe)
    {
        instanceName = name;
        maxTime = t;
        beta1 = p1;
        seed = s;
        shortSim = sm; 
        longSim = ls;
        variance = var;
        modeExe = exe;
    }

	
	
    /* GET METHODS */
    public String getInstanceName(){return instanceName;}
    public float getMaxTime(){return maxTime;}
    public float getBeta1(){return beta1;}
    public int getSeed(){return seed;}
    public RandomStream getRandomStream() {return rng;}
    public int getLongSim() {return longSim;}
	public int getShortSim() {return shortSim;}
	public float getVariance() {return variance;}
	public boolean isModeExe() {return modeExe;}
    
	
	
    /* SET METHODS */
    public void setBeta1(float beta){this.beta1 = beta;}
	public void setRandomStream(RandomStream stream) {rng = stream;}
	public void setLongSim(int longSim) {this.longSim = longSim;}
	public void setShortSim(int shortSim) {this.shortSim = shortSim;}
	public void setVariance(float variance) {this.variance = variance;}
	public void setMaxTime(float maxTime) {this.maxTime = maxTime;}
    



}
