# deepml_util.py

import pandas as pd
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import os
import umap.umap_ as umap
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score, confusion_matrix, ConfusionMatrixDisplay
from uuid import uuid4
import warnings

warnings.filterwarnings('ignore')

def run_deepml_pipeline(file_path, model_choice):
    df = pd.read_csv(file_path, skiprows=2)
    df = df.set_index(df.columns[0])
    X = df.values.T
    feature_names = df.index.tolist()

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    labels = []
    for col in df.columns:
        parts = col.split('_')
        if len(parts) >= 2:
            labels.append(parts[1])
        else:
            labels.append('Unknown')
    le = LabelEncoder()
    y = le.fit_transform(labels)

    X_train, X_test, y_train, y_test = train_test_split(X_scaled, y, test_size=0.2, random_state=42)

    results_folder = os.path.join("app", "static", "results")
    os.makedirs(results_folder, exist_ok=True)

    # PCA
    fig, ax = plt.subplots()
    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X_scaled)
    scatter = ax.scatter(X_pca[:,0], X_pca[:,1], c=y, cmap='tab10')
    plt.colorbar(scatter)
    plt.title("PCA Projection")
    pca_path = os.path.join(results_folder, f"pca_{uuid4().hex}.png")
    plt.savefig(pca_path)
    plt.close()

    # t-SNE
    fig, ax = plt.subplots()
    tsne = TSNE(n_components=2, perplexity=5, random_state=42)
    X_tsne = tsne.fit_transform(X_scaled)
    scatter = ax.scatter(X_tsne[:,0], X_tsne[:,1], c=y, cmap='tab10')
    plt.colorbar(scatter)
    plt.title("t-SNE Projection")
    tsne_path = os.path.join(results_folder, f"tsne_{uuid4().hex}.png")
    plt.savefig(tsne_path)
    plt.close()

    # UMAP
    fig, ax = plt.subplots()
    reducer = umap.UMAP(random_state=42)
    X_umap = reducer.fit_transform(X_scaled)
    scatter = ax.scatter(X_umap[:,0], X_umap[:,1], c=y, cmap='tab10')
    plt.colorbar(scatter)
    plt.title("UMAP Projection")
    umap_path = os.path.join(results_folder, f"umap_{uuid4().hex}.png")
    plt.savefig(umap_path)
    plt.close()

    accuracy_lr = None
    accuracy_rf = None
    top_features = None
    confusion_path = None

    # Model Training
    if len(np.unique(y_train)) >= 2:
        if model_choice in ["1", "3"]:
            model_lr = LogisticRegression(max_iter=500)
            model_lr.fit(X_train, y_train)
            y_pred_lr = model_lr.predict(X_test)
            accuracy_lr = round(accuracy_score(y_test, y_pred_lr) * 100, 2)

        if model_choice in ["2", "3"]:
            model_rf = RandomForestClassifier(n_estimators=100, random_state=42)
            model_rf.fit(X_train, y_train)
            y_pred_rf = model_rf.predict(X_test)
            accuracy_rf = round(accuracy_score(y_test, y_pred_rf) * 100, 2)

            feature_importance = model_rf.feature_importances_
            idx = np.argsort(feature_importance)[-10:]
            top_features = [(feature_names[i], round(feature_importance[i], 4)) for i in idx]

            fig, ax = plt.subplots(figsize=(8,8))
            cm = confusion_matrix(y_test, y_pred_rf)
            disp = ConfusionMatrixDisplay(confusion_matrix=cm)
            disp.plot(ax=ax, cmap='Blues')
            confusion_path = os.path.join(results_folder, f"confusion_{uuid4().hex}.png")
            plt.savefig(confusion_path)
            plt.close()
    else:
        print("\n❌ Not enough classes to train a classifier. Only one class found.")

    # Now safe replace
    pca_path = pca_path.replace("app\\static\\", "/static/").replace("app/static/", "/static/")
    tsne_path = tsne_path.replace("app\\static\\", "/static/").replace("app/static/", "/static/")
    umap_path = umap_path.replace("app\\static\\", "/static/").replace("app/static/", "/static/")
    if confusion_path:
        confusion_path = confusion_path.replace("app\\static\\", "/static/").replace("app/static/", "/static/")

    return {
        "pca_path": pca_path,
        "tsne_path": tsne_path,
        "umap_path": umap_path,
        "accuracy_lr": accuracy_lr,
        "accuracy_rf": accuracy_rf,
        "top_features": top_features,
        "confusion_path": confusion_path
    }
