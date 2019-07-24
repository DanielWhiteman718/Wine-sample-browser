package assignment2019;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import assignment2019.codeprovided.*;

/**
* WineSampleCellar.java
* 
* Implements the AbstractWineSampleCellar.java
* 
* @author Daniel Whiteman
*
*/
public class WineSampleCellar extends AbstractWineSampleCellar {
	
	private String whiteFile;
	private String redFile;
	private String queryFile;
	/**
	 * Constructor
	 * 
	 * Instantiates a WineSampleCellar object
	 * 
	 * @param red file name containing red wine samples
	 * @param white file name containing white wine samples
	 * @param queries file name containing queries
	 * 
	 * @author Daniel Whiteman
	 * */
	public WineSampleCellar(String red, String white, String queries) {
		//calls the AbstractWineSampleCellar constructor
		super(red, white, queries);
		this.whiteFile = white;
		this.redFile = red;
		this.queryFile = queries;
	}
	
	/**
	 * readQueries
	 * 
	 * Returns a list of querys from a list of strings
	 * 
	 * @param queryList lists of strings where each string is a word in the queries file
	 * @return querys List of of objects with type Query, generated from the queryList
	 * 
	 * @author Daniel Whiteman
	 * */
	public List<Query> readQueries(List<String> queryList){
		//Instantiates the variables type, conditions, querys, list
		WineType type = WineType.ALL;
		List<QueryCondition> conditions;	
		List<Query> querys = new ArrayList<>(); 
		List<WineSample> list;
		
		//Take each string in the queryList and examine what to do with it
		int i;
		for(i = 0; i < queryList.size(); i++) {
			
			//if the string is "select" then we need to find the next instance of "where"
			//Then pass the sub array of the strings in between "select" and "where" into calculateWineType
			//Set the result to variable type
			if(queryList.get(i).equals("select")){
				int a = i + 1;
				while(!(queryList.get(a).equals("where"))) {
					a++;
				}
				type = calculateWineType(queryList.subList(i+1, a));
			}
			
			//if the string is "where" then we need to find the next instance of "select"
			//Then pass the sub array of the strings in between "where" and "select" into calculateConditions
			//Set the result to variable type
			else if(queryList.get(i).equals("where")) {
				int a = i + 1;
				boolean reachedEnd = false;
				while(!(queryList.get(a).equals("select"))) {
					a++;
					if(a == queryList.size()-1) {
						reachedEnd = true;
						break;
					}
				}
				List<String> listCondi = queryList.subList(i+1, a);
				if (reachedEnd) {
					listCondi.add(queryList.get(a));
				}
				conditions = calculateQueryConditions(listCondi);
				
				//Getting list of wine samples based on winetype
				if (type == WineType.WHITE){
					list = readWineFile(whiteFile, type);
				} else if(type == WineType.RED){
					list = readWineFile(whiteFile, type);
				} else {
					list = new ArrayList<>();
					list.addAll(readWineFile(whiteFile, type));
					list.addAll(readWineFile(redFile, type));
				}
				
				//creating a query object and adding it to querys
				querys.add(new Query(list, conditions, type));
			}
		}
		return querys;
	}
	
	
	/**
	 * calculateWineType
	 * 
	 * Returns a WineType from a list of strings
	 * 
	 * @param redOrWhiteAll list of strings that specify the wine type
	 * @return wt WineType calculated from the given strings in redOrWhiteAll
	 * 
	 * @author Daniel Whiteman
	 * */
	public WineType calculateWineType(List<String> redOrWhiteAll) {
		//If the size of the list is 1(1 word) the wine type can only be red or white
		if(redOrWhiteAll.size() == 1) {
			if ((redOrWhiteAll.get(0).equals("red")) || (redOrWhiteAll.get(0).equals("RED"))) {
				WineType wt = WineType.RED;
				return wt;
			} else {
				WineType wt = WineType.WHITE;
				return wt;
			}
			
		//Must have "Or" in the set of strings
		} else {
			WineType wt = WineType.ALL;
			return wt;
		}	
	}
	
	
	/**
	 * calculateQueryConditions
	 * 
	 * Returns a list of queryCondition objects from a list of strings
	 * that specify the query conditions
	 * 
	 * @param conditions List of strings that specify the query conditions
	 * @return qConditions List of queryCondition objects generated from conditions
	 * 
	 * @author Daniel Whiteman
	 * */
	public List<QueryCondition> calculateQueryConditions(List<String> conditions){
		//Instantiates the variables qConditions, property, operator, value
		List<QueryCondition> qConditions = new ArrayList<>();
		WineProperty property = WineProperty.Alcohol;
		String operator = "";
		double value = 0;
		
		//Take each string in conditions and examine what to do with it
		for(int i = 0; i < conditions.size(); i++) {
			
			//If it is the last item in list then it must be a number or an operator with a number
			if(i == conditions.size() - 1) {
				try{
					value = Double.parseDouble(conditions.get(i));
					QueryCondition condition = new QueryCondition(property, operator, value);
					qConditions.add(condition);}
				
				catch (Exception e){
					value = Double.parseDouble(conditions.get(i).replaceAll("[^0-9]",""));
					int index = conditions.get(i).indexOf(conditions.get(i).replaceAll("[^0-9]",""));
					operator = conditions.get(i).substring(0, index);
					
					qConditions.add(new QueryCondition(property, operator, value));
					return qConditions;
				}
				break;
			}
			
			//If the string is "and" then we must have reached the end of a condition
			//So we create a QueryCondition object and add it to qConditions
			if(conditions.get(i).equals("and")) {
				QueryCondition condition = new QueryCondition(property, operator, value);
				qConditions.add(condition);
			}
			
			//Checks if the string is a WineProperty
			else if(WineProperty.fromFileIdentifier(conditions.get(i)) != null) {
				property = WineProperty.fromFileIdentifier(conditions.get(i));
			}
			
			//Checks if the string contains an operator
			else if((conditions.get(i).contains("<")) || (conditions.get(i).contains(">")) || (conditions.get(i).contains("="))){
				
				//Checks to see if the string contains numbers
				boolean numbers = false;
				
				if(!(conditions.get(i).replaceAll("[^0-9]","").equals(""))) {
					value = Double.parseDouble(conditions.get(i).replaceAll("[^0-9]",""));
					int index = conditions.get(i).indexOf(conditions.get(i).replaceAll("[^0-9]",""));
					operator = conditions.get(i).substring(0, index);
				
				} else {
					operator = conditions.get(i);
				}
				
			} else {
				//If not any of the above it must be a number so value is set
				value = Double.parseDouble(conditions.get(i));
			}
			
		}
		return qConditions;
	}
	
	
	
	
	 /**
     * updateCellar method - updates wineSampleRacks to contain 'also' an additional list 
     * containing ALL wine samples (in this case red and white)
     * 
     * @author Daniel Whiteman
     */
	public void updateCellar() {
		//Adds all wine samples in red and white wine text files to a list
		List<WineSample> list = new ArrayList<>();
		list.addAll(getWineSampleList(WineType.WHITE));
		list.addAll(getWineSampleList(WineType.RED));
		//Add this to wineSampleRacks
		wineSampleRacks.put(WineType.ALL, list);
	}
	
