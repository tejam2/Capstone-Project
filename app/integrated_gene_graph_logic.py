import os
import pandas as pd
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from pandas.plotting import scatter_matrix
from fpdf import FPDF
import argparse


def get_dataset_path(omic, fraction):
    paths = {
        ("protein", "gcm"): os.path.join("app", "data", "GCM_GeneNames_Normalized.csv"),
        ("protein", "gcp"): os.path.join("app", "data", "GCP_GeneNames_Normalized.csv"),
        ("lipid", "gcm"): os.path.join("app", "data", "GCM_LipidClass_Normalized.csv"),
        ("lipid", "gcp"): os.path.join("app", "data", "GCP_LipidClass_Normalized.csv")
    }
    return paths.get((omic, fraction))


def load_data(path, omic):
    if omic == "protein":
        columns = ["Gene"] + [f"E18_{i}" for i in range(1, 6)] + \
                  [f"P0_{i}" for i in range(1, 7)] + \
                  [f"P3_{i}" for i in range(1, 7)] + \
                  [f"P6_{i}" for i in range(1, 7)] + \
                  [f"P9_{i}" for i in range(1, 7)]
    else:
        columns = ["Gene"] + [f"E18_{i}" for i in range(1, 7)] + \
                  [f"P0_{i}" for i in range(1, 7)] + \
                  [f"P3_{i}" for i in range(1, 7)] + \
                  [f"P6_{i}" for i in range(1, 7)] + \
                  [f"P9_{i}" for i in range(1, 7)]

    return pd.read_csv(path, names=columns, header=None, skiprows=2)


def plot_graphs(df, genes, stage, output_prefix, graph_types):
    stage_cols = [col for col in df.columns if col.startswith(stage)]
    df_filtered = df[df["Gene"].isin(genes)]
    if df_filtered.empty:
            raise ValueError("❌ No matching gene names found. Please check your selection.")
    images = []
    colors = plt.get_cmap("tab10")
    os.makedirs(os.path.dirname(output_prefix), exist_ok=True)

    if "bar" in graph_types:
        fig, ax = plt.subplots(figsize=(10, 6))
        x_labels, values, bar_colors = [], [], []
        for idx, (_, row) in enumerate(df_filtered.iterrows()):
            for i, col in enumerate(stage_cols):
                x_labels.append(f"{row['Gene']}_R{i+1}")
                values.append(row[col])
                bar_colors.append(colors(idx))
        ax.bar(x_labels, values, color=bar_colors)
        ax.set_title(f"Bar Plot for Stage: {stage}")
        ax.set_xlabel("Gene Replicates")
        ax.set_ylabel("Expression Level")
        plt.xticks(rotation=45, ha='right')
        plt.tight_layout()
        path = f"{output_prefix}_bar.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    if "line" in graph_types:
        df_line = df_filtered[["Gene"] + stage_cols].set_index("Gene").T
        ax = df_line.plot(marker='o', figsize=(10, 6))
        ax.set_title(f"Line Plot - {stage}")
        plt.xlabel("Replicates")
        plt.ylabel("Expression")
        plt.tight_layout()
        path = f"{output_prefix}_line.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    if "box" in graph_types:
        df_filtered[stage_cols].T.boxplot(figsize=(10, 6))
        plt.title(f"Box-and-Whisker - {stage}")
        plt.tight_layout()
        path = f"{output_prefix}_box.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    if "hist" in graph_types:
        df_filtered[stage_cols].T.hist(figsize=(10, 6), bins=10)
        plt.suptitle(f"Histogram - {stage}")
        plt.tight_layout()
        path = f"{output_prefix}_hist.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    if "scatter" in graph_types:
        scatter_matrix(df_filtered[stage_cols], figsize=(8, 8), diagonal='hist')
        plt.suptitle(f"Scatter Matrix - {stage}")
        plt.tight_layout()
        path = f"{output_prefix}_scatter.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    if "3d" in graph_types:
        fig = plt.figure(figsize=(10, 8))
        ax = fig.add_subplot(111, projection='3d')
        xpos, ypos, zpos, dx, dy, dz, color_list = [], [], [], [], [], [], []
        for gidx, (_, row) in enumerate(df_filtered.iterrows()):
            for ridx, col in enumerate(stage_cols):
                xpos.append(gidx)
                ypos.append(ridx)
                zpos.append(0)
                dx.append(0.5)
                dy.append(0.5)
                dz.append(row[col])
                color_list.append(colors(gidx))
        ax.bar3d(xpos, ypos, zpos, dx, dy, dz, color=color_list)
        ax.set_xticks(range(len(df_filtered)))
        ax.set_xticklabels(df_filtered["Gene"].values)
        ax.set_yticks(range(len(stage_cols)))
        ax.set_yticklabels([f"R{i+1}" for i in range(len(stage_cols))])
        ax.set_zlabel("Expression")
        ax.set_title(f"3D Bar Plot - {stage}")
        plt.tight_layout()
        path = f"{output_prefix}_3d_bar.jpg"
        plt.savefig(path)
        images.append(path)
        plt.close()

    return df_filtered, stage_cols, images


