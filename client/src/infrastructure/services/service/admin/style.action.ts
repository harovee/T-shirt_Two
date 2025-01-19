import {
    createStyle,
    getListStyle,
    StyleRequest,
    getStyle,
    FindStyleRequest,
    getStyles,
    updateStyle
} from "@/infrastructure/services/api/admin/style.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetStyles = (
    params: Ref<FindStyleRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getStyles>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.style.styleList, params],
        queryFn: () => getStyles(params),
        ...options,
    });
};

export const useGetListStyle = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListStyle>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.style.styleList],
        queryFn: () => getListStyle(),
        ...options,
    });
};

export const useCreateStyle = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: StyleRequest) => createStyle(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.style.styleList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.style.styleList, "🚀 ~ StyleCreate ~ error:", error);
        },
    });
};


export const useGetStyle = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getStyle>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.style.styleList, id,],
        queryFn: () => getStyle(id.value),
        ...options,
    });
};


export const useUpdateStyle = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: StyleRequest; }) => updateStyle(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.style.styleList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.style.styleList + "🚀 ~ StyleUpdate ~ error:", error);
        },
    });
};