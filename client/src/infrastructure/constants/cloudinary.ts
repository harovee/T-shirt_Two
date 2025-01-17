const { VITE_CLOUDINARY_CLOUD_NAME } = import.meta.env || {};

const { VITE_CLOUDINARY_KEY_NAME } = import.meta.env || {};

const { VITE_CLOUDINARY_API_KEY } = import.meta.env || {};

const { VITE_CLOUDINARY_API_SECRET } = import.meta.env || {};

const { VITE_CLOUDINARY_UPLOAD_PRESET } = import.meta.env || {};

export const CLOUDINARY_CLOUD_NAME = `${VITE_CLOUDINARY_CLOUD_NAME}` as string;

export const CLOUDINARY_KEY_NAME = `${VITE_CLOUDINARY_KEY_NAME}` as string;

export const CLOUDINARY_API_KEY = `${VITE_CLOUDINARY_API_KEY}` as string;

export const CLOUDINARY_API_SECRET = `${VITE_CLOUDINARY_API_SECRET}` as string;

export const CLOUDINARY_UPLOAD_PRESET = `${VITE_CLOUDINARY_UPLOAD_PRESET}` as string;