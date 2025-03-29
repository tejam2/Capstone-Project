# File: gci_viz.py
# Date: April 10, 2019
# Author: M. Samarah
# Input: --eval or one or more gene names or lipids
# Output: One of the following plots: 
# 		box-and-whisker
# 		line markers
# 		histogram
# 		scatter matrix
# Usage: 
#
# python gci_viz.py --help
# python gci_viz.py --version
# python gci_viz.py --eval
# python gci_viz --eval Thy1
# python gci_viz --eval Acat1 Lamp1 Yars
# python gci_viz --eval Thy1 Dync1h1
# python gci_viz --eval Thy1 Dync1h1 Map1b
# python gci_viz.py --eval AcCa So
# python gci_viz.py --eval Thy1 Dync1h1 Map1b AcCa So
# python gci_viz.py --eval Acat1 Lamp1 So PI PA PC PAF dMePE
# python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval
# python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval Acat1 Lamp1 So PI PA PC PAF dMePE
#  python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval Thy1 Dync1h1 Map1b AcCa Acat1 Acat2 Lamp1 So PI PA PAF dMePE

# Python version in runtime.txt is set to python-3.6.8
# App does not load on python-3.7.13

# Load system libraries
import os
import sys
import datetime
import random
from pathlib import Path
from random import randint

# Load ML libraries
import pandas
import numpy as np

# Load plotting and imaging libraries
from pandas.plotting import scatter_matrix
from matplotlib import pyplot
import matplotlib as mpl
from PIL import Image

strSig = "gci_viz: "
_debug = 1

# main program
def _main():
	if (_showingHelp() or len(sys.argv) < 2):
		_showHelp()
		exit(0)
		
	if (_showingVersions()):
		_showVersions()
		exit(0)
	
	# set default data folder and home path
	strGCHome="app/data"	
	strDataset=["protein", "lipids"]
	strFraction=["GCM_GeneNames_Normalized.csv", "GCP_GeneNames_Normalized.csv", "GCM_LipidClass_Normalized.csv", "GCP_LipidClass_Normalized.csv"]
	strDSLabel=["GCM_protein", "GCP_protein", "GCM_lipid", "GCP_lipid"]

	# load datasets
	df1 = readDataset(strGCHome, strDataset[0], strFraction[0])
	df2 = readDataset(strGCHome, strDataset[0], strFraction[1])
	df3 = readDataset(strGCHome, strDataset[1], strFraction[2])
	df4 = readDataset(strGCHome, strDataset[1], strFraction[3])
	df1.assign(DS=strDSLabel[0])
	df2.assign(DS=strDSLabel[1])
	df3.assign(DS=strDSLabel[2])
	df4.assign(DS=strDSLabel[3])
	df = pandas.concat([df1, df2, df3, df4])    

	# get command line arguments
	strFigureFileName = _getFigureFileName()
	strVizList = _getViz()
	strGeneLipidList = _getGeneLipidList(df)
	showGeneLipidList(strGeneLipidList)
	
	if (_showingEval()):
		if (len(strGeneLipidList) == 0):
			print(datetime.datetime.now(), "gci_viz: Analyze data");
			_sampleData(df)
			_visualizeData(df, pyplot, scatter_matrix, strVizList, strGeneLipidList, strFigureFileName)
		else:
			# verify gene and lipid names
			subsetdf = df.loc[df['Gene'].isin(strGeneLipidList)]
			if subsetdf.shape[0] >= 1 and subsetdf.shape[0] == len(strGeneLipidList)*2:
				print(datetime.datetime.now(), strSig, "Analyze data");
				_sampleData(subsetdf)
				_visualizeData(subsetdf, pyplot, scatter_matrix, strVizList, strGeneLipidList, strFigureFileName)
			else:
				print(strSig, "one or more of the genes/lipids specified are not valid: %s" %(', '.join(strGeneLipidList)))
	else:
		_showHelp()
		exit(0)

def readDataset(strGCHome, strDataset, strFraction):
	# construct full file path to data file
	strHome = str(Path.home())	
	strDatafilePath = os.path.join(strHome, strGCHome, strFraction)
	url = "file://" + strDatafilePath
	print(strSig, "data file = ", url)	
	
	if (strDataset.startswith("protein")):
		names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6","P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6","P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]
	else:
		names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5","E18_6", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6","P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6","P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]

	df = pandas.read_csv(url, names=names, header=None, skiprows=2)
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
	
