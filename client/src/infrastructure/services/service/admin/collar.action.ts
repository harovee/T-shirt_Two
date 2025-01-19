import {
    createCollar,
    getListCollar,
    CollarRequest,
    getCollar,
    FindCollarRequest,
    getCollars,
    updateCollar
} from "@/infrastructure/services/api/admin/collar.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetCollars = (
    params: Ref<FindCollarRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCollars>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.collar.collarList, params],
        queryFn: () => getCollars(params),
        ...options,
    });
};

export const useGetListCollar = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListCollar>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.collar.collarList],
        queryFn: () => getListCollar(),
        ...options,
    });
};

export const useCreateCollar = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: CollarRequest) => createCollar(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.collar.collarList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.collar.collarList, "🚀 ~ CollarCreate ~ error:", error);
        },
    });
};


export const useGetCollar = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCollar>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.collar.collarList, id,],
        queryFn: () => getCollar(id.value),
        ...options,
    });
};


export const useUpdateCollar = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: CollarRequest; }) => updateCollar(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.collar.collarList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.collar.collarList + "🚀 ~ CollarUpdate ~ error:", error);
        },
    });
};