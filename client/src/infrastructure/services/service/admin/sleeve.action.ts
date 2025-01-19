import {
    createSleeve,
    getListSleeve,
    SleeveRequest,
    getSleeve,
    FindSleeveRequest,
    getSleeves,
    updateSleeve
} from "@/infrastructure/services/api/admin/sleeve.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetSleeves = (
    params: Ref<FindSleeveRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSleeves>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sleeve.sleeveList, params],
        queryFn: () => getSleeves(params),
        ...options,
    });
};

export const useGetListSleeve = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListSleeve>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sleeve.sleeveList],
        queryFn: () => getListSleeve(),
        ...options,
    });
};

export const useCreateSleeve = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: SleeveRequest) => createSleeve(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.sleeve.sleeveList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sleeve.sleeveList, "🚀 ~ SleeveCreate ~ error:", error);
        },
    });
};


export const useGetSleeve = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSleeve>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sleeve.sleeveList, id,],
        queryFn: () => getSleeve(id.value),
        ...options,
    });
};


export const useUpdateSleeve = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: SleeveRequest; }) => updateSleeve(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.sleeve.sleeveList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sleeve.sleeveList + "🚀 ~ SleeveUpdate ~ error:", error);
        },
    });
};