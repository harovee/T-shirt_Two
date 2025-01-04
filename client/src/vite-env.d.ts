/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_BASE_URL_CLIENT: number;
    readonly VITE_BASE_URL_SERVER: number;
    readonly VITE_PROXY_TARGET: string;
    readonly VITE_APP_ENV: string
    // add more environment variables as needed
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}
