"""
Recommender package initialization.
This module initializes the recommendation system components and makes them available for import.
"""

from .hybrid_recommender import HybridRecommender
from .collaborative_filter import CollaborativeFilter
from .content_filter import ContentFilter
from .data_processor import DataProcessor

__all__ = [
    'HybridRecommender',
    'CollaborativeFilter',
    'ContentFilter',
    'DataProcessor'
]

# Version info
__version__ = '1.0.0'
