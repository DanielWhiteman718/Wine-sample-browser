package assignment2019;
import assignment2019.codeprovided.*;
import javax.swing.*;
import java.text.DecimalFormat;
import java.util.List;

/**
* WineSampleCellar.java
*
* Main class
* 
* @author Daniel Whiteman
*
*/
public class WineSampleBrowser {
	
	public static void main(String[] args) {
		if(args.length == 0) {
			args = new String[] {
					"resources/winequality-red.csv",
					"resources/winequality-white.csv",
					"resources/queries.txt"};
			}
		String redWineFile = args[0];
		String whiteWineFile = args[1];
		String queriesFile = args[2];
		
		//Instantiating the WineSampleCellar and WineSampleBrowserPanel objects
		WineSampleCellar cellar = new WineSampleCellar(redWineFile, whiteWineFile, queriesFile);
		WineSampleBrowserPanel gui = new WineSampleBrowserPanel(cellar);
		
		//Sets up a frame and adds gui to it
		JFrame f = new JFrame("browser");
		f.setVisible(true);
		f.setSize(1800, 1000);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(gui);
		gui.updateStatistics();
		gui.updateWineList();
		
		
		List<Query> querys = cellar.readQueries(WineSampleCellar.readQueryFile(queriesFile));
		for(Query query : querys) {
			System.out.println("[QUERY] WineType: "+query.getWineType()+
					" conditions: "+query.getQueryConditionList());
			cellar.displayQueryResults(query);
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		}
		
		System.out.println("######################");
		System.out.println("######################");
		System.out.println("######################");
		System.out.println("######################");

		//Answering questions
		System.out.println("Q1. Total number of wine samples: "+cellar.getWineSampleList(WineType.ALL).size());
		System.out.println("Q2. Total number of red wine samples: "+cellar.getWineSampleList(WineType.RED).size());
		System.out.println("Q3. Total number of white wine samples: "+cellar.getWineSampleList(WineType.WHITE).size());
		System.out.println("\n");
		
		
		System.out.println("Q4. Top three best quality wine samples are: ");
		for(WineSample sample: cellar.bestQualityWine(WineType.ALL)) {
			System.out.println("* Wine ID "+sample.getId()+" of type "+
						sample.getType()+" with a quality of "+sample.getQuality());
		}
		System.out.println("\n");
		
		
		System.out.println("Q5. Worst three quality wine samples are: ");
		for(WineSample sample: cellar.worstQualityWine(WineType.ALL)) {
			System.out.println("* Wine ID "+sample.getId()+" of type "+
						sample.getType()+" with a quality of "+sample.getQuality());
		}
		System.out.println("\n");
		
		
		System.out.println("Q6. Top three highest PH wine samples are: ");
		for(WineSample sample: cellar.highestPH(WineType.ALL)) {
			System.out.println("* Wine ID "+sample.getId()+" of type "+
						sample.getType()+" with a PH of "+sample.getpH());
		}
		System.out.println("\n");
		
		
		System.out.println("Q7. Three lowest PH wine samples are: ");
		for(WineSample sample: cellar.lowestPH(WineType.ALL)) {
			System.out.println("* Wine ID "+sample.getId()+" of type "+
						sample.getType()+" with a PH of "+sample.getpH());
		}
		System.out.println("\n");
		
		
		System.out.println("Q8. Highest value of alcohol grade for red wine samples: "+
									String.format("%.2f",cellar.highestAlcoholContent(WineType.RED)));
		System.out.println("Q9. Lowest value of citric acid for white wine samples: "+
									String.format("%.2f",cellar.lowestCitricAcid(WineType.WHITE)));
		System.out.println("Q10. Average value of alcohol grade for white wine samples: "+
									String.format("%.2f",cellar.averageAlcoholContent(WineType.WHITE)));

	}
	
}
