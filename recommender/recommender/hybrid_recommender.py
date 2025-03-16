from typing import List, Dict

from utils.logging_config import logger
from .collaborative_filter import CollaborativeFilter
from .content_filter import ContentFilter
from .data_processor import DataProcessor


class HybridRecommender:
    def __init__(self):
        self.data_processor = DataProcessor()
        self.collaborative_filter = CollaborativeFilter()
        self.content_filter = ContentFilter()
        self.is_trained = False

    def train(self, api_data: Dict) -> None:
        """Train both recommender systems"""
        try:
            # Process data
            self.data_processor.process_api_data(api_data)

            # Train collaborative filter
            user_product_matrix = self.data_processor.get_user_product_matrix()
            self.collaborative_filter.train(user_product_matrix)

            # Train content filter
            product_features = self.data_processor.get_product_features()
            self.content_filter.train(product_features)

            self.is_trained = True
            logger.info("Successfully trained hybrid recommender")
        except Exception as e:
            logger.error(f"Error training hybrid recommender: {str(e)}")
            raise

    def get_recommendations(self, user_id: str, n_recommendations: int = 3) -> List[str]:
        """Get hybrid recommendations combining both approaches"""
        try:
            if not self.is_trained:
                logger.error("Recommender not trained")
                return []

            # Get user features and purchase history
            user_features = self.data_processor.get_user_features(user_id)
            purchased_products = self.data_processor.orders_df[
                self.data_processor.orders_df['clientId'] == user_id
                ]['productVariantId'].unique().tolist()

            # Get recommendations from both systems
            collab_recs = self.collaborative_filter.get_recommendations(
                user_id, n_recommendations=n_recommendations
            )
            content_recs = self.content_filter.get_recommendations(
                user_features, purchased_products, n_recommendations=n_recommendations
            )

            # Combine recommendations (simple averaging)
            combined_recs = []
            remaining_slots = n_recommendations

            # Add collaborative recommendations first
            for rec in collab_recs:
                if rec not in purchased_products and rec not in combined_recs:
                    combined_recs.append(rec)
                    remaining_slots -= 1
                if remaining_slots == 0:
                    break

            # Fill remaining slots with content-based recommendations
            for rec in content_recs:
                if rec not in purchased_products and rec not in combined_recs:
                    combined_recs.append(rec)
                    remaining_slots -= 1
                if remaining_slots == 0:
                    break

            return combined_recs[:n_recommendations]
        except Exception as e:
            logger.error(f"Error getting hybrid recommendations: {str(e)}")
            return []
