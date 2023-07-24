import algorithms.*;
import constructives.*;
import improvement.LocalSearch_Fact;
import structure.TSSInstance;
import structure.TSSInstanceFactory;
import grafo.optilib.results.Experiment;
import grafo.optilib.metaheuristics.Algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args){

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String date = String.format("%04d-%02d-%02d T%02d-%02d", year, month, day, hour, minute);


        int alg = 0; //Integer.parseInt(args[0]);
        int psi = 10;
        int initialPopulation = 20;
        String dir = "../instances";
        if(args.length==1){
            System.out.println("Custom instances path");
            dir = args[0];
        }
        else if(args.length==3){
            System.out.println("Custom instances path");
            System.out.println("Custom values in DPR");
            dir = args[0];
            psi = Integer.parseInt(args[1]);
            initialPopulation = Integer.parseInt(args[2]);
        }
        String outDir = "experiments/";
        File outDirCreator = new File(outDir);
        try {
            Files.createDirectories(Paths.get(outDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String outputFile = outDir + "/" + date + ".xlsx";


        /*ProcedureGurobi pg = new ProcedureGurobi(108000);
        List<Result> results = new ArrayList<>();
        String[] pathnames;
        File f = new File(dir+"/");
        pathnames = f.list();
        Arrays.sort(pathnames);
        for (String pathname : pathnames) {
            String path= dir+"/" + pathname;
            results.add(pg.execute(new TSSInstance(path)));
        }
        TableCreator.createTable(outputFile, results);*/

        System.out.println("Instances path " + dir);
        System.out.println("Selected psi " + psi + " selected initial population " + initialPopulation);
        String[] extensions = new String[]{".dat", ".txt"};
        String[] algorithm = new String[]{"DPR_"+psi+"-"+initialPopulation};
        TSSInstanceFactory factory = new TSSInstanceFactory();
        Algorithm<TSSInstance>[] execution = new Algorithm[]{
                new DPRAlg(new TSSGRASP_Heuristic(-1.00), new LocalSearch_Fact(), psi, initialPopulation), //24
        };

        outputFile = outDir + "/" + algorithm[alg] + ".xlsx";
        Experiment<TSSInstance, TSSInstanceFactory> experiment = new Experiment<>(execution[alg], factory);
        experiment.launch(dir, outputFile, extensions);

    }
}