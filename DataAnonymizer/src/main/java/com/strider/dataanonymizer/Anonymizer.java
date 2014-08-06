package com.strider.dataanonymizer;

import static com.strider.dataanonymizer.utils.AppProperties.loadProperties;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import static org.apache.log4j.Logger.getLogger;



/**
 * Entry point to Data Anonymizer. 
 *  
 * This class will parse and analyze the parameters and execute appropriate 
 * service.
 *
 */
public class Anonymizer  {
 
    private static final Logger log = getLogger(Anonymizer.class);

    public static void main( String[] args ) throws ParseException, AnonymizerException {

        if (args.length == 0 ) {
            log.info("To display usage info please type");
            log.info("    java -jar DataAnonymizer.jar com.strider.DataAnonymyzer help");
            return;
        }        

        final Options options = createOptions();
        final CommandLine line = getCommandLine(options, args);
        if (line.hasOption("help")) {
            help(options);
            return;
        } 
        
        String databasePropertyFile = "db.properties";
        Properties props = null;
        if (line.hasOption("D")) {
            databasePropertyFile = line.getOptionValues("D")[0];
            try {
                props = loadProperties(databasePropertyFile);            
            } catch (IOException ioe) {
                throw new AnonymizerException("ERROR: Unable to load " + databasePropertyFile, ioe);
            }
        }
        if (props == null) {
            throw new AnonymizerException("ERROR: Database property file is not defined.");
        }
        
        String anonymizePropertyFile = "anonymize.properties";
        if (line.hasOption("A")) {
            anonymizePropertyFile = line.getOptionValue("A");
        } 
        
        Properties anonymizerProperties = null;
        try {
            anonymizerProperties = loadProperties(anonymizePropertyFile);
        } catch (IOException ioe) {
            throw new AnonymizerException("ERROR: Unable to load " + databasePropertyFile, ioe);
        }
        if (anonymizerProperties == null) {
            throw new AnonymizerException("ERROR: Database property file is not defined.");
        }                    
        
        if (line.hasOption("a")) {
            IAnonymizer anonymizer = new DatabaseAnonymizer();
            anonymizer.anonymize(databasePropertyFile, anonymizePropertyFile);
        }
    }
    
    /**
     * Parses command line arguments
     * @param options
     * @param args
     * @return CommandLine
     * @throws AnonymizerException 
     */
    private static CommandLine getCommandLine(final Options options, final String[] args) 
    throws ParseException {
        final CommandLineParser parser = new GnuParser();
        CommandLine line = null;
 
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            help(options);
        }
 
        return line;
    }    
    
    /**
     * Creates options for the command line
     * 
     * @return Options
     */
    @SuppressWarnings("static-access")
    private static Options createOptions() {
        final Options options = new Options();
        options.addOption("help", false, "Display help");
        options.addOption( "a", "anonymize", false, "anonymize database" );
        options.addOption( "D", "database properties", true, "define database property file" );
        options.addOption( "A", "anonymizer properties", true, "define anonymizer property file" );
        
        return options;
    }
 
    /**
     * Displays help
     * 
     * @param Options 
     */
    private static void help(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DataAnonymizer", options);
    }    
}