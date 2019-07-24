package assignment2019;
import assignment2019.codeprovided.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
* WineSampleBrowserPanel.java
* 
* Implements the AbstractWineSampleBrowserPanel.java
* Handles the GUI and event-handling side of the project
* 
* @author Daniel Whiteman
*
*/
public class WineSampleBrowserPanel extends AbstractWineSampleBrowserPanel {
	//Instance field
	private WineSampleCellar cellar;
	
	/**
	 * Constructor
	 * 
	 * Instantiates a WineSampleBrowserPanel object
	 * 
	 * @param cellar WineSampleCellar object
	 * 
	 * @author Daniel Whiteman
	 * */
	public WineSampleBrowserPanel(WineSampleCellar cellar) {
		//calls the AbstractWineSampleBrowserPanel constructor
		super(cellar);
		this.cellar = (WineSampleCellar) cellar;
	}
	
	/**
	 * Private ItemHandler class
	 * 
	 * Deals with changes in the wine type combo box
	 * Updates wineType and updates GUI accordingly
	 * 
	 * @author Daniel Whiteman
	 */
	private class ItemHandler implements ItemListener{
		public void itemStateChanged(ItemEvent event) {
			//Checks which GUI component the event came from
			if(event.getSource() == comboWineTypes) {
				
				//updates the wineType and filteredWineSampleList accordingly
				if(comboWineTypes.getSelectedItem() == "RED") {
					wineType = WineType.RED;
					filteredWineSampleList = cellar.getWineSampleList(WineType.RED);
				} 
				else if(comboWineTypes.getSelectedItem() == "WHITE") {
					wineType = WineType.WHITE;
					filteredWineSampleList = cellar.getWineSampleList(WineType.WHITE);
				} else {
					wineType = WineType.ALL;
					filteredWineSampleList = cellar.getWineSampleList(WineType.ALL);
				}
				
				//Updates the GUI
				if (queryConditionList.size() > 0) {
					executeQuery();
				}
				updateWineList();
				updateStatistics();
				
			}
		}
	}
	
	/**
	 * Private ButtonHandler class
	 * 
	 * Deals with button presses on addFilter and clearFilters buttons
	 * 
	 * @author Daniel Whiteman
	 */
	private class ButtonHandler implements ActionListener{
		//Checks which GUI component the event came from
		public void actionPerformed(ActionEvent event) {
			
			//addFilter is called if the add filter button is pressed
			if(event.getSource() == buttonAddFilter){
				addFilter();
			}
			
			//clearFilters is called if the clear filters button is pressed
			if(event.getSource() == buttonClearFilters) {
				clearFilters();
			}
		}
	}
	
	/**
	 * addListeners
	 * 
	 * Adds event listeners to GUI components
	 * Attaches the private classes above as listeners
	 * 
	 * @author Daniel Whiteman
	 * */
	public void addListeners(){
		ItemHandler iHandler = new ItemHandler();
		comboWineTypes.addItemListener(iHandler);
		ButtonHandler bHandler = new ButtonHandler();
		buttonAddFilter.addActionListener(bHandler);
		buttonClearFilters.addActionListener(bHandler);
	}
	
	
	/**
	 * addFilter
	 * 
	 * Adds a query condition to the query conditions list
	 * Then updates the GUI
	 * 
	 * @author Daniel Whiteman
	 * */
	public void addFilter() {
		//Gets a string(property) from combo box
		String prop = (String)comboProperties.getSelectedItem();
		
		//Switch statement to see wineProperty the string corresponds to
		//then sets propf to a wineProperty
		WineProperty propf;
		switch(prop) {
			case "Fixed Acidity":
				propf = WineProperty.FixedAcidity;
				break;
			case "Volatile Acidity":
				propf = WineProperty.VolatileAcidity;
				break;
			case "Citric Acidity":
				propf = WineProperty.CitricAcid;
				break;
			case "Residual Sugar":
				propf = WineProperty.ResidualSugar;
				break;
			case "Chlorides":
				propf = WineProperty.Chlorides;
				break;
			case "Free Sulfur Dioxide":
				propf = WineProperty.FreeSulfurDioxide;
				break;
			case "Total Sulfur Dioxide":
				propf = WineProperty.TotalSulfurDioxide;
				break;
			case "Density":
				propf = WineProperty.Density;
				break;
			case "pH":
				propf = WineProperty.PH;
				break;
			case "Sulphates":
				propf = WineProperty.Sulphates;
				break;
			case "Alcohol":
				propf = WineProperty.Alcohol;
				break;
			case "Quality":
				propf = WineProperty.Quality;
				break;
			default:
				propf = WineProperty.Quality;		
		}
		
		//Getting the operator from combo box
		String oper = (String)comboOperators.getSelectedItem();
		
		//Gets the number value as a string from the text box
		//Then stores it as a double
		try {
			String val = value.getText();
			double valf = Double.valueOf(val);
			//Creates a queryCondition and adds it to the queryConditionList
			queryConditionList.add(new QueryCondition(propf,oper,valf));
			
			//Executes query and updates the GUI
			executeQuery();
			updateWineList();
			updateStatistics();
			updateFilterBox();
		} 
		catch (Exception e){
			JOptionPane.showMessageDialog(this, "Value needs to be a number.");
		}
		
		
	}
	
