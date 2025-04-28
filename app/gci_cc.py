# File: gci_cc.py
# Remarks: GCInsights datasets cluster comparison
# Author: ms
# Date: May 16, 2019
# Last updated: June 16, 2019
# version: 2.1
# Usage:
# python gci_cc.py --help"
# python gci_cc.py --version"
# python gci_cc.py --eval MKM AFP MSH SPC GMX
# python gci_cc.py --clusters 5 --mcs 0.1 --mcsi 0.02 --eval MKM AFP MSH SPC GMX
# python gci_cc.py --omic protein --fraction gcm --eval MKM AFP MSH
# python gci_cc.py --omic protein --fraction both --eval MKM AFP MSH
# python gci_cc.py --omic lipid --fraction gcp --eval MKM MSH
# python gci_cc.py --omic both --fraction both --eval MKM MSH
# python gci_cc.py --clusters 5 --mcs 0.1 --mcsi 0.02 --filename figure_p_gcm.png --omic protein --fraction gcm --eval MKM AFP MSH
# python gci_cc.py --clusters 5 --mcs 0.05 --mcsi 0.03 --filename figure_l_both.png --omic lipids --fraction both --eval MKM AFP MSH
# python gci_cc.py --clusters 5 --mcs 0.1 --mcsi 0.02 --mcsic 4 --eval MKM AFP MSH SPC GMX

# Python version in runtime.txt is set to python-3.6.8
# App does not load on python-3.7.13

import warnings
import os
from pathlib import Path
import sys
from random import randint

import numpy as np
import matplotlib.pyplot as plt
import pandas

from sklearn import cluster, datasets, mixture
from sklearn.neighbors import kneighbors_graph
from sklearn.preprocessing import StandardScaler
from itertools import cycle, islice

strGCISig = "gci_cc:"
strGCIVersion = "2.1"

def readDataset(cloud_url, strDataset, strFraction):
    print(strGCISig, "data file = ", cloud_url)
    if (strDataset.startswith("protein")):
        names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6","P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6","P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]
    else:
        names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5","E18_6", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6","P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6","P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]

    print(strGCISig, "data file = ", cloud_url)    
    df = pandas.read_csv(cloud_url, names=names, header=None, skiprows=2)
    if (strDataset.startswith("protein")):
        col = df.loc[: , "E18_1":"E18_5"]
    else:
        col = df.loc[: , "E18_1":"E18_6"]
    
    df['E18'] = col.mean(axis=1)
    col = df.loc[: , "P0_1":"P0_6"]
    df['P0'] = col.mean(axis=1)
    col = df.loc[: , "P3_1":"P3_6"]
    df['P3'] = col.mean(axis=1)
    col = df.loc[: , "P6_1":"P6_6"]
    df['P6'] = col.mean(axis=1)
    col = df.loc[: , "P9_1":"P9_6"]
    df['P9'] = col.mean(axis=1)
    df = df.filter(['Gene', 'E18','P0','P3','P6','P9'], axis=1)
    return(df)
    
def watermarkPlot(plt, params, omics,fraction):
    plt.xlim(-2.5, 2.5)
    plt.ylim(-2.5, 2.5)
    plt.xticks(())
    plt.yticks(())
    if (omics == "both"):
        om = "protein/lipids"
    else:
        om = omics
    if (fraction == "both"):
        frc = "gcm/gcp"
    else:
        frc = fraction
        
    plt.text(.99, .01, ('%1s:%1s NC=%d MCS=%0.2f' % (om, frc, params['n_clusters'], params['min_cluster_size'])).lstrip('0'),transform=plt.gca().transAxes, size=8,horizontalalignment='right')

def setFigureSize(plt):
    plt.figure(figsize=(24,12))
    plt.subplots_adjust(left=.05, right=.95, bottom=.05, top=.95, wspace=.05,hspace=.01)
    
