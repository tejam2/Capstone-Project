import pandas as pd
import cloudinary
import cloudinary.uploader
import os
import traceback  
import matplotlib.pyplot as plt
from fpdf import FPDF
from datetime import datetime
import time
import uuid
from io import BytesIO
import subprocess
from flask import Flask, render_template, redirect, url_for, session, send_file, request, flash
from flask_sqlalchemy import SQLAlchemy
from itsdangerous import URLSafeTimedSerializer
from flask_login import LoginManager, login_user, logout_user, login_required, UserMixin, current_user
from flask_mail import Mail, Message
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.utils import secure_filename
from app.integrated_gene_graph_logic import run_gene_graph_pipeline
from app.deepml_util import run_combined_deepml_analysis
from PIL import Image, ImageOps, ImageDraw  # ADD THIS import
import numpy as np
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
import umap
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, ConfusionMatrixDisplay
from sqlalchemy import Text
import json
from flask import jsonify
import re



# App setup
app = Flask(__name__)
app.secret_key = "your_secret_key"
app.config['SECRET_KEY'] = app.secret_key

os.makedirs(os.path.join(app.root_path, 'static', 'results'), exist_ok=True)

# Email config
app.config['MAIL_SERVER'] = 'smtp.gmail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USERNAME'] = 'GCinsights.reset@gmail.com'
app.config['MAIL_PASSWORD'] = 'MAIL_PASSWORD'
app.config['MAIL_DEFAULT_SENDER'] = ('GCInsights Support', 'gcinsights.support@gmail.com')

# Database
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Cloudinary Config
cloudinary.config(
    cloud_name="cloud_name",
    api_key="api_key",
    api_secret="api_secret",
    secure=True
)

def upload_to_cloudinary(filepath, is_raw=False):
    filename = os.path.basename(filepath)
    public_id = f"gcinsights/results/{filename.rsplit('.', 1)[0]}"

    upload_result = cloudinary.uploader.upload(
        filepath,
        public_id=public_id,
        resource_type="raw" if is_raw else "image",
        overwrite=True
    )

    # Force download link
    base_url = upload_result['secure_url']
    download_url = base_url.replace('/upload/', '/upload/fl_attachment/')
    return download_url

def is_strong_password(pwd):
    return re.match(r"^(?=.*\d)(?=.*[a-zA-Z]).{8,}$", pwd)

# Allow uploads to this folder
UPLOAD_FOLDER = os.path.join("app", "static", "profile_photos")
ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg", "gif"}

app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
# Preload datasets
datasets = {
    "GCM_Proteins": "app/static/data/GCM_GeneNames_Normalized.csv",
    "GCP_Proteins": "app/static/data/GCP_GeneNames_Normalized.csv",
    "GCM_Lipids": "app/static/data/GCM_LipidClass_Normalized.csv",
    "GCP_Lipids": "app/static/data/GCP_LipidClass_Normalized.csv"
}

dataframes = {key: pd.read_csv(path, skiprows=2) for key, path in datasets.items()}

# Extensions
db = SQLAlchemy(app)

mail = Mail(app)
login_manager = LoginManager(app)
login_manager.login_view = "login"
serializer = URLSafeTimedSerializer(app.config['SECRET_KEY'])

class User(db.Model, UserMixin):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(150), nullable=False, unique=True)
    email = db.Column(db.String(150), unique=True)
    password = db.Column(db.String(150))
    role = db.Column(db.String(50))
    state = db.Column(db.String(100))
    city = db.Column(db.String(100))
    country = db.Column(db.String(100))
    profile_photo = db.Column(db.String(255), nullable=True, default='default.png')

    def set_password(self, password):
        self.password = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password, password)

class DownloadHistory(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    file_name = db.Column(db.String(255))
    file_url = db.Column(db.String(500))
    download_type = db.Column(db.String(100))  # e.g., 'Interactive Query', 'Deep ML'
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    parameters = db.Column(Text)

    user = db.relationship('User', backref='downloads')



@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))

@app.route("/")
def start():
    return render_template("GCinsights.html")

