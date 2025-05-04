import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from fpdf import FPDF


def preprocess_dataset(file_path, label, is_protein=True):
    if is_protein:
        names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6",
                 "P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6",
                 "P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]
    else:
        names = ["Gene","E18_1","E18_2","E18_3","E18_4","E18_5","E18_6", "P0_1","P0_2","P0_3","P0_4","P0_5","P0_6",
                 "P3_1","P3_2","P3_3","P3_4","P3_5","P3_6","P6_1","P6_2","P6_3","P6_4","P6_5","P6_6",
                 "P9_1","P9_2","P9_3","P9_4","P9_5","P9_6"]

    df = pd.read_csv(file_path, names=names, skiprows=2)
    df['E18'] = df.loc[:, "E18_1":"E18_6" if not is_protein else "E18_5"].mean(axis=1)
    df['P0'] = df.loc[:, "P0_1":"P0_6"].mean(axis=1)
    df['P3'] = df.loc[:, "P3_1":"P3_6"].mean(axis=1)
    df['P6'] = df.loc[:, "P6_1":"P6_6"].mean(axis=1)
    df['P9'] = df.loc[:, "P9_1":"P9_6"].mean(axis=1)
    df = df[["Gene", "E18", "P0", "P3", "P6", "P9"]].dropna()
    df["Label"] = label
    return df


def run_combined_deepml_analysis(file_path1, file_path2):
    def label_name(file):
        if "GCM_GeneNames" in file:
            return "GCM Protein"
        elif "GCP_GeneNames" in file:
            return "GCP Protein"
        elif "GCM_LipidClass" in file:
            return "GCM Lipid"
        elif "GCP_LipidClass" in file:
            return "GCP Lipid"
        else:
            return "Dataset"

    label1 = label_name(file_path1)
    label2 = label_name(file_path2)

    df1 = preprocess_dataset(file_path1, label=label1, is_protein="Protein" in label1)
    df2 = preprocess_dataset(file_path2, label=label2, is_protein="Protein" in label2)
    df_combined = pd.concat([df1, df2], ignore_index=True)

    X = df_combined.drop(columns=["Gene", "Label"])
    y = df_combined["Label"]

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    pca_proj = PCA(n_components=2).fit_transform(X_scaled)
    tsne_proj = TSNE(n_components=2, perplexity=30, random_state=42, max_iter=500).fit_transform(X_scaled)

    output_dir = "app/static/results"
    os.makedirs(output_dir, exist_ok=True)

    pca_path = os.path.join(output_dir, "pca_plot.png")
    tsne_path = os.path.join(output_dir, "tsne_plot.png")

    plt.figure()
    sns.scatterplot(x=pca_proj[:, 0], y=pca_proj[:, 1], hue=y, palette="Set2")
    plt.title(f"PCA: {label1} vs {label2}")
    plt.savefig(pca_path)
    plt.close()

    plt.figure()
    sns.scatterplot(x=tsne_proj[:, 0], y=tsne_proj[:, 1], hue=y, palette="Set2")
    plt.title(f"t-SNE: {label1} vs {label2}")
    plt.savefig(tsne_path)
    plt.close()

    X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.3, random_state=42)

    lr = LogisticRegression(max_iter=500)
    lr.fit(X_train, y_train)
    acc_lr = accuracy_score(y_test, lr.predict(X_test))

    rf = RandomForestClassifier(n_estimators=100, random_state=42)
    rf.fit(X_train, y_train)
    acc_rf = accuracy_score(y_test, rf.predict(X_test))

    importance = rf.feature_importances_
    top_features = sorted(zip(X.columns, importance), key=lambda x: x[1], reverse=True)[:5]

    from sklearn.metrics import classification_report, confusion_matrix

    # Random Forest predictions
    y_pred = rf.predict(X_test)
    cm = confusion_matrix(y_test, y_pred)
    report_text = classification_report(y_test, y_pred, target_names=[label1, label2])

    # Save heatmap
    cm_path = os.path.join(output_dir, "confusion_matrix.png")
    plt.figure(figsize=(6, 4))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=[label1, label2],     yticklabels=[label1, label2])
    plt.title("Confusion Matrix")
    plt.xlabel("Predicted")
    plt.ylabel("Actual")
    plt.tight_layout()
    plt.savefig(cm_path)
    plt.close()


    pdf_path = os.path.join(output_dir, "deepml_report.pdf")
    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=12)
    pdf.cell(200, 10, txt="Deep ML Report", ln=True, align='C')
    pdf.ln(10)
    pdf.cell(200, 10, txt=f"Logistic Regression Accuracy: {round(acc_lr * 100, 2)}%", ln=True)
    pdf.cell(200, 10, txt=f"Random Forest Accuracy: {round(acc_rf * 100, 2)}%", ln=True)
    pdf.ln(5)
    pdf.cell(200, 10, txt="Top 5 Features (Random Forest):", ln=True)
    for feat, val in top_features:
        pdf.cell(200, 10, txt=f"{feat}: {round(val, 4)}", ln=True)

    pdf.image(pca_path, x=10, y=pdf.get_y() + 10, w=90)
    pdf.ln(70)
    pdf.image(tsne_path, x=10, y=pdf.get_y() + 10, w=90)
    pdf.add_page()
    pdf.cell(200, 10, txt="Classification Report (Random Forest):", ln=True)
    for line in report_text.splitlines():
        pdf.cell(200, 10, txt=line.strip(), ln=True)

    pdf.image(cm_path, x=10, y=pdf.get_y() + 10, w=100)


    pdf.output(pdf_path)

    return {
        "accuracy_lr": round(acc_lr * 100, 2),
        "accuracy_rf": round(acc_rf * 100, 2),
        "top_features": top_features,
        "pca_path": "/static/results/pca_plot.png",
        "tsne_path": "/static/results/tsne_plot.png",
        "cm_path": "/static/results/confusion_matrix.png",
        "classification_report": report_text,
        "pdf_path": "/static/results/deepml_report.pdf",
        "labels": [label1, label2]
    }