def genFigures(tuples, figure_file_name, no_of_clusters, min_cluster_size, min_cluster_size_increment, min_cluster_size_iterations_count, omics, fraction, cmp_algorithms):    
    default_base = {'quantile': .3,
                        'eps': .3,
                        'damping': .9,
                        'preference': -200,
                        'n_neighbors': 10,
                        'n_clusters': int(no_of_clusters),
                        'min_samples': 20,
                        'xi': 0.05,
                        'min_cluster_size': float(min_cluster_size)
                        }

    datasets = [
        (tuples, {'min_cluster_size': float(min_cluster_size)}),
        (tuples, {'min_cluster_size': float(min_cluster_size)+float(min_cluster_size_increment)}),
        (tuples, {'min_cluster_size': float(min_cluster_size)+float(min_cluster_size_increment)*2}),
        (tuples, {'min_cluster_size': float(min_cluster_size)+float(min_cluster_size_increment)*3})]
    
    fileRandSeq = randint(100,999)
    
    bOneFile = 1
    plot_num = 1
    if (bOneFile):
        setFigureSize(plt)

    j = 0                        
    for i_dataset, (dataset, algo_params) in enumerate(datasets):
        if (j >= int(min_cluster_size_iterations_count)):
            break;
        
        if (not bOneFile):
            setFigureSize(plt)

        params = default_base.copy()
        params.update(algo_params)

        X, y = dataset

        X = StandardScaler().fit_transform(X)
        bandwidth = cluster.estimate_bandwidth(X, quantile=params['quantile'])
        connectivity = kneighbors_graph(X, n_neighbors=params['n_neighbors'], include_self=False)
        connectivity = 0.5 * (connectivity + connectivity.T)

        ms = cluster.MeanShift(bandwidth=bandwidth, bin_seeding=True)
        two_means = cluster.MiniBatchKMeans(n_clusters=params['n_clusters'])
        ward = cluster.AgglomerativeClustering(n_clusters=params['n_clusters'], linkage='ward',connectivity=connectivity)
        spectral = cluster.SpectralClustering(n_clusters=params['n_clusters'], eigen_solver='arpack',affinity="nearest_neighbors")
        dbscan = cluster.DBSCAN(eps=params['eps'])
        optics = cluster.OPTICS(min_samples=params['min_samples'],xi=params['xi'],min_cluster_size=params['min_cluster_size'])
        affinity_propagation = cluster.AffinityPropagation(
        damping=params['damping'], preference=params['preference'])
        average_linkage = cluster.AgglomerativeClustering(linkage="average", metric="cityblock",n_clusters=params['n_clusters'], connectivity=connectivity)
        birch = cluster.Birch(n_clusters=params['n_clusters'])
        gmm = mixture.GaussianMixture(n_components=params['n_clusters'], covariance_type='full')

        clustering_algorithms = (
            ('Mini Batch KMeans', two_means),
            ('Spectral Clustering', spectral),
            ('Agglomerative Clustering', average_linkage),
            ('Affinity Propagation', affinity_propagation),
            ('Ward', ward),
            ('Gaussian Mixture', gmm),
            ('Mean Shift', ms),
            ('Birch', birch),
            ('DBSCAN', dbscan),
            ('OPTICS', optics)
        )
        
        if (not bOneFile):
            plot_num = 1
            
        j += 1    
        for name, algorithm in clustering_algorithms:
            if (_isAlgorithmSelectedForComparison(name, cmp_algorithms)):
                algorithm.fit(X)

                if hasattr(algorithm, 'labels_'):
                    y_pred = algorithm.labels_.astype(int)
                else:
                    y_pred = algorithm.predict(X)

                plt.subplot(min_cluster_size_iterations_count, len(cmp_algorithms), plot_num)
                
                if i_dataset == 0:
                    plt.title(name, size=8)
                colors = np.array(list(islice(cycle(['#377eb8', '#ff7f00', '#4daf4a',
                                                                    '#f781bf', '#a65628', '#984ea3',
                                                                    '#999999', '#e41a1c', '#dede00']),
                                                        int(max(y_pred) + 1))))
                colors = np.append(colors, ["#000000"])
                plt.scatter(X[:, 0], X[:, 1], s=10, color=colors[y_pred])
                watermarkPlot(plt, params, omics, fraction)                

                if (not bOneFile):
                    if (figure_file_name == ""):
                        strFigureFile="gci_cc_figure_"+str("%d_%d_%0.2f" % (fileRandSeq, params['n_clusters'], params['min_cluster_size']))+".png"
                    else:
                        strFigureFile = figure_file_name
                                                
                    print(strGCISig, "Output file: ", strFigureFile)
                    plt.savefig(strFigureFile)
                    
                plot_num += 1
                print(strGCISig, "plot_num=", plot_num)
                
    if (bOneFile):    
        if (figure_file_name == ""):
            strFigureFile="gci_cc_figure_"+str("%d_%d_%0.2f" % (fileRandSeq, params['n_clusters'], params['min_cluster_size']))+".png"
        else:
            strFigureFile = figure_file_name
        print(strGCISig, "Output file: ", strFigureFile)
        plt.savefig(strFigureFile)