	/**
     * displayQueryResults method - displays in console the results of a query 
     * in a meaningful format to the user
     *
     * @param query The Query object to be printed
     * 
     * @author Daniel Whiteman
     */
	public void displayQueryResults(Query query) {
		
		//Solves the query and displays each wine sample
		List<WineSample> results = query.solveQuery();
		for(WineSample result : results) {
			System.out.println("[SAMPLE] id: "+result.getId()+" Type: "+result.getType()+
							   " Fixed Acidity: "+result.getFixedAcidity()+" Volatile Acidity: "+
							   result.getVolatileAcidity()+" Citric Acid: "+result.getCitricAcid()+
							   " Residual Sugar: "+result.getResidualSugar()+" Chlorides: "
							   +result.getChlorides()+" Free Sulphur Dioxide: "+result.getFreeSulfurDioxide()+
							   " Total Sulphur Dioxide: "+result.getTotalSulfurDioxide()+
							   " Density: "+result.getDensity()+" PH: "+result.getpH()+
							   " Sulphates: "+result.getSulphates()+" Alcohol: "+result.getAlcohol()+
							   " Quality: "+result.getQuality());
		}
	}
	
	
	/**
     * bestQualityWine method - receives the wine type
     * Returns a list of objects which have been assigned the highest quality score in the list.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return collection of WineSample objects with the highest quality
     * 
     * @author Daniel Whiteman
     */
	public List<WineSample> bestQualityWine(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Sorting the list of wine samples in order of quality
		//uses custom comparator class
		Collections.sort(list, new Comparator<WineSample>() {
			public int compare(WineSample ws1, WineSample ws2) {
				
				if(ws1.getQuality() > ws2.getQuality()) {
					return 1;
				}
				else if(ws1.getQuality() < ws2.getQuality()) {
					return -1;
				}
				return 0;
			}
		});
		
		//Returns the top three
		List<WineSample> best = new ArrayList<>();
		best.add(list.get(list.size()-1));
		best.add(list.get(list.size()-2));
		best.add(list.get(list.size()-3));
		return best;
	}
	
	
	/**
     * worstQualityWine method - receives the wine type 
     * Returns a list of objects which have been assigned the lowest quality score in the list.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return collection of WineSample objects with the lowest quality
     * 
     * @author Daniel Whiteman
     */
	public List<WineSample> worstQualityWine(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Sorting the list of wine samples in order of quality
		//uses custom comparator class
		Collections.sort(list, new Comparator<WineSample>() {
			public int compare(WineSample ws1, WineSample ws2) {
				
				if(ws1.getQuality() > ws2.getQuality()) {
					return 1;
				}
				else if(ws1.getQuality() < ws2.getQuality()) {
					return -1;
				}
				return 0;
			}
		});
		
		//Returns lowest three
		List<WineSample> best = new ArrayList<>();
		best.add(list.get(0));
		best.add(list.get(1));
		best.add(list.get(2));
		return best;
	}
	
	
	
