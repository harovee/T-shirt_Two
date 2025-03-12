from dotenv import load_dotenv
import os

load_dotenv()


class Config:
    BASE_URL = os.getenv('BASE_URL')
    RECOMMENDER_ENDPOINT = '/recommender/data'
    DEBUG = True
    LOGGING_LEVEL = 'DEBUG'
    NUM_RECOMMENDATIONS = 3
