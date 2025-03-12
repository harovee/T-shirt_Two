import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
from typing import List
from utils.logging_config import logger


class CollaborativeFilter:
    def __init__(self):
        self.user_product_matrix = None
        self.similarity_matrix = None

    def train(self, user_product_matrix: pd.DataFrame) -> None:
        """Train the collaborative filter using user-product interactions"""
        try:
            self.user_product_matrix = user_product_matrix
            self.similarity_matrix = cosine_similarity(user_product_matrix)
            logger.info("Successfully trained collaborative filter")
        except Exception as e:
            logger.error(f"Error training collaborative filter: {str(e)}")
            raise

    def get_recommendations(self, user_id: str, n_recommendations: int = 3) -> List[str]:
        """Get product recommendations based on collaborative filtering"""
        try:
            if user_id not in self.user_product_matrix.index:
                return []

            user_idx = self.user_product_matrix.index.get_loc(user_id)

            # Get similar users
            similar_users = self.similarity_matrix[user_idx]
            similar_users_indices = np.argsort(similar_users)[-5:]  # Top 5 similar users

            # Get products bought by similar users
            recommendations = []
            user_products = set(self.user_product_matrix.columns[self.user_product_matrix.loc[user_id] > 0])

            for idx in similar_users_indices:
                similar_user_id = self.user_product_matrix.index[idx]
                if similar_user_id == user_id:
                    continue

                similar_user_products = self.user_product_matrix.columns[
                    self.user_product_matrix.loc[similar_user_id] > 0
                    ]

                for product in similar_user_products:
                    if product not in user_products:
                        recommendations.append(product)
                        if len(recommendations) >= n_recommendations:
                            return recommendations

            return recommendations[:n_recommendations]
        except Exception as e:
            logger.error(f"Error getting collaborative recommendations: {str(e)}")
            return []