@app.route("/visualizations")
@login_required
def visualizations():
    return render_template("visualizations.html")

@app.route('/advanced-analysis', methods=['GET', 'POST'])
@login_required
def advanced_analysis():

    prefill = {}
    if 'rerun_params' in session:
        prefill = session.pop('rerun_params', {})

    if request.method == 'POST':
        try:
            # Gather form inputs
            omics_type = request.form.get('omics_type', '').lower()
            fraction = request.form.get('fraction', '').lower()
            clusters = request.form.get('clusters', '3')
            min_cluster_size = request.form.get('min_cluster_size', '0.1')
            mcsi = request.form.get('min_cluster_size_increment', '0.02')
            mcsic = request.form.get('min_cluster_iterations', '2')
            algorithms = request.form.getlist('algorithms')

            params = {"type": "advanced", "omics_type": omics_type, "fraction": fraction, "clusters": clusters, "min_cluster_size": min_cluster_size, "mcsi": mcsi, "mcsic": mcsic, "algorithms": algorithms}


            eval_str = ' '.join(algorithms)

            # Generate unique filename
            unique_id = uuid.uuid4().hex[:6]
            figure_filename = f"figure_{omics_type}_{fraction}_{unique_id}.png"
            figure_path = os.path.join(app.root_path, "static", "results", figure_filename)
            os.makedirs(os.path.dirname(figure_path), exist_ok=True)

            # Build the command to run gci_cc.py
            command = [
                "python", "app/gci_cc.py",
                "--clusters", clusters,
                "--mcs", min_cluster_size,
                "--mcsi", mcsi,
                "--mcsic", mcsic,
                "--omic", omics_type,
                "--fraction", fraction,
                "--filename", figure_path,
                "--eval"
            ] + algorithms

            # Run the clustering script
            subprocess.run(command, check=True)

            # Create PDF report
            pdf_filename = figure_filename.replace('.png', '.pdf')
            pdf_path = os.path.join(app.root_path, "static", "results", pdf_filename)

            pdf = FPDF()
            pdf.add_page()
            pdf.set_font("Arial", size=12)
            pdf.cell(200, 10, txt="GCInsights Clustering Report", ln=True, align='C')
            pdf.ln(10)
            pdf.set_font("Arial", size=10)
            pdf.multi_cell(0, 10, txt=f"Input Parameters:\nOmics: {omics_type}\nFraction: {fraction}\nClusters: {clusters}\nMin Cluster Size: {min_cluster_size}\nCluster Size Increment: {mcsi}\nIterations: {mcsic}\nAlgorithms: {eval_str}")
            pdf.image(figure_path, x=10, w=180)
            pdf.output(pdf_path)
            print(f"PDF saved at: {pdf_path}")
            # Upload to Cloudinary
            img_url = upload_to_cloudinary(figure_path, is_raw=False)
            pdf_url = upload_to_cloudinary(pdf_path, is_raw=True)

            db.session.add(DownloadHistory(user_id=current_user.id, file_name=figure_filename, file_url=img_url, download_type="Advanced Analysis", parameters=json.dumps(params)))
            db.session.add(DownloadHistory(user_id=current_user.id, file_name=pdf_filename, file_url=pdf_url, download_type="Advanced Analysis", parameters=json.dumps(params)))
            db.session.commit()

            return render_template("advanced_analysis.html", img_url=img_url, pdf_url=pdf_url, img_path=os.path.join("results", figure_filename), pdf_path=os.path.join("results", pdf_filename))


        except subprocess.CalledProcessError as e:
            flash(f"Failed to run analysis script: {e}", "danger")
        except Exception as e:
            traceback.print_exc()
            flash(f"Unexpected error: {str(e)}", "danger")

    
    return render_template("advanced_analysis.html", **prefill)


