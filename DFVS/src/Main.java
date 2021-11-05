import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static Graph readGraphFromFile(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        Graph g = new Graph();
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("#") || line.contains("%") || line.isEmpty()) {}
            else {
                String[] arr = line.trim().split(" ");
                String start = arr[0];
                String target = arr[1];

                g.getOutEdges().putIfAbsent(start, new HashSet<>());
                g.getOutEdges().get(start).add(target);

                g.getInEdges().putIfAbsent(target, new HashSet<>());
                g.getInEdges().get(target).add(start);
            }
        }
        return g;
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("no file given");
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(() -> {
            try {
                InputStream in = new FileInputStream(args[0]);
                Graph g = readGraphFromFile(in);
                HashSet<String> s = DFVS.solve(g);
                for(String i: s){
                    System.out.println(i);
                }
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
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
