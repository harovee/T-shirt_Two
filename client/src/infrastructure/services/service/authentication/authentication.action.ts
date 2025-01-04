import {
    login,
    LoginForm,
    register,
    RegisterForm
} from "@/infrastructure/services/api/authentication/authentication.api.ts";
import {useQuery, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";

export const useLogin = (
    params: LoginForm,
    options?: any
): UseQueryReturnType<
    Awaited<ReturnType<typeof login>>,
    Error
> => {
    return useQuery({
        queryKey: [queryKey.authentication.login],
        queryFn: () => login(params),
        ...options
    });
};

export const useRegister = (
    params: RegisterForm,
    options?: any
): UseQueryReturnType<
    Awaited<ReturnType<typeof register>>,
    Error
> => {
    return useQuery({
        queryKey: [queryKey.authentication.register],
        queryFn: () => register(params),
        ...options
    });
};