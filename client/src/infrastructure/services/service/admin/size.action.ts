import {
    createSize,
    getListSize,
    SizeRequest,
    getSize,
    FindSizeRequest,
    getSizes,
    updateSize
} from "@/infrastructure/services/api/admin/size.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetSizes = (
    params: Ref<FindSizeRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSizes>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.size.sizeList, params],
        queryFn: () => getSizes(params),
        ...options,
    });
};

export const useGetListSize = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListSize>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.size.sizeList],
        queryFn: () => getListSize(),
        ...options,
    });
};

export const useCreateSize = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: SizeRequest) => createSize(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.size.sizeList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.size.sizeList, "🚀 ~ SizeCreate ~ error:", error);
        },
    });
};


export const useGetSize = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSize>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.size.sizeList, id,],
        queryFn: () => getSize(id.value),
        ...options,
    });
};


export const useUpdateSize = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: SizeRequest; }) => updateSize(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.size.sizeList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.size.sizeList + "🚀 ~ SizeUpdate ~ error:", error);
        },
    });
};