@app.route("/interactive-query", methods=["GET", "POST"])
def interactive_query():

    prefill = {}
    if 'rerun_params' in session:
         prefill = session.pop('rerun_params', {})

    if request.method == "POST":
        omic = request.form.get("omic")
        fraction = request.form.get("fraction")
        stage = request.form.get("stage")
        genes = request.form.getlist("genes[]")
        graph_types_selected = request.form.getlist("graph_types")

        if not all([omic, fraction, stage, genes, graph_types_selected]):
            return "Missing required inputs", 400


        output_prefix = os.path.join("app", "static", "results", f"{'_'.join(genes)}_{stage}")

        # Ensure output folder exists
        os.makedirs(os.path.dirname(output_prefix), exist_ok=True)

        try:
            # Generate graphs and PDF
            image_files, pdf_path = run_gene_graph_pipeline(
                omic=omic,
                fraction=fraction,
                genes=genes,
                stage=stage,
                graph_types=graph_types_selected,
                output_prefix=output_prefix
            )

            # Convert to relative static paths for HTML
            image_paths = [img.replace("app/static/", "/static/") for img in image_files]
            pdf_path_rel = pdf_path.replace("app/static/", "/static/")

            for img in image_files:
                url = upload_to_cloudinary(img)
                db.session.add(DownloadHistory(user_id=current_user.id, file_name=os.path.basename(img), file_url=url, download_type="Interactive Query", parameters=json.dumps({"type": "interactive", "omic": omic, "fraction": fraction, "stage": stage, "genes": genes, "graph_types": graph_types_selected})))

            pdf_url = upload_to_cloudinary(pdf_path, is_raw=True)
            db.session.add(DownloadHistory(user_id=current_user.id, file_name=os.path.basename(pdf_path), file_url=pdf_url, download_type="Interactive Query", parameters=json.dumps({"type": "interactive", "omic": omic, "fraction": fraction, "stage": stage, "genes": genes, "graph_types": graph_types_selected})))
            db.session.commit()
            return render_template(
                "interactive_query.html",
                image_paths=image_paths,
                pdf_path=pdf_path_rel
            )

        except ValueError as ve:
            flash(str(ve), 'danger')
            return redirect(url_for('interactive_query'))
        except Exception as e:
            flash("An unexpected error occurred. Please try again.", 'danger')
            return redirect(url_for('interactive_query'))
 
    return render_template("interactive_query.html", **prefill)

@app.route('/get_gene_names', methods=['POST'])
def get_gene_names():
    data = request.get_json()
    omics_layer = data.get('omics_layer')
    fraction = data.get('fraction')

    if omics_layer and fraction:
        filename = None
        if omics_layer == "Protein" and fraction == "GCM":
            filename = "a.csv"
        elif omics_layer == "Lipid" and fraction == "GCM":
            filename = "b.csv"
        elif omics_layer == "Protein" and fraction == "GCP":
            filename = "c.csv"
        elif omics_layer == "Lipid" and fraction == "GCP":
            filename = "d.csv"

        if filename:
            try:
                filepath = os.path.join(app.root_path, "static", "data", filename)
  # updated to match your path
                with open(filepath, "r", encoding="utf-8-sig") as f:
                    genes = [line.strip() for line in f.readlines() if line.strip()]
                return jsonify({"genes": genes})
            except Exception as e:
                return jsonify({"error": str(e)})
    return jsonify({"genes": []})



@app.route("/history")
@login_required
def download_history():
    history = DownloadHistory.query.filter_by(user_id=current_user.id).order_by(DownloadHistory.timestamp.desc()).all()
    return render_template("history.html", history=history)


@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        identifier = request.form.get("email")
        password = request.form.get("password")
        user = User.query.filter((User.username == identifier) | (User.email == identifier)).first()
        if user and user.check_password(password):
            login_user(user)
            flash("Login successful!", "success")
            return redirect(url_for("home"))
        else:
            flash("Incorrect username/email or password", "error")
    return render_template("login.html")


from email_validator import validate_email, EmailNotValidError

