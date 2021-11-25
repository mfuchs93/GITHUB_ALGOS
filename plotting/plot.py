import matplotlib.pyplot as plt
import pandas as pd
import re


def get_sec(time_str):
    """Get Seconds from time."""
    m, s = time_str.split(':')
    return float(m) * 60 + float(s)


def get_nodes(path):
    path = path.split(" ")[0]
    nodes = []
    edges = 0
    with open(path, "r") as f:
        for line in f:
            if "%" not in line:
                nodes += re.findall(r'\d+', line)
                edges += 1
    return len(set(nodes)), edges


def plotting(path):
    data = pd.read_csv(path, sep='\t', names=["path", "time", "result", "steps"])
    clean_data = pd.DataFrame(columns=["type", "filename", "nodes", "edges", "k", "time", "steps"])
    for index, row in data.iterrows():
        if not row["result"] == " 1 ":
            if "synthetic" in row["path"]:
                type = "synthetic"
            else:
                type = "complex"
            filename = str(row["path"].split("/")[3].split(" ")[0])
            nodes, edges = get_nodes(row["path"])
            with open("../instances/optimal_solution_sizes.txt", "r") as f:
                for line in f:
                    if filename in line:
                        k = int(line.split()[1])
            if "timelimit" in row["path"]:
                time = 200
            else:
                time = (get_sec(row["time"][1:8]))
            if not pd.isna(row["steps"]):
                steps = int(re.findall(r'\d+', row["steps"])[0])
            else:
                steps = -1
            series = pd.Series([type, filename, nodes, edges, k, time, steps], index=clean_data.columns)
            clean_data = clean_data.append(series, ignore_index=True)
    complex = clean_data[clean_data["type"] == "complex"]
    synthetic = clean_data[clean_data["type"] == "synthetic"]

    plt.style.use('dark_background')
    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.yscale("log")
    plt.xlim(0, 30)
    # plt.xscale("log")
    plt.title("All Graphs Verti")
    plt.scatter(complex["k"], complex["time"], marker="x", c="yellow")
    plt.plot([0, 250], [180, 180])
    # plt.boxplot(complex["k"], complex["time"])
    plt.show()

    plt.style.use('dark_background')
    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.yscale("log")
    plt.xscale("log")
    plt.title("All Graphs Verti")
    plt.scatter(complex["steps"], complex["time"], marker="x", c="yellow")
    plt.scatter(synthetic["steps"], synthetic["time"], marker="x", c="red")
    plt.plot([0, 250], [180, 180])
    plt.show()


if __name__ == '__main__':
    path = "DFVS_tarjan_forbidden_reduction_shortCycles.jar.res"
    plotting(path)
