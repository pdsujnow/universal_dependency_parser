package improve_supervised;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import multiple_source.Uti;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import multiple_source.*;

public class Read_SupervisedMST_Result {

	
	public static double read_result(String fileName, String flag) throws IOException{
		try {
			FileInputStream fis = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			String line = "";
			String uas = "Unlabeled Accuracy:";
			String las = "Labeled Accuracy:";
			double uasS = -1; 
			double lasS = -1; 
			while ((line = br.readLine())!=null){
				if (line.contains(uas))
					uasS = Double.parseDouble(line.replace(uas, ""));
				if (line.contains(las))
					lasS = Double.parseDouble(line.replace(las, ""));
			}
			br.close();
			fis.close();

			if (flag.equals("UAS")) return uasS; 
			else return lasS ;
			
		}
		catch (Exception e){
			return -1 ;
		}
	}
	
	public static void main(String[] args) throws Exception{
		
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

                
        options.addOption(OptionBuilder.withLongOpt("Langs").withDescription("Set of languages ").isRequired().hasArg().withArgName("Langs").create("Langs"));
        options.addOption(OptionBuilder.withLongOpt("DataPoints").withDescription("Data point (in k) for running ").isRequired().hasArg().withArgName("range").create("range"));
        options.addOption(OptionBuilder.withLongOpt("Prefix").withDescription("Prefix of result (e.g. result.sup) ").hasArg().withArgName("prefix").create("prefix"));
        options.addOption("h", "help", false, "Print this message");

        CommandLine commandLine = null;
        
        try {
            commandLine = parser.parse(options, args); // if not enough parameters ....
            if (commandLine.hasOption("help")) {       // also if help is presented
                throw new ParseException("");
            }
        } catch (ParseException exp) {
            System.out.println();
            if (exp.getMessage().length() > 0) {
                System.out.println("ERR: " + exp.getMessage());
                System.out.println();
            }
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(400, "java -mx4g " + Thread.currentThread().getStackTrace()[1].getClassName(), "\n", options, "\n", true);
            System.out.println();
            System.exit(0);
        }


        String Langs = commandLine.getOptionValue("Langs");
        String dataPoints = commandLine.getOptionValue("DataPoints");
        String prefix = "result.mst.sup";
        if (commandLine.hasOption("Prefix"))
        	prefix = commandLine.getOptionValue("Prefix");
        
        if (!Uti.verifyLanguages(Langs)){
        	throw new Exception(" Values of source languages are not correct ");
        }
        
        String[] dataList = dataPoints.split(",");
        String[] langList = Langs.split(",");
        System.out.println("UAS :");
        for (String data_size : dataList){
        	System.out.print(data_size +",");
        	for (String lang : langList){
        		String result_file = prefix +"." + lang + "." + data_size;
        		System.out.print(read_result(result_file, "UAS")+","); 
        	}
        	System.out.println();
        }
        
        System.out.println("LAS :");
        
        for (String data_size : dataList){
        	System.out.print(data_size +",");
        	for (String lang : langList){
        		String result_file = prefix +"." + lang + "." + data_size;
        		System.out.print(read_result(result_file, "LAS")+","); 
        	}
        	System.out.println();
        }

	}

}