@app.route("/signup", methods=["GET", "POST"])
def signup():
    if request.method == "POST":
        username = request.form.get("username")
        email = request.form.get("email")
        try:
            valid = validate_email(email)
            email = valid.email  # cleaned email
        except EmailNotValidError as e:
            flash("Invalid email address: " + str(e), "danger")
            return redirect(url_for("signup"))
        password = request.form.get("password")
        confirm_password = request.form.get("confirm_password")
        role = request.form.get("role")
        state = request.form.get("state")
        city = request.form.get("city")
        country = request.form.get("country")
        if not is_strong_password(password):
            flash("Password must be at least 8 characters long and include at least one number and one letter.", "danger")
            return redirect(url_for('signup'))
        if password != confirm_password:
            flash("Passwords do not match", "warning")
            return redirect(url_for("signup"))

        if User.query.filter_by(email=email).first() or User.query.filter_by(username=username).first():
            flash("Email or Username already exists.", "danger")
            return redirect(url_for("signup"))

        new_user = User(
            username=username,
            email=email,
            role=role,
            state=state,
            city=city,
            country=country
        )
        new_user.set_password(password)
        db.session.add(new_user)
        db.session.commit()
        flash("Signup successful. Please log in.", "success")
        return redirect(url_for("login"))

    return render_template("signup.html")

@app.route("/home")
@login_required
def home():
    return render_template("home.html", user=current_user)

@app.route("/logout")
def logout():
    logout_user()
    session.clear()
    return redirect(url_for("start"))

@app.route('/forgot-password', methods=['GET', 'POST'])
def forgot_password():
    if request.method == 'POST':
        email = request.form['email']
        user = db.session.query(User).filter_by(email=email).first()
        if user:
            token = serializer.dumps(email, salt='password-reset-salt')
            reset_url = url_for('reset_password', token=token, _external=True)

            subject = "GCInsights Password Reset Request"
            body = f"""\
Hi {user.username},

You requested to reset your password. Click the link below to proceed:

{reset_url}

If you didn't request this, you can safely ignore this email.

Thanks,  
GCInsights Support Team
"""
            try:
                msg = Message(subject, sender=("GCInsights Support", "gcinsights.support@gmail.com"), recipients=[email], body=body)
                mail.send(msg)
                flash('Check your email for a reset link. If you can\'t find the link in your mail check in the spam mails.', 'info')
            except Exception as e:
                flash('Could not send email. Check configuration.', 'danger')
        else:
            flash('Email not found.', 'warning')
    return render_template('forgot_password.html')


@app.route('/reset-password/<token>', methods=['GET', 'POST'])
def reset_password(token):
    try:
        email = serializer.loads(token, salt='password-reset-salt', max_age=3600)
    except Exception as e:
        flash("The reset link is invalid or has expired.", "error")
        return redirect(url_for('login'))

    if request.method == "POST":
        new_password = request.form.get("password")
        confirm_password = request.form.get("confirm_password")

        if new_password != confirm_password:
            flash("Passwords do not match.", "error")
            return render_template("reset_password.html")


        if not is_strong_password(new_password):
            flash("Password must be at least 8 characters long and include at least one number and one letter.", "danger")
            return redirect(request.url)

        user = db.session.query(User).filter_by(email=email).first()
        if user:
            user.set_password(new_password)
            db.session.commit()
            flash("Your password has been updated successfully.", "success")
            return redirect(url_for('login'))
        else:
            flash("User not found.", "error")

    return render_template("reset_password.html")

@app.route("/profile")
@login_required
def profile():
    print("Profile Photo Path: ", current_user.profile_photo)
    return render_template("profile.html", user=current_user)

