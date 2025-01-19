import {
    createColor,
    getListColor,
    ColorRequest,
    getColor,
    FindColorRequest,
    getColors,
    updateColor
} from "@/infrastructure/services/api/admin/color.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetColors = (
    params: Ref<FindColorRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getColors>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.color.colorList, params],
        queryFn: () => getColors(params),
        ...options,
    });
};

export const useGetListColor = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListColor>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.color.colorList],
        queryFn: () => getListColor(),
        ...options,
    });
};

export const useCreateColor = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ColorRequest) => createColor(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.color.colorList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.color.colorList, "🚀 ~ ColorCreate ~ error:", error);
        },
    });
};


export const useGetColor = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getColor>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.color.colorList, id,],
        queryFn: () => getColor(id.value),
        ...options,
    });
};


export const useUpdateColor = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: ColorRequest; }) => updateColor(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.color.colorList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.color.colorList + "🚀 ~ ColorUpdate ~ error:", error);
        },
    });
};