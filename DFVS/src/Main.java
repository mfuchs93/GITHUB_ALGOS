import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static int recursiveSteps = 0;

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
                //long time = System.currentTimeMillis();
                InputStream in = new FileInputStream(args[0]);
                Graph g = readGraphFromFile(in);
                HashSet<Vertex> s = DFVS.solve(g);
                for (Vertex i : s) {
                    System.out.println(i.getName());
                }
                System.out.println("#recursive steps: " + recursiveSteps);
                //System.out.println("time: " + (System.currentTimeMillis() - time));
            } catch (FileNotFoundException e) {
                System.out.println("File not found '" + args[0] + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            future.get(180, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