@app.route("/edit-profile", methods=["GET", "POST"])
@login_required
def edit_profile():
    if request.method == "POST":
        file = request.files.get("profile_photo")
        if file:
            filename = secure_filename(file.filename)
            ext = filename.rsplit('.', 1)[1].lower()

            # Validate file type
            if ext not in {'png', 'jpg', 'jpeg'}:
                flash('Invalid file type. Only PNG, JPG, JPEG allowed.', 'danger')
                return redirect(url_for('edit_profile'))

            # Create unique filename
            unique_filename = f"user_{current_user.id}_{int(time.time())}.png"  # Save always as PNG

            # Create folder if not exists
            upload_folder = os.path.join("app", "static", "profile_photos")
            os.makedirs(upload_folder, exist_ok=True)

            filepath = os.path.join(upload_folder, unique_filename)

            # Save, resize, crop to circle
            img = Image.open(file.stream)
            img = ImageOps.fit(img, (400, 400), Image.Resampling.LANCZOS)

            mask = Image.new('L', (400, 400), 0)
            draw = ImageDraw.Draw(mask)
            draw.ellipse((0, 0, 400, 400), fill=255)
            img.putalpha(mask)

            img.save(filepath, format='PNG')

            # UPDATE user profile
            current_user.profile_photo = unique_filename
            db.session.commit()

            flash("Profile photo updated successfully!", "success")
            return redirect(url_for('profile'))

        else:
            flash("No file selected.", "danger")
            return redirect(url_for('edit_profile'))

    return render_template("edit_profile.html")



@app.route('/change-password', methods=['GET', 'POST'])
@login_required
def change_password():
    if request.method == "POST":
        current_password = request.form.get("current_password")
        new_password = request.form.get("password")
        confirm_password = request.form.get("confirm_password")

        if not current_user.check_password(current_password):
            flash("Current password is incorrect.", "error")
            return redirect(url_for('change_password'))

        if new_password != confirm_password:
            flash("New passwords do not match.", "error")
            return redirect(url_for('change_password'))

        if not is_strong_password(new_password):
            flash("Password must be at least 8 characters long and include at least one number and one letter.", "danger")
            return redirect(url_for('change_password'))

        current_user.set_password(new_password)
        db.session.commit()
        flash("Your password has been updated.", "success")
        return redirect(url_for('profile'))

    return render_template("change_password.html")


@app.route("/deep-ml", methods=["GET", "POST"])
@login_required
def deep_ml():

    prefill = {}
    if 'rerun_params' in session:
        prefill = session.pop('rerun_params', {})

    if request.method == "POST":
        selected = request.form.getlist("datasets")
        if len(selected) != 2:
            flash("Please select exactly two datasets.", "warning")
            return redirect(url_for("deep_ml"))

        dataset_path1 = os.path.join("app", "static", "data", selected[0])
        dataset_path2 = os.path.join("app", "static", "data", selected[1])

        try:
            results = run_combined_deepml_analysis(dataset_path1, dataset_path2)

            image_paths = [
                os.path.join("app", results["pca_path"].lstrip("/")),
                os.path.join("app", results["tsne_path"].lstrip("/")),
                os.path.join("app", results["cm_path"].lstrip("/"))
            ]
            pdf_path = os.path.join("app", results["pdf_path"].lstrip("/"))

            for img in image_paths:
                url = upload_to_cloudinary(img)
                db.session.add(DownloadHistory(user_id=current_user.id, file_name=os.path.basename(img), file_url=url, download_type="Deep ML", parameters=json.dumps({"type": "deepml", "datasets": selected})))

            pdf_url = upload_to_cloudinary(pdf_path, is_raw=True)
            db.session.add(DownloadHistory(user_id=current_user.id, file_name=os.path.basename(pdf_path), file_url=pdf_url, download_type="Deep ML", parameters=json.dumps({"type": "deepml", "datasets": selected})))

            db.session.commit()
            return render_template("deep_ml.html", results=results)
        except Exception as e:
            traceback.print_exc()
            flash("An error occurred during analysis.", "danger")
            return redirect(url_for("deep_ml"))

    return render_template("deep_ml.html", results=None, **prefill)


@app.route("/rerun/<int:history_id>")
@login_required
def rerun_task(history_id):
    history = DownloadHistory.query.get_or_404(history_id)

    if history.user_id != current_user.id:
        flash("Unauthorized access", "danger")
        return redirect(url_for("download_history"))

    if not history.parameters:
        flash("This entry has no saved parameters to rerun.", "warning")
        return redirect(url_for("download_history"))

    try:
        params = json.loads(history.parameters)
        task_type = params.get("type")

        if task_type == "interactive":
            session['rerun_params'] = params
            return redirect(url_for("interactive_query"))
        elif task_type == "advanced":
            session['rerun_params'] = params
            return redirect(url_for("advanced_analysis"))
        elif task_type == "deepml":
            session['rerun_params'] = params
            return redirect(url_for("deep_ml"))
        else:
            flash("Unknown task type", "warning")
    except Exception as e:
        flash(f"Error parsing parameters: {str(e)}", "danger")

    return redirect(url_for("download_history"))


