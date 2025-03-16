from typing import Dict, List, Any, Optional
import logging

logger = logging.getLogger(__name__)


def validate_user_data(user_data: Dict) -> bool:
    """Validate user data from API response"""
    try:
        required_fields = ['id']
        return all(field in user_data for field in required_fields)
    except Exception as e:
        logger.error(f"Error validating user data: {str(e)}")
        return False


def validate_product_data(product_data: Dict) -> bool:
    """Validate product data from API response"""
    try:
        required_fields = ['id', 'productVariantId', 'name', 'category', 'price']
        return all(field in product_data for field in required_fields)
    except Exception as e:
        logger.error(f"Error validating product data: {str(e)}")
        return False


def validate_order_history(order_data: Dict) -> bool:
    """Validate order history data from API response"""
    try:
        required_fields = ['clientId', 'productVariantId', 'purchaseTimestamp']
        return all(field in order_data for field in required_fields)
    except Exception as e:
        logger.error(f"Error validating order history: {str(e)}")
        return False


def validate_api_response(response_data: Dict) -> bool:
    """Validate complete API response"""
    try:
        if not response_data.get('success'):
            return False

        required_sections = ['users', 'products', 'orderHistories']
        return all(section in response_data.get('data', {}) for section in required_sections)
    except Exception as e:
        logger.error(f"Error validating API response: {str(e)}")
        return False
