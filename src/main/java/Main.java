import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;

public class Main {
    private static final String BEEMAN_FILE = "./parsable_files/beeman.txt";
    private static final String VERLET_FILE = "./parsable_files/verlet.txt";
    private static final String GEAR_FILE = "./parsable_files/gear.txt";
    private static final String ANALYTIC_FILE = "./parsable_files/analytic.txt";

    public static void main(String[] args) {
        long startTime = Instant.now().toEpochMilli();

        // Parsing the options
        OptionsParser.ParseOptions(args);

        /*
        try {
            // Parsing the initial configuration
            ConfigurationParser.ParseConfiguration(OptionsParser.staticFile, OptionsParser.dynamicFile);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }
         */

        // Determine what to run
        switch (OptionsParser.option){
            case RUN_ANALYTICAL:
                runAnalytic(OptionsParser.totalTime, OptionsParser.delta, OptionsParser.timeMultiplicator);
                break;
            case RUN_NUMERICAL:
                runNumerical(OptionsParser.numericalOption, OptionsParser.totalTime, OptionsParser.delta, OptionsParser.timeMultiplicator);
                break;
            case RUN_SIMULATION:
                runSimulation();
                break;
        }

        long endTime = Instant.now().toEpochMilli();

        long total = endTime - startTime;

        System.out.format("Total Time %d millis\n", total);
    }

    private static void runAnalytic(double tf, double dt, int tm){
        System.out.println("Running ANALYTICAL solution...");
        OscillatorSimulation os = new OscillatorSimulation(tf, dt, tm);
        double[][] results = os.runAnalytical();
        GenerateOutputFileForOscillator(results, ANALYTIC_FILE);
    }

    private static void runNumerical(OptionsParser.NumericalOptions option, double tf, double dt, int tm){
        OscillatorSimulation os = new OscillatorSimulation(tf, dt, tm);
        double[][] results;

        switch (option){
            case RUN_GEAR:
                System.out.println("Running GEAR PREDICTOR CORRECTOR solution...");
                results = os.runGearPredictorCorrector();
                GenerateOutputFileForOscillator(results, GEAR_FILE);
                break;
            case RUN_VERLET:
                System.out.println("Running VERLET solution...");
                results = os.runVerlet();
                GenerateOutputFileForOscillator(results, VERLET_FILE);
                break;
            case RUN_BEEMAN:
                System.out.println("Running BEEMAN solution...");
                results = os.runBeeman();
                GenerateOutputFileForOscillator(results, BEEMAN_FILE);
                break;
        }
    }

    private static void runSimulation(){
        // TODO: IMPLEMENTAR ESTO
    }

    private static void GenerateOutputFileForOscillator(double[][] results, String filename) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(filename, true));

            for (double[] result : results) {
                // Adding the time
                bf.append(String.format("%f ", result[0]));

                // Adding the position
                String line = result[1] + "\n";
                try {
                    bf.append(line);
                } catch (IOException e) {
                    System.out.println("Error writing to the output file");
                }
            }

            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error writing to the output file");
        }
    }
}

