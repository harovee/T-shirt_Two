import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
from typing import List
from utils.logging_config import logger


class ContentFilter:
    def __init__(self):
        self.product_features = None
        self.product_similarity = None

    def train(self, product_features: pd.DataFrame) -> None:
        """Train the content-based filter using product features"""
        try:
            self.product_features = product_features
            self.product_similarity = cosine_similarity(product_features)
            logger.info("Successfully trained content filter")
        except Exception as e:
            logger.error(f"Error training content filter: {str(e)}")
            raise

    def get_recommendations(self, user_features: np.ndarray, purchased_products: List[str],
                            n_recommendations: int = 3) -> List[str]:
        """Get product recommendations based on content similarity"""
        try:
            if self.product_features is None:
                return []

            # Calculate similarity between user preferences and products
            recommendations = []
            purchased_indices = [self.product_features.index.get_loc(pid) for pid in purchased_products
                                 if pid in self.product_features.index]

            if not purchased_indices:
                return []

            # Get average similarity scores for purchased products
            avg_similarity = np.mean([self.product_similarity[idx] for idx in purchased_indices], axis=0)

            # Get top similar products
            similar_indices = np.argsort(avg_similarity)[::-1]

            for idx in similar_indices:
                product_id = self.product_features.index[idx]
                if product_id not in purchased_products:
                    recommendations.append(product_id)
                    if len(recommendations) >= n_recommendations:
                        break

            return recommendations[:n_recommendations]
        except Exception as e:
            logger.error(f"Error getting content-based recommendations: {str(e)}")
            return []