# data sampling and visualization functions
def _sampleData(df):
	print("_sampleData-start: df.shape=", df.shape)

	# show shape, first and last 20 records, description of frame and its distribution
	print("Show shape, first and last 20 records, description of frame and its distribution")
	print(df.shape)
	print(df.head(20))
	print(df.tail(20))
	print(df.describe())

	# show label distribution
	print("Group by Gene")
	print(df.groupby('Gene').size())
	print("_sampleData-end: df.shape=", df.shape)

def _visualizeData(df, plt, scatter_matrix, strVizList, strGeneLipidList, figure_file_name):
	print("_visualizeData-start: df.shape=", df.shape)

	plt.figure(figsize=(24,16))
	plt.subplots_adjust(left=.25, right=.75, bottom=.25, top=.75, wspace=.05, hspace=.05)

	fileRandSeq = randint(100,999)
	imageListFiles = []

	if (len(strGeneLipidList) > 0):
		strTitle = str("Selection: %s" %(', '.join(strGeneLipidList)))
		if (len(strTitle) > 56):
			strTitle = strTitle[0:min(len(strTitle), 56)]
			lastCommaIndex = strTitle.rfind(",")
			strTitle = strTitle[:lastCommaIndex] + "..."
		else:
			strTitle = strTitle[0:min(len(strTitle), 56)]
			
	else:
		strTitle = "Selection: All"
	
	# draw box-and-whisker plot
	if ("BXW" in strVizList):
		df.plot.box()
		if (figure_file_name != ""):
			strFigureFile=figure_file_name+str("_%s" % ("BXW"))+".jpg"
		else:
			strFigureFile="gci_viz_figure_"+str("%d_%s" % (fileRandSeq,"BXW"))+".jpg"
		plt.title(strTitle)
		plt.savefig(strFigureFile)
		imageListFiles.append(strFigureFile)
	
	# draw line plot with markers
	if ("LMK" in strVizList):
		# create valid markers from mpl.markers
		valid_markers = mpl.markers.MarkerStyle.filled_markers # todo zzz
		markers = np.random.choice(valid_markers, df.shape[1], replace=False)

		ax = df.plot(kind='line')
		for i, line in enumerate(ax.get_lines()):
			line.set_marker(markers[i])
		ax.legend(ax.get_lines(), df.columns, loc='best')

		if (figure_file_name != ""):
			strFigureFile=figure_file_name+str("_%s" % ("LMK"))+".jpg"
		else:
			strFigureFile="gci_viz_figure_"+str("%d_%s" % (fileRandSeq,"LMK"))+".jpg"
		plt.title(strTitle)
		plt.savefig(strFigureFile)
		imageListFiles.append(strFigureFile)

	# show histogram
	if ("HST" in strVizList):
		df.hist()
		if (figure_file_name != ""):
			strFigureFile=figure_file_name+str("_%s" % ("HST"))+".jpg"
		else:
			strFigureFile="gci_viz_figure_"+str("%d_%s" % (fileRandSeq,"HST"))+".jpg"
		plt.suptitle(strTitle)
		plt.savefig(strFigureFile)
		imageListFiles.append(strFigureFile)
	
	# show scatter matrix
	if ("SCM" in strVizList):
		axes = scatter_matrix(df)
		[plt.setp(item.yaxis.get_majorticklabels(), 'size', 6) for item in axes.ravel()]
		[plt.setp(item.xaxis.get_majorticklabels(), 'size', 6) for item in axes.ravel()]
		[plt.setp(item.yaxis.get_label(), 'size', 6) for item in axes.ravel()]
		[plt.setp(item.xaxis.get_label(), 'size', 6) for item in axes.ravel()]
		labels = [round(float(i.get_text()), 2) for i in axes[0,0].get_yticklabels()]
		axes[0,0].set_yticklabels(labels)
		if (figure_file_name != ""):
			strFigureFile=figure_file_name+str("_%s" % ("SCM"))+".jpg"
		else:
			strFigureFile="gci_viz_figure_"+str("%d_%s" % (fileRandSeq,"SCM"))+".jpg"
		plt.suptitle(strTitle)	
		plt.savefig(strFigureFile)
		imageListFiles.append(strFigureFile)
	
	buildPDFFile(imageListFiles, figure_file_name, fileRandSeq)
	print("_visualizeData-end: df.shape=", df.shape)
				