def main():    
    if (_showingHelp()):
        _showHelp()
        exit(0)
        
    if (_showingVersions()):
        _showVersions()
        exit(0)

    if (not _showingEval()):
        _showHelp()
        exit(0)
        
    # set default data folder and home path
    
    strDSLabel=["GCM_protein", "GCP_protein", "GCM_lipid", "GCP_lipid"]

    cloudinary_urls = {
        "GCM_protein": "https://res.cloudinary.com/dwngugeu6/raw/upload/v1745530118/GCM_GeneNames_Normalized.csv",
        "GCP_protein": "https://res.cloudinary.com/dwngugeu6/raw/upload/v1745530118/GCP_GeneNames_Normalized.csv",
        "GCP_lipid": "https://res.cloudinary.com/dwngugeu6/raw/upload/v1745530118/GCP_LipidClass_Normalized.csv",
        "GCM_lipid": "https://res.cloudinary.com/dwngugeu6/raw/upload/v1745530118/GCM_LipidClass_Normalized.csv"
    }
    df1 = readDataset(cloudinary_urls["GCM_protein"], "protein", "gcm")
    df2 = readDataset(cloudinary_urls["GCP_protein"], "protein", "gcp")
    df3 = readDataset(cloudinary_urls["GCM_lipid"], "lipids", "gcm")
    df4 = readDataset(cloudinary_urls["GCP_lipid"], "lipids", "gcp")

    df1.assign(DS=strDSLabel[0])
    df2.assign(DS=strDSLabel[1])
    df3.assign(DS=strDSLabel[2])
    df4.assign(DS=strDSLabel[3])
    df = pandas.concat([df1, df2, df3, df4])    
    df_1_2 = pandas.concat([df1, df2])    
    df_3_4 = pandas.concat([df3, df4])
    
    omics = _getOmicsLayer()
    fraction = _getFraction()

    if (omics == "protein" and fraction == "gcm"):
        subset = df1[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "protein" and fraction == "gcp"):
        subset = df2[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "lipids" and fraction == "gcm"):
        subset = df3[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "lipids" and fraction == "gcp"):
        subset = df4[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "protein" and fraction == "both"):
        subset = df_1_2[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "lipids" and fraction == "both"):
        subset = df_3_4[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    elif (omics == "both" and fraction == "both"):
        subset = df[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]
    else:
        subset = df1[['E18', 'P0', 'P3', 'P6', 'P9']]
        tuples = [tuple(x) for x in subset.values]
        tuples = tuples, df[['Gene']]

    no_of_clusters = _getNumberOfClusters()
    min_cluster_size = _getMCS()
    min_cluster_size_increment = _getMCSI()
    cmp_algorithms = _getComparisonAlgorithms()
    min_cluster_size_iterations_count = int(_getMCSIC(omics, fraction, len(cmp_algorithms)))
    figure_file_name = _getFigureFileName()
    
    _displayArguments()    
    genFigures(tuples, figure_file_name, no_of_clusters, min_cluster_size, min_cluster_size_increment, min_cluster_size_iterations_count, omics, fraction, cmp_algorithms)

def _isAlgorithmSelectedForComparison(strAlgorithmName, cmp_algorithms):
    bFound = 0
    if (strAlgorithmName == "Mini Batch KMeans" and "MKM" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Affinity Propagation" and "AFP" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Mean Shift" and "MSH" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Spectral Clustering" and "SPC" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Ward" and "WRD" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Agglomerative Clustering" and "AGC" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "DBSCAN" and "DBS" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "OPTICS" and "OPT" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Birch" and "BRC" in cmp_algorithms):
        bFound = 1
    elif (strAlgorithmName == "Gaussian Mixture" and "GMX" in cmp_algorithms):
        bFound = 1
    return(bFound)

def _parseArgumets(arg):
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == arg):
            return 1

def _getOmicsLayer():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--omic"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("protein")

def _getFraction():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--fraction"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("gcm")

def _getMCS():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--mcs"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("0.1")

def _getMCSI():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--mcsi"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("0.01")

def _getMCSIC(omics, fraction, cmpAlgorithmsCount):
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--mcsic"):
            if (len(sys.argv) > i):
                return int(sys.argv[i+1])
    
    if (omics == "both" or fraction == "both" or cmpAlgorithmsCount > 2):
        defaultMCSIC = 1
    else:
        defaultMCSIC = 2
    return(defaultMCSIC)
        
def _getFigureFileName():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--filename"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("")

def _getNumberOfClusters():
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--clusters"):
            if (len(sys.argv) > i):
                return(sys.argv[i+1])
    return("3")

def _getComparisonAlgorithms():
    defAlgorithms = ["MKM", "AFP"]
    for i in range(1, len(sys.argv)):
        if (sys.argv[i] == "--eval"):
            i += 1
            algorithms = []
            j = i
            k = 0
            while j < len(sys.argv):
                if (_isValidAlgorithmName(sys.argv[j])):
                    algorithms.append(sys.argv[j])
                    k += 1
                j +=1
            
            if (len(algorithms) >=2):
                return(algorithms)
            else:
                print(strGCISig, "At least two algorithms are required. Using default algorithms: MKM and AFP")
                
    return(defAlgorithms)
    
def _showingHelp():
    return(_parseArgumets("--help"))

def _showingVersions():
    return(_parseArgumets("--version"))

def _showingEval():
    return(_parseArgumets("--eval") and len(sys.argv) >= 4)

