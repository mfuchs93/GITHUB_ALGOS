import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static int recursiveSteps = 0;
    public static int chaining1 = 0;
    public static int chaining2 = 0;
    public static int chaining3 = 0;
    public static int preK = 0;
    public static String path = "";
    public static int indCliques = 0;
    public static int indCycles = 0;
    public static int petalOne = 0;
    public static int flowers = 0;
    public static int cliqueRule = 0;
    //public static HashSet<Vertex> packingFlowers = new HashSet<>();
    public static long time = 0;
    public static int lbFlower = 0;

    public static boolean USEDFVS = true;
    public static boolean HEURISTIC = false;


    public static void log(String path, int size) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("log.txt", true));
            pw.println(path + "\t" + chaining1 + "\t"+ chaining2 + "\t" + chaining3 + "\t" + recursiveSteps + "\t" + preK +
                    "\t" + CyclePacking.cancelCounter + "\t"+ indCliques + "\t" + indCycles + "\t" + petalOne + "\t" + flowers+ "\t" + cliqueRule+"\t" + (System.currentTimeMillis() - time) + "\t" + lbFlower + "\t" + size);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void countStep() {
        recursiveSteps++;
    }
    public static Graph readGraphFromFile(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        Graph g = new Graph();
        HashMap<String, Vertex> vertices = new HashMap<>();
        int id_counter = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.contains("#") && !line.contains("%") && !line.isEmpty()) {
                String[] arr = line.trim().split(" ");
                Vertex start;
                Vertex target;
                start = vertices.getOrDefault(arr[0], null);
                if (start == null) {
                    start = new Vertex(id_counter++, arr[0]);
                    vertices.put(arr[0], start);
                }
                target = vertices.getOrDefault(arr[1], null);
                if (target == null) {
                    target = new Vertex(id_counter++, arr[1]);
                    vertices.put(arr[1], target);
                }

                g.getOutEdges().putIfAbsent(start, new HashSet<>());
                g.getOutEdges().get(start).add(target);

                g.getInEdges().putIfAbsent(target, new HashSet<>());
                g.getInEdges().get(target).add(start);
            }
        }
        g.setId_counter(id_counter);
        return g;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("no file given");
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                time = System.currentTimeMillis();
                InputStream in = new FileInputStream(args[0]);
                path = args[0];
                Graph g = readGraphFromFile(in);
                HashSet<Vertex> s = null;
                if(USEDFVS) {
                    if (HEURISTIC){
                        s = DFVSHeuristic.solve(g);
                    } else {
                        s = DFVS.solve(g);
                    }
                } else {
                    Graph h = new Graph(g);
                    for (Vertex v : h.getVertices()) {
                        h.splitVertex(v);
                    }
                    s = DFASHeuristic.solve(h);
                }
                for (Vertex i : s) {
                    System.out.println(i.getName());
                }
                log(args[0], s.size());
                System.out.println("#recursive steps: " + recursiveSteps);
                //System.out.println("#" + packingFlowers);
                //System.out.println("time: " + (System.currentTimeMillis() - time));
            } catch (FileNotFoundException e) {
                System.out.println("File not found '" + args[0] + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            future.get(175, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
