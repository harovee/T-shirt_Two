import pandas as pd
import numpy as np
from typing import Dict, List, Tuple
from utils.logging_config import logger


class DataProcessor:
    def __init__(self):
        self.users_df = None
        self.products_df = None
        self.orders_df = None

    def process_api_data(self, api_data: Dict) -> None:
        """Process raw API data into DataFrames"""
        try:
            data = api_data['data']

            # Process users
            self.users_df = pd.DataFrame(data['users'])
            self.users_df['age'] = pd.to_numeric(self.users_df['age'], errors='coerce')
            self.users_df['gender'] = self.users_df['gender'].map({'true': 1, 'false': 0})

            # Process products
            self.products_df = pd.DataFrame(data['products'])
            self.products_df['price'] = pd.to_numeric(self.products_df['price'], errors='coerce')

            # Process order histories
            self.orders_df = pd.DataFrame(data['orderHistories'])
            self.orders_df['purchaseTimestamp'] = pd.to_datetime(self.orders_df['purchaseTimestamp'], unit='ms')

            logger.info("Successfully processed API data")
        except Exception as e:
            logger.error(f"Error processing API data: {str(e)}")
            raise

    def get_user_features(self, user_id: str) -> np.ndarray:
        """Extract user features for content-based filtering"""
        try:
            user = self.users_df[self.users_df['id'] == user_id].iloc[0]
            features = [
                user['age'] if pd.notna(user['age']) else 0,
                user['gender'] if pd.notna(user['gender']) else 0
            ]
            return np.array(features)
        except Exception as e:
            logger.error(f"Error getting user features: {str(e)}")
            return np.zeros(2)

    def get_product_features(self) -> pd.DataFrame:
        """Extract product features for content-based filtering"""
        try:
            # Create feature columns
            feature_df = pd.get_dummies(self.products_df[['category', 'brand', 'material', 'feature']])
            feature_df['price'] = self.products_df['price']

            # Normalize price
            feature_df['price'] = (feature_df['price'] - feature_df['price'].mean()) / feature_df['price'].std()

            return feature_df
        except Exception as e:
            logger.error(f"Error getting product features: {str(e)}")
            return pd.DataFrame()

    def get_user_product_matrix(self) -> pd.DataFrame:
        """Create user-product interaction matrix for collaborative filtering"""
        try:
            # Create interaction matrix
            matrix = pd.pivot_table(
                self.orders_df,
                values='quantity',
                index='clientId',
                columns='productVariantId',
                aggfunc='sum',
                fill_value=0
            )
            return matrix
        except Exception as e:
            logger.error(f"Error creating user-product matrix: {str(e)}")
            return pd.DataFrame()
