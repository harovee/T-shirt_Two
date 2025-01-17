/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_BASE_URL_CLIENT: number;
    readonly VITE_BASE_URL_SERVER: number;
    readonly VITE_PROXY_TARGET: string;
    readonly VITE_APP_ENV: string
    readonly CLOUDINARY_CLOUD_NAME: string
    readonly CLOUDINARY_KEY_NAME: string
    readonly CLOUDINARY_API_KEY: number
    readonly CLOUDINARY_API_SECRET: string
    readonly CLOUDINARY_UPLOAD_PRESET: string
    // add more environment variables as needed
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
