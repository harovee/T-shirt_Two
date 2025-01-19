import {
    changeStatusClient,
    ClientRequest,
    createClient,
    FindClientRequest,
    getClientById,
    getClients,
    updateAvatarClient,
    updateClient
} from "@/infrastructure/services/api/admin/client.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetClients = (
    params: Ref<FindClientRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getClients>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.clientList, params],
        queryFn: () => getClients(params),
        ...options,
    });
};

export const useCreateClient = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ClientRequest) => createClient(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.client.clientList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.clientList, "🚀 ~ clientCreate ~ error:", error);
        },
    });
};


export const useGetClientById = (
    clientId: string, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getClientById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.clientDetail, clientId,],
        queryFn: () => getClientById(clientId),
        ...options,
    });
};


export const useUpdateClient = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({clientId, data,}: { clientId: string; data: ClientRequest; }) => updateClient(clientId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.client.clientList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.clientList + "🚀 ~ clientUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusClient = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (clientId: string) => changeStatusClient(clientId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.client.clientList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.clientList + "🚀 ~ clientDelete ~ error:", error);
        },
    });
};

export const useUpdateClientAvatar = () => {
    const query = useQueryClient();
    return useMutation({
        mutationFn: ({clientId, data}: {
            clientId: string;
            data: ClientRequest;
        }) => updateAvatarClient(clientId, data),
        onSuccess: () => {
            query.invalidateQueries({queryKey: [queryKey.admin.client.clientDetail],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.clientDetail + "🚀 ~ clientUpdate ~ error:", error);
        },
    });
}