def buildPDFFile(imageListFiles, figure_file_name, fileRandSeq):
	img = []
	i = 0
	while (i < len(imageListFiles)):
		img.append(Image.open(imageListFiles[i]))
		i += 1

	im_list = []
	if (len(img) >=2):	
		j = 1
		while (j < len(img)):
			im_list.append(img[j])
			j += 1
	
	if (figure_file_name == ""):
		strPDFFile="gci_viz_figure_"+str("%d" % (fileRandSeq))+".pdf"
	else:
		strPDFFile = figure_file_name+".pdf"
		
	if (len(im_list) >=1 and len(img) >= 1):
		img[0].save(strPDFFile, "PDF" ,resolution=100.0, save_all=True, append_images=im_list)
	elif (len(im_list) ==0 and len(img) == 1):
		img[0].save(strPDFFile, "PDF" ,resolution=100.0, save_all=True, append_images=im_list)

def _parseArgumets(arg):
	for i in range(1, len(sys.argv)):
		if (sys.argv[i] == arg):
			return 1

def _getFigureFileName():
	for i in range(1, len(sys.argv)):
		if (sys.argv[i] == "--filename"):
			if (len(sys.argv) > i):
				return(os.path.splitext(sys.argv[i+1])[0])
	return("")

def _isValidViz(strVizName):
	strViz = ["BXW", "LMK", "HST", "SCM"]
	return (strVizName in strViz)	

def _isValidGene(df, strGeneName):
	listGene = []
	listGene.append(strGeneName)
	subsetdf = df.loc[df['Gene'].isin(listGene)]
	return(subsetdf.shape[0] == 2)
	
def _getGeneLipidList(df):
	defGenesLipidsList = []
	for i in range(1, len(sys.argv)):
		if (sys.argv[i] == "--eval"):
			i += 1
			genesLipidsList = []
			j = i
			invalidCount = 0
			while j < len(sys.argv):
				if (_isValidGene(df, sys.argv[j])):
					genesLipidsList.append(sys.argv[j])
				else:
					print("_isValidGene: not valid ", sys.argv[j])
					invalidCount += 1 
				j +=1
			
			if (invalidCount > 0):
				print(strSig, "Invalid gene and/or lipid names specified");
				exit(0)		
			elif (len(genesLipidsList) >=1):
				return(genesLipidsList)
				
	return(defGenesLipidsList)

def _getViz():
	defViz = ["HST", "SCM"]
	for i in range(1, len(sys.argv)):
		if (sys.argv[i] == "--viz"):
			i += 1
			vizList = []
			j = i
			k = 0
			while j < len(sys.argv):
				if (_isValidViz(sys.argv[j])):
					vizList.append(sys.argv[j])
					k += 1
				j +=1
			
			if (len(vizList) >=1):
				return(vizList)
			else:
				print("gci_cc: At least one output name is required. Using default output: HST and SCM")
				
	return(defViz)

def _showingHelp():
	return(_parseArgumets("--help"))

def _showingVersions():
	return(_parseArgumets("--version"))

def _showingEval():
    return(_parseArgumets("--eval"))

def showGeneLipidList(strGeneLipidList):
	if (_debug):
		print("strGeneLipidList=", strGeneLipidList)

def _showHelp():
	print("gci_viz: syntax gci_viz --help --version [--filename output_filename.pdf] [--viz BXW LMK HST SCM] --eval [protein-name lipid-name ...]")
	print("--help: show this help message");
	print("--version: show version info for Python runtime and ML libraries");
	print("--eval: show samples and evaluation graphs");
	print("Examples:")
	print("python gci_viz.py --version")
	print("python gci_viz.py --eval")
	print("python gci_viz.py --help") 
	print("python gci_viz.py --eval Acat1 Lamp1 Yars")
	print("python gci_viz.py --eval Thy1")
	print("python gci_viz.py --eval Thy1 Dync1h1")
	print("python gci_viz.py --eval Thy1 Dync1h1 Map1b")
	print("python gci_viz.py --eval AcCa So")
	print("python gci_viz.py --eval Thy1 Dync1h1 Map1b AcCa So")
	print("python gci_viz.py --eval Acat1 Lamp1 So PI PA PAF dMePE")
	print("python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval")
	print("python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval Acat1 Lamp1 So PI PA PAF dMePE")
	print("python gci_viz.py --viz BXW LMK HST SCM --filename myAnalysis.pdf --eval Thy1 Dync1h1 Map1b AcCa Acat1 Acat2 Lamp1 So PI PA PAF dMePE")

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
	
_main()
