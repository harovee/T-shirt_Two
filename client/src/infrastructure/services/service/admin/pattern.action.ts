import {
    createPattern,
    getListPattern,
    PatternRequest,
    getPattern,
    FindPatternRequest,
    getPatterns,
    updatePattern
} from "@/infrastructure/services/api/admin/pattern.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetPatterns = (
    params: Ref<FindPatternRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getPatterns>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pattern.patternList, params],
        queryFn: () => getPatterns(params),
        ...options,
    });
};

export const useGetListPattern = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListPattern>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pattern.patternList],
        queryFn: () => getListPattern(),
        ...options,
    });
};

export const useCreatePattern = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: PatternRequest) => createPattern(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.pattern.patternList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pattern.patternList, "🚀 ~ PatternCreate ~ error:", error);
        },
    });
};


export const useGetPattern = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getPattern>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pattern.patternList, id,],
        queryFn: () => getPattern(id.value),
        ...options,
    });
};


export const useUpdatePattern = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: PatternRequest; }) => updatePattern(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.pattern.patternList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pattern.patternList + "🚀 ~ PatternUpdate ~ error:", error);
        },
    });
};