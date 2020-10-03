import org.apache.commons.cli.*;

// TODO: HACER ESTO Y DEFINIR OPCIONES
public class OptionsParser {
    protected static RunOptions option;
    protected static Double delta;
    protected static String dynamicFile;
    protected static String staticFile;

    private static final String PARAM_DT = "dt";
    private static final String PARAM_RA = "ra";
    private static final String PARAM_RN = "rn";
    private static final String PARAM_RS = "rs";
    private static final String PARAM_DF = "df";
    private static final String PARAM_SF = "sf";

    /**
     * Generates the options for the help.
     *
     * @return Options object with the options
     */
    private static Options GenerateOptions() {
        Options options = new Options();
        options.addOption(PARAM_DT, "delta", true, "Delta of time to be used");
        options.addOption(PARAM_RA, "run_analytical", false, "Run the analytical solution");
        options.addOption(PARAM_RN, "run_numerical", false, "Run the numerical solution");
        options.addOption(PARAM_RS, "run_simulation", false, "Run the simulation");
        options.addOption(PARAM_DF, "dynamic_file", true, "Path to the file with the dynamic values.");
        options.addOption(PARAM_SF, "static_file", true, "Path to the file with the static values.");
        return options;
    }

    public static void ParseOptions(String[] args) {
        // Generating the options
        Options options = GenerateOptions();

        // Creating the parser
        CommandLineParser parser = new DefaultParser();

        try {
            // Parsing the options
            CommandLine cmd = parser.parse(options, args);

            // Parsing run options
            int runOptionsCount = 0;
            if (cmd.hasOption(PARAM_RA)) runOptionsCount++;
            if (cmd.hasOption(PARAM_RN)) runOptionsCount++;
            if (cmd.hasOption(PARAM_RS)) runOptionsCount++;

            if (runOptionsCount != 1){
                System.out.println("Only 1 run option must be specified");
                System.exit(1);
            } else {
                if (cmd.hasOption(PARAM_RA)) option = RunOptions.RUN_ANALYTICAL;
                else if (cmd.hasOption(PARAM_RN)) option = RunOptions.RUN_NUMERICAL;
                else if (cmd.hasOption(PARAM_RS)) option = RunOptions.RUN_SIMULATION;
            }

            // Checking if the time amount is present
            if (!cmd.hasOption(PARAM_DT)){
                System.out.println("A delta time must be specified");
                System.exit(1);
            }
            // Retrieving the amount of "time" to iterate with
            delta = Double.parseDouble(cmd.getOptionValue(PARAM_DT));

            // Checking if the files were present
            if (!cmd.hasOption(PARAM_SF) | !cmd.hasOption(PARAM_DF)){
                System.out.println("The dynamic and static file path are needed");
                System.exit(1);
            }

            // Parsing the file paths
            staticFile = cmd.getOptionValue(PARAM_SF);
            dynamicFile = cmd.getOptionValue(PARAM_DF);

        } catch (ParseException e) {
            System.out.println("Unknown command used");

            // Display the help again
            help(options);
        }
    }

    /**
     * Prints the help for the system to the standard output, given the options
     *
     * @param options Options to be printed as help
     */
    private static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }

    public enum RunOptions{
        RUN_ANALYTICAL,
        RUN_NUMERICAL,
        RUN_SIMULATION
    }
}