	 /**
     * highestPH method - receives the wine type 
     * Returns a list of objects which have the highest pH in the list.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return collection of WineSample objects with the highest pH
     * 
     * @author Daniel Whiteman
     */
	public List<WineSample> highestPH(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Sorting the list of wine samples in order of PH
		//uses custom comparator class
		Collections.sort(list, new Comparator<WineSample>() {
			public int compare(WineSample ws1, WineSample ws2) {
				
				if(ws1.getpH() > ws2.getpH()) {
					return 1;
				}
				else if(ws1.getpH() < ws2.getpH()) {
					return -1;
				}
				return 0;
			}
		});
		
		//Returns the top three
		List<WineSample> best = new ArrayList<>();
		best.add(list.get(list.size()-1));
		best.add(list.get(list.size()-2));
		best.add(list.get(list.size()-3));
		return best;
	}
	
	
	
	/**
     * lowestPH method - receives the wine type 
     * Returns a list of objects which have the lowest pH in the list.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return collection of WineSample objects with the lowest pH
     * 
     * @author Daniel Whiteman
     */
	public List<WineSample> lowestPH(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Sorting the list of wine samples in order of PH
		//uses custom comparator class
		Collections.sort(list, new Comparator<WineSample>() {
			public int compare(WineSample ws1, WineSample ws2) {
				
				if(ws1.getpH() > ws2.getpH()) {
					return 1;
				}
				else if(ws1.getpH() < ws2.getpH()) {
					return -1;
				}
				return 0;
			}
		});
		
		//Returns lowest three
		List<WineSample> best = new ArrayList<>();
		best.add(list.get(0));
		best.add(list.get(1));
		best.add(list.get(2));
		return best;
	}
	
	
	/**
     * highestAlcoholContent method - receives the wine type
     * Returns the highest alcohol value in the list for the specified wineType.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return  a double value with the highest alcohol content
     * 
     * @author Daniel Whiteman
     */
	public double highestAlcoholContent(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Uses bestProp method
		double highest = bestProp(list, WineProperty.Alcohol);
		return highest;
	}

	
	
	/**
     * averageAlcoholContent method - receives the wine type
     * Returns the count variable divided by the number of objects in the List.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return average alcohol content of the list as a double
     * 
     * @author Daniel Whiteman
     */
	public double averageAlcoholContent(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Uses averageProp method
		double sum = averageProp(list, WineProperty.Alcohol);
		return sum;
	}
	
	
	
	/**
     * lowestCitricAcid method - receives the wine type
     * Returns the lowest citric acid value in the list for the specified wineType.
     *
     * @param wineType Either RED, WHITE or ALL
     * @return a double value with the lowest citric acid content
     * 
     * @author Daniel Whiteman
     */
	public double lowestCitricAcid(WineType wineType){
		//Gets the list of winesamples of type wineType
		List<WineSample> list = getWineSampleList(wineType);
		
		//Uses worstProp method
		double lowest = worstProp(list, WineProperty.CitricAcid);
		return lowest;
	}
	
	
	/**
     * bestProp - Gets the highest value for a given property in a given
     * list of wine samples
     *
     * @param samples List of wine samples
     * @return a double value of average value
     * 
     * @author Daniel Whiteman
     */
	public double bestProp(List<WineSample> samples, WineProperty prop) {
		double best = 0;
		/*For each sample, if the property value
		 * is greater than the current best, set best to
		 * the property value*/
		
		for(WineSample sample : samples) {
			if (sample.getProperty(prop) > best) {
				best = sample.getProperty(prop);
			}
		}
		return best;
	}
	
	/**
     * worstProp - Gets the lowest value for a given property in a given
     * list of wine samples
     *
     * @param samples List of wine samples
     * @return a double value of average value
     * 
     * @author Daniel Whiteman
     */
	public double worstProp(List<WineSample> samples, WineProperty prop) {
		double worst = 0;
		boolean first = true;
		
		/*For each sample, if the property value
		 * is less than the current worst, set worst to
		 * the property value*/
		for(WineSample sample : samples) {
			if (first) {
				worst = sample.getProperty(prop);
				first = false;
			}else if (sample.getProperty(prop) < worst) {
				worst = sample.getProperty(prop);
			}
		}
		return worst;
	}
	
	
	/**
     * averageProp - Gets the average value for a given property in a given
     * list of wine samples
     *
     * @param samples List of wine samples
     * @return a double value of average value
     * 
     * @author Daniel Whiteman
     */
	public double averageProp(List<WineSample> samples, WineProperty prop) {
		double sum = 0;
		/*For each sample, add the property value to the sum then divide the sum by
		 * the list size*/
		for(WineSample sample : samples){
			sum = sum + sample.getProperty(prop);
		}
		sum = (sum/samples.size());
		return sum;
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
