import math
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# TODO: pandas cleanup
def check_solved(solution):
    if type(solution) is float:
        return(15)
    else:
        solution = solution.split(",")
        return(int(solution[0][1:]))


def get_sec(time_str):
    """Get Seconds from time."""
    m, s = time_str.split(':')
    return float(m) * 60 + float(s)


def plotting (path):
    data = pd.read_csv(path, sep='\t', names=["path", "time", "column3", "column4"])
    print(data)
    vertices = []
    complexverti = []
    complexarcs = []
    syntheticverti = []
    syntheticarcs = []
    times = []
    ks = []
    counter = 0
    complextime = []
    complexk = []
    synthetictime = []
    synthetick = []
    arcs = []
    for path in data["path"]:
        x = path.split("-")
        print(x)
        xarcs = x[2].split(".")
        print(xarcs)
        vertices.append(int(x[1][2:]))
        arcs.append(int(xarcs[0][2:]))
        if "complex" in path:
            complexverti.append(int(x[1][2:]))
            complexarcs.append(int(xarcs[0][2:]))
            complextime.append(get_sec(data.at[counter, "time"][1:8]))
            complexk.append(check_solved(data.at[counter, "column3"]))
        if "synthetic" in path:
            syntheticverti.append(int(x[1][2:]))
            syntheticarcs.append(int(xarcs[0][2:]))
            synthetictime.append(get_sec(data.at[counter, "time"][1:8]))
            synthetick.append(check_solved(data.at[counter, "column3"]))

        counter += 1

    print(complex, complextime)
    colors = np.where(data["path"].str.contains("complex"), "red",
                      np.where(data["path"].str.contains("synthetic"), "green", "NA"))

    for time in data["time"]:
        times.append(get_sec(time[1:8]))
    for k in data["column3"]:
        ks.append(check_solved(k))

    print(ks)
    plt.style.use('dark_background')
    plt.xlabel("vertices")
    plt.ylabel("time in s")
    plt.title("All Graphs Verti")
    plt.scatter(vertices, times, marker="x")
    plt.show()

    plt.xlabel("arcs")
    plt.ylabel("time in s")
    plt.title("All Graphs Arcs")
    plt.scatter(arcs, times, marker="x")
    plt.show()

    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.title("All Graphs k")
    plt.scatter(ks, times, marker="x")
    plt.show()

    plt.xlabel("vertices")
    plt.ylabel("time in s")
    plt.title("Complex Verti")
    plt.scatter(complexverti, complextime, c="red", marker="x")
    plt.show()

    plt.xlabel("arcs")
    plt.ylabel("time in s")
    plt.title("Complex Arcs")
    plt.scatter(complexarcs, complextime, c="red", marker="x")
    plt.show()

    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.title("Complex K")
    plt.scatter(complexk, complextime, c="red", marker="x")
    plt.show()

    plt.xlabel("vertices")
    plt.ylabel("time in s")
    plt.title("Synthetic Verti")
    plt.scatter(syntheticverti, synthetictime, c="green", marker="x")
    plt.show()

    plt.xlabel("arcs")
    plt.ylabel("time in s")
    plt.title("Synthetic Arcs")
    plt.scatter(syntheticarcs, synthetictime, c="green", marker="x")
    plt.show()

    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.title("Synthetic K")
    plt.scatter(synthetick, synthetictime, c="green", marker="x")
    plt.show()




if __name__ == '__main__':
    path = "DFVS.jar.res"
    plotting(path)