	/**
	 * clearFilters
	 * 
	 * Clears all query conditions from queryConditionList
	 * Then updates the GUI
	 * 
	 * @author Daniel Whiteman
	 * */
	public void clearFilters() {
		//Clears the list
		queryConditionList.clear();
		//Resets the filteredWineSampleList to wine sample of type wineType
		filteredWineSampleList = cellar.getWineSampleList(wineType);
		//updates the GUI
		updateWineList();
		updateStatistics();
		updateFilterBox();
	}
	
	
	/**
	 * updateStatistics
	 * 
	 * Updates the statistics in the GUI according to the wine sample currently being displayed
	 * 
	 * @author Daniel Whiteman
	 * */
	public void updateStatistics() {
		//Clears all text currently in the text box then sets the font
		statisticsTextArea.setText("");
		statisticsTextArea.setFont(new Font("Consolas", Font.BOLD, 12));
		
		//Quality
		statisticsTextArea.append("Best quality: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.Quality))+"\n");
		statisticsTextArea.append("Worst quality: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.Quality))+"\n");
		statisticsTextArea.append("Average quality: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.Quality))+"\n");
		statisticsTextArea.append("\n");


		//PH
		statisticsTextArea.append("Highest PH: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.PH))+"\n");
		statisticsTextArea.append("Lowest PH: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.PH))+"\n");
		statisticsTextArea.append("Average PH: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.PH))+"\n");
		statisticsTextArea.append("\n");
		
		//Alcohol
		statisticsTextArea.append("Highest alcohol content: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.Alcohol))+"\n");
		statisticsTextArea.append("Lowest alcohol: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.Alcohol))+"\n");
		statisticsTextArea.append("Average alcohol content: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.Alcohol))+"\n");
		statisticsTextArea.append("\n");
		
		//fAcid
		statisticsTextArea.append("Highest fixed acidity: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.FixedAcidity))+"\n");
		statisticsTextArea.append("Lowest fixed acidity: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.FixedAcidity))+"\n");
		statisticsTextArea.append("Average fixed acidity: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.FixedAcidity))+"\n");
		statisticsTextArea.append("\n");
		
		//VAcid
		statisticsTextArea.append("Highest volatile acidity: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.VolatileAcidity))+"\n");
		statisticsTextArea.append("Lowest volatile acidity: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.VolatileAcidity))+"\n");
		statisticsTextArea.append("Average volatile acidity: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.VolatileAcidity))+"\n");
		statisticsTextArea.append("\n");
		
		//CiAcid
		statisticsTextArea.append("Highest citric acidity: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.CitricAcid))+"\n");
		statisticsTextArea.append("Lowest citric acidity: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.CitricAcid))+"\n");
		statisticsTextArea.append("Average citric acidity: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.CitricAcid))+"\n");
		statisticsTextArea.append("\n");
		
		//sugar
		statisticsTextArea.append("Highest residual sugar: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.ResidualSugar))+"\n");
		statisticsTextArea.append("Lowest residual sugar: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.ResidualSugar))+"\n");
		statisticsTextArea.append("Average residual sugar: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.ResidualSugar))+"\n");
		statisticsTextArea.append("\n");
		
		//sugar
		statisticsTextArea.append("Highest Chlorides: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.Chlorides))+"\n");
		statisticsTextArea.append("Lowest Chlorides: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.Chlorides))+"\n");
		statisticsTextArea.append("Average Chlorides: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.Chlorides))+"\n");
		statisticsTextArea.append("\n");
		
		//FreeSulphurDio
		statisticsTextArea.append("Highest free sulpur dioxide: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.FreeSulfurDioxide))+"\n");
		statisticsTextArea.append("Lowest free sulpur dioxide: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.FreeSulfurDioxide))+"\n");
		statisticsTextArea.append("Average free sulpur dioxide: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.FreeSulfurDioxide))+"\n");
		statisticsTextArea.append("\n");
		
		//TotalSulphurDio
		statisticsTextArea.append("Highest total sulpur dioxide: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.TotalSulfurDioxide))+"\n");
		statisticsTextArea.append("Lowest total sulpur dioxide: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.TotalSulfurDioxide))+"\n");
		statisticsTextArea.append("Average total sulpur dioxide: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.TotalSulfurDioxide))+"\n");
		statisticsTextArea.append("\n");
		
		//Density
		statisticsTextArea.append("Highest density: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.Density))+"\n");
		statisticsTextArea.append("Lowest density: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.Density))+"\n");
		statisticsTextArea.append("Average density: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.Density))+"\n");
		statisticsTextArea.append("\n");
		
		//Sulphates
		statisticsTextArea.append("Highest sulphates: "+
				String.format("%.2f",cellar.bestProp(filteredWineSampleList, WineProperty.Sulphates))+"\n");
		statisticsTextArea.append("Lowest sulphates: "+
				String.format("%.2f",cellar.worstProp(filteredWineSampleList, WineProperty.Sulphates))+"\n");
		statisticsTextArea.append("Average sulphates: "+
				String.format("%.2f",cellar.averageProp(filteredWineSampleList, WineProperty.Sulphates))+"\n");
		statisticsTextArea.append("\n");
	}
	
	/**
	 * updateWineList
	 * 
	 * Updates the wine samples currently being displayed based on filters
	 * 
	 * @author Daniel Whiteman
	 * */
	public void updateWineList() {
		//Clears all text currently in the text box then sets the font
		filteredWineSamplesTextArea.setText("");
		filteredWineSamplesTextArea.setFont(new Font("Consolas", Font.BOLD, 12));
		
		//gets contents of filteredWineSampleList
		List<WineSample> results = this.getFilteredWineSampleList();
		
		//Table headings
		filteredWineSamplesTextArea.append("ID          Type        Fixed Acidity       Volatile Acidity"
				+ "       Residual Sugar       Chlorides        Free Sulpher Dioxide       Total Sulphur Dioxide"
				+ "       Density          PH          Sulphates        Alcohol       Quality" + "\n");
		filteredWineSamplesTextArea.append("----------------------------------------------------------------------"
				+ "-----------------------------------------------------------------------------------------------"
				+ "--------------------------------------------------------------" + "\n");
		
		//Outputs each formatted wine sample 
		for(WineSample result : results) {
			
			String message = ""+String.format("%05d",(int)result.getId())+"       "+String.format("%5s",result.getType())+
							   "       "+String.format("%09f",result.getFixedAcidity()).subSequence(0, 5)+"               "+
							   String.format("%010f",result.getVolatileAcidity()).subSequence(2, 7)+"                  "+
							   String.format("%010f",result.getResidualSugar()).subSequence(1, 6)+"                "
							   +String.format("%010f",result.getChlorides()).subSequence(2, 7)+"            "+
							   String.format("%010f",result.getFreeSulfurDioxide()).subSequence(0, 6)+"                     "+
							   String.format("%010f",result.getTotalSulfurDioxide()).subSequence(0, 6)+"                      "+
							   String.format("%010f",result.getDensity()).subSequence(2, 9)+"          "+
							   String.format("%010f",result.getpH()).subSequence(2, 7)+"       "+
							   String.format("%010f",result.getSulphates()).subSequence(2, 6)+"             "+
							   String.format("%010f",result.getAlcohol()).subSequence(1, 6)+"           "+result.getQuality();
			filteredWineSamplesTextArea.append(message + "\n");
		}
		
	}
	//.subSequence(2, 7)
	/**
	 * updateFilterBox
	 * 
	 * Updates the filter text box with the current query conditions in the queryConditioList
	 * 
	 * @author Daniel Whiteman
	 * */
	public void updateFilterBox() {
		//clears the text area
		queryConditionsTextArea.setText("");
		//adds each query condition
		for(QueryCondition condition : queryConditionList) {
			queryConditionsTextArea.append(condition + ",");
		}
	}
	
	/**
	 * executeQuery
	 * 
	 * Updates the filteredWineSampleList to contain wine samples that fit the current query conditions
	 * 
	 * @author Daniel Whiteman
	 * */
	public void executeQuery() {
		//gets conditions and set of wine samples
		List<QueryCondition> conditions = this.getQueryConditionList();
		List<WineSample> list = cellar.getWineSampleList(wineType);
		
		//creates a new query object
		Query query = new Query(list, conditions, wineType);
		
		//If no results come back then error message is shown and clearFilters is shown
		//else set filteredWineSampleList to the results of calling solveQuery();
		if(query.solveQuery().size() == 0) {
			JOptionPane.showMessageDialog(this, "No samples fit your criteria");
			filteredWineSampleList = cellar.getWineSampleList(wineType);
			clearFilters();
		} else {
		    filteredWineSampleList = query.solveQuery();
		}		
	}
	
}