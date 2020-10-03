import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        long startTime = Instant.now().toEpochMilli();

        // Parsing the options
        OptionsParser.ParseOptions(args);

        try {
            // Parsing the initial configuration
            ConfigurationParser.ParseConfiguration(OptionsParser.staticFile, OptionsParser.dynamicFile);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }

        // Determine what to run
        switch (OptionsParser.option){
            case RUN_ANALYTICAL:
                runAnalytic();
                break;
            case RUN_NUMERICAL:
                runNumerical();
                break;
            case RUN_SIMULATION:
                runSimulation();
                break;
        }

        long endTime = Instant.now().toEpochMilli();

        long total = endTime - startTime;

        System.out.format("Total Time %d millis\n", total);
    }

    private static void runAnalytic(){

    }

    private static void runNumerical(){

    }

    private static void runSimulation(){

    }

    private static void GenerateOutputFile(Collection<Particle> particles, double time) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(OptionsParser.dynamicFile, true));
            bf.append(String.format("%f\n", time));

            // Creating the output for the file
            particles.forEach(p -> {
                String line = p.getX() + " " + p.getY() + " " + p.getVx() + " " + p.getVy() + "\n";
                try {
                    bf.append(line);
                } catch (IOException e) {
                    System.out.println("Error writing to the output file");
                }
            });

            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error writing to the output file");
        }
    }
}

