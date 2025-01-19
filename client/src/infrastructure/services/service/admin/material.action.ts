import {
    createMaterial,
    getListMaterial,
    MaterialRequest,
    getMaterial,
    FindMaterialRequest,
    getMaterials,
    updateMaterial
} from "@/infrastructure/services/api/admin/material.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetMaterials = (
    params: Ref<FindMaterialRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getMaterials>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.material.materialList, params],
        queryFn: () => getMaterials(params),
        ...options,
    });
};

export const useGetListMaterial = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListMaterial>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.material.materialList],
        queryFn: () => getListMaterial(),
        ...options,
    });
};

export const useCreateMaterial = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: MaterialRequest) => createMaterial(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.material.materialList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.material.materialList, "🚀 ~ MaterialCreate ~ error:", error);
        },
    });
};


export const useGetMaterial = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getMaterial>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.material.materialList, id,],
        queryFn: () => getMaterial(id.value),
        ...options,
    });
};


export const useUpdateMaterial = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: MaterialRequest; }) => updateMaterial(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.material.materialList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.material.materialList + "🚀 ~ MaterialUpdate ~ error:", error);
        },
    });
};