@app.route("/delete/<int:history_id>", methods=["POST"])
@login_required
def delete_history_item(history_id):
    history = DownloadHistory.query.get_or_404(history_id)
    if history.user_id != current_user.id:
        flash("Unauthorized", "danger")
        return redirect(url_for("download_history"))

    db.session.delete(history)
    db.session.commit()
    flash("Download entry deleted.", "success")
    return redirect(url_for("download_history"))


@app.route("/request-admin", methods=["POST"])
@login_required
def request_admin():
    token = serializer.dumps(current_user.id, salt="admin-request")
    approve_url = url_for("confirm_admin", token=token, _external=True)
    deny_url = url_for("deny_admin", token=token, _external=True)

    send_email(
        subject="Approve Admin Access",
        recipients=["gcinsights.reset@gmail.com"],
        body=f"""
    Approve admin access for {current_user.email}: {approve_url}

    To deny the request, click here: {deny_url}
    """
    )
    flash("Admin access request sent for approval.", "info")
    return redirect(url_for("profile"))

@app.route("/confirm-admin/<token>")
def confirm_admin(token):
    try:
        user_id = serializer.loads(token, salt="admin-request", max_age=3600)
        user = User.query.get(user_id)
        user.role = "admin"
        db.session.commit()

        send_email(
            subject="Admin Access Approved",
            recipients=[user.email],
            body="✅ Your request for admin access has been approved. You can now access the admin dashboard."
        )

        flash(f"{user.email} is now an admin!", "success")
    except Exception as e:
        flash("Invalid or expired token.", "danger")
    return redirect(url_for("login"))

@app.route("/deny-admin/<token>")
def deny_admin(token):
    try:
        user_id = serializer.loads(token, salt="admin-request", max_age=3600)
        user = User.query.get(user_id)

        send_email(
            subject="Admin Access Denied",
            recipients=[user.email],
            body="❌ Your request for admin access has been denied by the GCInsights team."
        )

        flash(f"Denied admin access for {user.email}", "info")
    except Exception:
        flash("Invalid or expired token.", "danger")
    return redirect(url_for("login"))


@app.route("/admin")
@login_required
def admin_dashboard():
    if current_user.role != "admin":
        abort(403)

    users = User.query.all()
    stats = {}

    for user in users:
        downloads = DownloadHistory.query.filter_by(user_id=user.id).all()

        # Count per download_type
        interactive = sum(1 for d in downloads if d.download_type == "Interactive Query")
        advanced = sum(1 for d in downloads if d.download_type == "Advanced Analysis")
        deepml = sum(1 for d in downloads if d.download_type == "Deep ML")

        stats[user.id] = {
            "email": user.email,
            "username": user.username,
            "role": user.role,
            "city": user.city,
            "state": user.state,
            "country": user.country,
            "downloads": len(downloads),
            "interactive": interactive,
            "advanced": advanced,
            "deepml": deepml,
        }

    return render_template("admin_dashboard.html", users=users, stats=stats)



import smtplib
from email.mime.text import MIMEText

def send_email(subject, recipients, body):
    sender = "gcinsights.support@gmail.com"
    password = "buyu wiux dbwu wcrb"  # Use Gmail App Password

    msg = MIMEText(body)
    msg["Subject"] = subject
    msg["From"] = sender
    msg["To"] = ", ".join(recipients)

    with smtplib.SMTP_SSL("smtp.gmail.com", 465) as server:
        server.login(sender, password)
        server.sendmail(sender, recipients, msg.as_string())

@app.route('/about')
def about():
    return render_template('about.html')


if __name__ == "__main__":
    with app.app_context():
        db.create_all()
    app.run(debug=True)
    
