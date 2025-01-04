import {
    changeStatusUser,
    createUser,
    FindUserRequest,
    getUser,
    getUsers,
    updateUser,
    UserRequest
} from "@/infrastructure/services/api/admin/user.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetUsers = (
    params: Ref<FindUserRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getUsers>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.user.userList, params],
        queryFn: () => getUsers(params),
        ...options,
    });
};

export const useCreateUser = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: UserRequest) => createUser(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.user.userList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.user.userList, "🚀 ~ userCreate ~ error:", error);
        },
    });
};


export const useGetUser = (
    userId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getUser>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.user.userDetail, userId,],
        queryFn: () => getUser(userId),
        ...options,
    });
};


export const useUpdateUser = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({userId, data,}: { userId: string; data: UserRequest; }) => updateUser(userId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.user.userList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.user.userList + "🚀 ~ userUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusUser = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (userId: string) => changeStatusUser(userId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.user.userList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.user.userList + "🚀 ~ userDelete ~ error:", error);
        },
    });
};