import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path";
import {
    ACCESS_TOKEN_STORAGE_KEY,
    REFRESH_TOKEN_STORAGE_KEY,
    USER_INFO_STORAGE_KEY,
} from "@/infrastructure/constants/storageKey";
import {API_URL, PREFIX_API_REFRESH} from "@/infrastructure/constants/url";
import {DefaultResponse} from "@/infrastructure/types/api.common";
import {localStorageAction} from "@/utils/storage";
import {getUserInformation} from "@/utils/token.helper";
import router from "@/infrastructure/routes/router.ts";
import axios, {AxiosResponse} from "axios";

const request = axios.create({
    baseURL: `${API_URL}`,
});

request.interceptors.request.use((config) => {
    const accessToken = localStorageAction.get(ACCESS_TOKEN_STORAGE_KEY);
    if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
});

request.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        if (
            error.response &&
            error.response.status === 401 &&
            !originalRequest._retry &&
            window.location.pathname !== ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.path
        ) {
            originalRequest._retry = true;

            const refreshToken = localStorageAction.get(REFRESH_TOKEN_STORAGE_KEY);
            if (refreshToken) {
                try {
                    const response = await axios.post(`${PREFIX_API_REFRESH}`, {
                        refreshToken,
                    }) as AxiosResponse<DefaultResponse<{ accessToken: string; refreshToken: string }>>;
                    const newAccessToken = response.data.data.accessToken;
                    const newRefreshToken = response.data.data.refreshToken;
                    localStorageAction.set(ACCESS_TOKEN_STORAGE_KEY, newAccessToken);
                    localStorageAction.set(REFRESH_TOKEN_STORAGE_KEY, newRefreshToken);
                    localStorageAction.set(USER_INFO_STORAGE_KEY, getUserInformation(newAccessToken));
                    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                    return request(originalRequest);
                } catch (refreshError) {
                    console.log("🚀 ~ refreshError:", refreshError)
                }
            }

            localStorageAction.remove(ACCESS_TOKEN_STORAGE_KEY);
            localStorageAction.remove(REFRESH_TOKEN_STORAGE_KEY);
            localStorageAction.remove(USER_INFO_STORAGE_KEY);
            await router.push(ROUTES_CONSTANTS.AUTHENTICATION.path);
        }

        return Promise.reject(error);
    }
);

export default request;
