package structure;

import grafo.optilib.structure.Instance;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TSSInstance implements Instance {

    private String name;
    private int[][] instance;
    private float[][] psi;
    private int[][] psiInt;
    private float[] effort;
    private float[] reward;
    private float k_min;
    private float k_max;
    private int row;
    private int col;
    private int[] numAdjList;
    private long maxTime;

    public TSSInstance(String path) {
        readInstance(path);
    }

    @Override
    public void readInstance(String s) {
        BufferedReader br = null;
        String line;
        String[] size;
        s = s.replace("\\","/");
        this.name = s.split("/")[s.split("/").length-1];
        try {
            br = new BufferedReader(new FileReader(s));
            size = br.readLine().split(" "); //Línea del tamaño

            this.row = Integer.parseInt(size[0]);
            this.col = Integer.parseInt(size[1]);
            this.numAdjList = new int[this.row];

            this.instance = new int[this.row][this.col];
            br.readLine(); //Línea del nombre de la instancia
            br.readLine(); //Línea vacía
            br.readLine(); //Línea "DATA:"
            line = br.readLine(); //Línea bien
            for (int i = 0; i < this.row; i++) {
                int aux = 0;
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (!part.equals(" ")) {
                        this.instance[i][aux] = Integer.parseInt(part);
                        this.numAdjList[i] += Integer.parseInt(part);
                        aux += 1;
                    }
                }
                line = br.readLine();
            }

            this.psi = new float[this.row][this.row];
            this.psiInt = new int[this.row][this.row];

            br.readLine(); //Línea "PSI:"
            line = br.readLine(); //Línea bien
            int aux, sum, i;
            for (i = 0; i < this.row; i++) {
                aux = 0;
                String[] parts = line.split(" ");
                sum = 0;
                for (String part : parts) {
                    if (!part.equals(" ")) {
                        this.psi[i][aux] = Float.parseFloat(part);
                        this.psiInt[i][aux] = (int)(this.psi[i][aux] * 100);
                        sum += (int)(this.psi[i][aux] * 100);
                        aux += 1;
                    }
                }
                line = br.readLine();
            }

            this.effort = new float[this.row];
            br.readLine(); //Línea "ALPHA:"
            line = br.readLine(); //Línea bien
            String[] parts = line.split(" ");
            for (i = 0; i < parts.length; i++) {
                if (!parts[i].equals(" ")) {
                    this.effort[i] = Float.parseFloat(parts[i]);
                }
            }

            this.reward = new float[this.row];
            br.readLine(); //Línea vacía
            br.readLine(); //Línea "BETA:"
            line = br.readLine(); //Línea bien
            parts = line.split(" ");
            for (i = 0; i < parts.length; i++) {
                if (!parts[i].equals(" ")) {
                    this.reward[i] = Float.parseFloat(parts[i]);
                }
            }

            br.readLine(); //Línea vacía
            br.readLine(); //Línea "K_MIN:"
            this.k_min = Integer.parseInt(br.readLine());

            br.readLine(); //Línea vacía
            br.readLine(); //Línea "K_MAX:"
            this.k_max = Float.parseFloat(br.readLine());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error closing file");
            }
        }
    }

    public String getName() {
        return name;
    }

    public int[][] getInstance() {
        return instance;
    }

    public float[][] getPsi() {
        return psi;
    }

    public float[] getEffort() {
        return effort;
    }

    public float[] getReward() {
        return reward;
    }

    public float getK_min() {
        return k_min;
    }

    public float getK_max() {
        return k_max;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int[] getNumAdjList() {
        return numAdjList;
    }

    public int[][] getPsiInt() {
        return psiInt;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

}