from datetime import datetime

def create_report(df_filtered, stage_cols, inputs, image_files, output_pdf):
    pdf = FPDF()
    pdf.set_auto_page_break(auto=True, margin=15)
    pdf.add_page()
    pdf.set_font("Arial", size=12)
    now = datetime.now().strftime("%a %Y-%m-%d %I:%M:%S %p")
    pdf.cell(0, 10, f"Neuronal Growth Cone Multi-Omics Insight (GC-Insights) {now}", ln=True)
    pdf.set_font("Arial", size=10)
    pdf.cell(0, 10, "This report is generated by GC-Insights: https://gcinsights.herokuapp.com", ln=True)

    pdf.ln(5)
    pdf.cell(0, 10, "Inputs:", ln=True)
    for key, value in inputs.items():
        pdf.cell(0, 8, f"{key}: {value}", ln=True)

    # Data Table
    pdf.ln(4)
    pdf.set_font("Arial", "B", 10)
    pdf.cell(0, 10, "Data:", ln=True)

    pdf.set_font("Arial", size=9)
    col_width = pdf.w / (len(stage_cols) + 1.5)
    pdf.set_fill_color(200, 220, 255)
    pdf.cell(col_width, 8, "Gene", border=1, fill=True, align="C")
    for col in stage_cols:
        pdf.cell(col_width, 8, col, border=1, fill=True, align="C")
    pdf.ln()

    for _, row in df_filtered.iterrows():
        pdf.cell(col_width, 8, row["Gene"], border=1)
        for col in stage_cols:
            pdf.cell(col_width, 8, f"{row[col]:.4f}", border=1)
        pdf.ln()

    for img_path in image_files:
        pdf.add_page()
        pdf.image(img_path, x=10, w=180)

    pdf.add_page()
    pdf.set_font("Arial", size=10)
    pdf.multi_cell(0, 8, """Credits:
This tool was created in support of the Neuronal Growth Cone Multi-Omics study and in collaboration with Florida Polytechnic University and University of Miami by Rabeet Fatmi, Florida Polytechnic alumnus, and Dr. Mohammad Samarah, assistant professor of computer science at Florida Polytechnic University. Contributors include Zain Chauhan, Rabeet Fatmi, Dr. Sanjoy Bhattacharya, and Dr. Mohammad Samarah. Dr. Samarah led requirements engineering, design and development of the tool. Dr. Bhattacharya provided helpful insights and direction. Zain was instrumental in requirements and user interface design.

Copyright © 2019 M. Samarah, Florida Polytechnic University/S. Bhattacharya, University of Miami. All rights reserved.""")
    pdf.output(output_pdf)




def run_gene_graph_pipeline(omic, fraction, genes, stage, graph_types, output_prefix):
    dataset_path = get_dataset_path(omic, fraction)
    df = load_data(dataset_path, omic)
    df_filtered, stage_cols, image_files = plot_graphs(df, genes, stage, output_prefix, graph_types)
    inputs = {
        "Omics Layer": omic,
        "Fraction": fraction,
        "Gene Names": ", ".join(genes),
        "Developmental Stage": stage,
        "Graph Types": ", ".join(graph_types)
    }
    create_report(df_filtered, stage_cols, inputs, image_files, f"{output_prefix}_report.pdf")
    return image_files, f"{output_prefix}_report.pdf"

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate gene graphs and reports.")
    parser.add_argument("--omic", required=True, choices=["protein", "lipid"])
    parser.add_argument("--fraction", required=True, choices=["gcm", "gcp"])
    parser.add_argument("--genes", required=True, nargs="+", help="List of gene names")
    parser.add_argument("--stage", required=True, choices=["E18", "P0", "P3", "P6", "P9"])
    parser.add_argument("--graph_types", nargs="+", required=True,
                    choices=["bar", "line", "box", "hist", "scatter", "3d"],
                    help="Graph types to generate")
    parser.add_argument("--output_prefix", required=True,
                    help="Prefix for output files (no extension)")

    args = parser.parse_args()

    
    # Run the function with the parsed arguments
    run_gene_graph_pipeline(
        omic=args.omic,
        fraction=args.fraction,
        genes=args.genes,
        stage=args.stage,
        graph_types=args.graph_types,
        output_prefix=args.output_prefix
    )

