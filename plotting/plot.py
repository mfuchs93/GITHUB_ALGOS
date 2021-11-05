import math

import matplotlib.pyplot as plt
import pandas as pd


def get_sec(time_str):
    """Get Seconds from time."""
    m, s = time_str.split(':')
    return float(m) * 60 + float(s)


def plotting (path):
    data = pd.read_csv(path, sep='\t', names=["path", "time", "column3", "column4"])
    # with open(path) as f:
    #    lines = f.readlines()
    vertices = []
    times = []
    ks = []
    for path in data["path"]:
        x = path.split("-")
        vertices.append(int(x[1][2:]))
    for time in data["time"]:
        times.append(get_sec(time[1:8]))
    for k in data["column3"]:
        if type(k) is float:
            ks.append(15)
        else:
            k = k.split(",")
            ks.append(int(k[0][1:]))
    print(ks)
    plt.xlabel("vertices")
    plt.ylabel("time in s")
    plt.scatter(vertices, times)
    plt.show()

    plt.xlabel("k")
    plt.ylabel("time in s")
    plt.scatter(ks, times)
    plt.show()


if __name__ == '__main__':
    path = "../DFVS.jar.res"
    plotting(path)
