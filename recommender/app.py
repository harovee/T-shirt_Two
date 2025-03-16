from flask import Flask
from api.routes import api_bp
from utils.logging_config import setup_logging
import os

def create_app():
    app = Flask(__name__)
    app.secret_key = os.environ.get("SESSION_SECRET", "your-secret-key")

    # Register blueprints
    app.register_blueprint(api_bp, url_prefix='/api')

    # Setup logging
    setup_logging()

    return app


app = create_app()
