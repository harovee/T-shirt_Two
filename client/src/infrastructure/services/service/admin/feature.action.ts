import {
    createFeature,
    getListFeature,
    FeatureRequest,
    getFeature,
    FindFeatureRequest,
    getFeatures,
    updateFeature
} from "@/infrastructure/services/api/admin/feature.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetFeatures = (
    params: Ref<FindFeatureRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getFeatures>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.feature.featureList, params],
        queryFn: () => getFeatures(params),
        ...options,
    });
};

export const useGetListFeature = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListFeature>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.feature.featureList],
        queryFn: () => getListFeature(),
        ...options,
    });
};

export const useCreateFeature = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: FeatureRequest) => createFeature(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.feature.featureList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.feature.featureList, "🚀 ~ FeatureCreate ~ error:", error);
        },
    });
};


export const useGetFeature = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getFeature>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.feature.featureList, id,],
        queryFn: () => getFeature(id.value),
        ...options,
    });
};


export const useUpdateFeature = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: FeatureRequest; }) => updateFeature(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.feature.featureList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.feature.featureList + "🚀 ~ FeatureUpdate ~ error:", error);
        },
    });
};