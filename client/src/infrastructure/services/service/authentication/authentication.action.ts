import {
    login,
    LoginForm,
    register,
    RegisterForm
} from "@/infrastructure/services/api/authentication/authentication.api.ts";
import {useMutation} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";

export const useLogin = () => {
    return useMutation({
        mutationFn: (params: LoginForm) => login(params),
        onSuccess: () => {
        },
        onError: (error: any) => {
            console.log(queryKey.authentication.login, "🚀 ~ address.district get ~ error:", error);
        },
    });
};

export const useRegister = () => {
    return useMutation({
        mutationFn: (params: RegisterForm) => register(params),
        onSuccess: () => {
        },
        onError: (error: any) => {
            console.log(queryKey.authentication.register, "🚀 ~ address.district get ~ error:", error);
        },
    });
};