def _displayArguments():
    if (_bDebug):
        omics = _getOmicsLayer()
        print(strGCISig, "omics = ", omics)
        fraction = _getFraction()
        print(strGCISig, "fraction = ", fraction)

        if (omics == "protein" and fraction == "gcm"):
            print(strGCISig, "protein gcm")
        elif (omics == "protein" and fraction == "gcp"):
            print(strGCISig, "protein gcp")
        elif (omics == "lipid" and fraction == "gcm"):
            print(strGCISig, "lipids gcm")
        elif (omics == "lipids" and fraction == "gcp"):
            print(strGCISig, "lipids gcm")
        elif (omics == "protein" and fraction == "both"):
            print(strGCISig, "protein both")
        elif (omics == "lipids" and fraction == "both"):
            print(strGCISig, "lipids both")
        elif (omics == "both" and fraction == "both"):
            print(strGCISig, "both both")
        else:
            print(strGCISig, "protein gcm")

        print(strGCISig, "Number of clusters = ", _getNumberOfClusters())
        print(strGCISig, "Min. cluster size = ", _getMCS())
        print(strGCISig, "Min. cluster size increment = ", _getMCSI())

        cmp_algorithms = _getComparisonAlgorithms()
        print(strGCISig, "Selected algorithms for comparison = ", cmp_algorithms)

def _showHelp():
    print(strGCISig, strGCIVersion, "syntax gci_cc [--version] [--help] [--omic protein or lipid or both] [--fraction gcm or gcp or both] [--clusters n] [--mcs f] [--msci f] [--mcsc n] [--filename figure_filename.png] --eval two or more algorith names")
    print("--help: show this help message");
    print("--version: show version info for Python runtime and ML libraries");
    print("--omic: select omics layer. Valid values are protein, lipid, both. Default protein")
    print("--fraction: select fraction. Valid values are gcm, gcp, both. Default gcm")
    print("--clusters: specify number of clusters as an integer. Default 3. Must be between 2 and 10")
    print("--mcs: specify minimum cluster size as a percentage. Default 0.1. Must be between 0.01 and 0.5")
    print("--msci: specify minimum cluster size as a percentage. Default 0.01. Must be between 0.01 and 0.1")
    print("--mcsc: specify number of increment to perform as an interger from 1 to 4. Default 1 if both omics or both fractions or if algorithms selected > 2. Default is 2 otherwise.")
    print("--filename: sepcify figure file name. Optional. Default file name figure_gci_cc_figure*.png]")
    print("--eval: show evaluation of clustering algorithms of two or more algorith names")
    print("Examples:")
    print("python gci_cc.py --help")
    print("python gci_cc.py --version")
    print("python gci_cc.py --eval MKM AFP MSH SPC GMX")
    print("python gci_cc.py --clusters 5 --mcs 0.1 --mcsi 0.02 --eval MKM AFP MSH SPC GMX")
    print("python gci_cc.py --omic protein --fraction gcm --eval MKM AFP MSH")
    print("python gci_cc.py --omic protein --fraction both --eval MKM AFP MSH")
    print("python gci_cc.py --omic lipid --fraction gcp --eval MKM MSH")
    print("python gci_cc.py --omic both --fraction both --eval MKM MSH")
    print("python gci_cc.py --clusters 5 --mcs 0.1 --mcsi 0.02 --filename figure_p_gcm.png --omic protein --fraction gcm --eval MKM AFP MSH")
    print("python gci_cc.py --clusters 5 --mcs 0.05 --mcsi 0.03 --filename figure_l_both.png --omic lipids --fraction both --eval MKM AFP MSH")
    print("Clustering aglorithm names")
    print("MKM: Mini Batch K Means")
    print("AFP: Affinity Propagation")
    print("MSH: MeanShift")
    print("SPC: Spectral Clustering")
    print("WRD: Ward")
    print("AGC: Agglomerative Clustering")
    print("DBS: BSCAN")
    print("OPT: OPTICS")
    print("BRC: Birch")
    print("GMX: Gaussian Mixture")

def _isValidAlgorithmName(strAlgorithName):
    strAlgorithms = ["MKM", "AFP", "MSH", "SPC", "WRD","AGC", "DBS","OPT","BRC", "GMX"]
    return (strAlgorithName in strAlgorithms)    

def _showVersions():
    # check versions of Python runtime and ML libraries
    import sys
    print('Python: {}'.format(sys.version))

    import scipy
    print('scipy: {}'.format(scipy.__version__))

    import numpy
    print('numpy: {}'.format(numpy.__version__))

    import matplotlib
    print('matplotlib: {}'.format(matplotlib.__version__))

    import pandas
    print('pandas: {}'.format(pandas.__version__))

    import sklearn
    print('sklearn: {}'.format(sklearn.__version__))

if __name__=="__main__":
    _bDebug = 1
    main()