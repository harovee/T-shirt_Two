import requests
from flask import Blueprint, jsonify

from config import Config
from recommender.hybrid_recommender import HybridRecommender
from utils.logging_config import logger
from utils.validation import validate_api_response

api_bp = Blueprint('api', __name__)
recommender = HybridRecommender()


@api_bp.route('/recommendations/<user_id>', methods=['GET'])
def get_recommendations(user_id):
    """Get product recommendations for a user"""
    try:
        # Fetch latest data from Java API
        response = requests.get(f"{Config.BASE_URL}{Config.RECOMMENDER_ENDPOINT}")
        data = response.json()

        if not validate_api_response(data):
            logger.error("Invalid API response")
            return jsonify({
                'success': False,
                'message': 'Invalid data received from API',
                'recommendations': []
            }), 400

        # Train recommender with latest data
        recommender.train(data)

        # Get recommendations
        recommendations = recommender.get_recommendations(
            user_id,
            n_recommendations=Config.NUM_RECOMMENDATIONS
        )

        return jsonify({
            'success': True,
            'message': 'Recommendations generated successfully',
            'recommendations': recommendations
        })

    except requests.RequestException as e:
        logger.error(f"API request error: {str(e)}")
        return jsonify({
            'success': False,
            'message': 'Error fetching data from API',
            'recommendations': []
        }), 503

    except Exception as e:
        logger.error(f"Recommendation error: {str(e)}")
        return jsonify({
            'success': False,
            'message': 'Error generating recommendations',
            'recommendations': []
        }), 500
