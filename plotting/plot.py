import matplotlib.pyplot as plt
import pandas as pd
import re


def get_average(df):
    a = df["k"].unique()
    a.sort()
    averages = []
    for value in a:
        mask = df['k'].values == value
        averages.append(df[mask]["time"].mean())
    return a, averages


def get_sec(time_str):
    m, s = time_str.split(':')
    return float(m) * 60 + float(s)


def get_comp_synth(df):
    complex_df = df[df["type"] == "complex"]
    synthetic_df = df[df["type"] == "synthetic"]
    return complex_df, synthetic_df


def load_data(path):
    data = pd.read_csv(path, sep='\t', names=["path", "time", "result", "steps"])
    clean_data = pd.DataFrame(columns=["type", "filename", "nodes", "edges", "k", "time", "steps"])
    counter = 0
    for index, row in data.iterrows():
        if not row["result"] == " 1 ":
            if "synthetic" in row["path"]:
                g_type = "synthetic"
            else:
                g_type = "complex"
            filename = str(row["path"].split("/")[3].split(" ")[0])
            nodes, edges = get_nodes(row["path"])
            with open("../instances/optimal_solution_sizes.txt", "r") as f:
                for line in f:
                    if filename in line:
                        k = int(line.split()[1])
            if "timelimit" in row["path"]:
                time = 200
            elif "error" in row["time"]:
                time = 200
            else:
                time = (get_sec(row["time"][1:8]))
            # if not pd.isna(row["steps"]):
            #     steps = int(re.findall(r'\d+', row["steps"])[0])
            #     counter += 1
            # else:
            steps = -1
            series = pd.Series([g_type, filename, nodes, edges, k, time, steps], index=clean_data.columns)
            clean_data = clean_data.append(series, ignore_index=True)
    return clean_data, counter


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


def plotting():
    first_path = "programs/v2/DFVS_v2.jar.res"
    second_path = "programs/DFVS_v1.jar.res"
    first_data, first_steps = load_data(first_path)
    second_data, second_steps = load_data(second_path)
    first_complex, first_synthetic = get_comp_synth(first_data)
    second_complex, second_synthetic = get_comp_synth(second_data)
    print(first_steps, second_steps)
    # plt.style.use('dark_background')
    # plt.xlabel("k")
    # plt.ylabel("time in s")
    # plt.yscale("log")
    # plt.xlim(0, 30)
    # # plt.xscale("log")
    # plt.title("All Graphs Verti")
    # plt.scatter(first_synthetic["k"], first_synthetic["time"], marker="x", c="yellow")
    # plt.plot([0, 250], [180, 180])
    # # plt.boxplot(complex["k"], complex["time"])
    # plt.show()

    a, averages = get_average(first_complex)
    b, baverages = get_average(first_complex)

    plt.style.use('dark_background')
    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.yscale("log")
    # plt.xscale("log")
    plt.xlim(0, 20)
    plt.title("Complex v1 vs v2")
    plt.scatter(first_complex["k"], first_complex["time"], marker="x", c="purple")
    plt.scatter(second_complex["k"], second_complex["time"], marker="x", c="yellow")
    # plot1, = plt.plot(a, averages, c="purple")
    # plot2, = plt.plot(b, baverages, c="yellow")
    # plt.legend([plots1, plots2], ['short cycle', 'normal cycle'])
    plt.plot([0, 260], [180, 180])
    plt.show()
    # print(first_complex["steps"].where(not -1))


if __name__ == '__main__':
    path = "programs/v2/DFVS_v2.jar.res"
    plotting()
