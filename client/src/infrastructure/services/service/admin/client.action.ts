import {
    changeClientAddressDefault,
    changeStatusClient,
    ClientAddressRequest,
    ClientAddressRequestCreate,
    ClientRequest,
    createClient,
    createClientAddress,
    createClientAddressMo,
    FindClientRequest,
    getClientAddressesResponseByClientId,
    getClientById,
    getClients,
    getDistrictsByProvinceId,
    getProvinces,
    getWardsByDistrictId,
    updateAvatarClient,
    updateClient,
    updateClientAddress
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

export const useCreateClientAddressMo = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ClientAddressRequestCreate) => createClientAddressMo(data),
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

export const useGetProvinces = (
    options?: any,
): UseQueryReturnType<Awaited<ReturnType<typeof getProvinces>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.address.province],
        queryFn: () => getProvinces(),
        ...options,
    });
};

export const useGetDistrictsByProvinceIdQuery = (
    provinceId: number,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getDistrictsByProvinceId>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.address.district, provinceId],
        queryFn: () => getDistrictsByProvinceId(provinceId),
        ...options,
    });
};

export const useGetWardsByDistrictIdQuery = (districtId: number, options?: any): UseQueryReturnType<Awaited<ReturnType<typeof getWardsByDistrictId>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.address.ward, districtId],
        queryFn: () => getWardsByDistrictId(districtId),
        ...options,
    });
};

export const useGetDistrictsByProvinceId = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (provinceId: number) => getDistrictsByProvinceId(provinceId),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.client.address.districtM, 'useMutation'],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.address.district, "🚀 ~ address.district get ~ error:", error);
        },
    });
};

export const useGetWardsByDistrictId = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (districtId: number) => getWardsByDistrictId(districtId),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.client.address.wardM],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.address.ward, "🚀 ~ address.ward get ~ error:", error);
        },
    });
};

export const useCreateClientAddress = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ClientAddressRequest) => createClientAddress(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.client.address.addressList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.address.addressList, "🚀 ~ clientAddressCreate ~ error:", error);
        },
    });
};

export const useUpdateClientAddress = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({addressId, data,}: {
            addressId: string;
            data: ClientAddressRequest;
        }) => updateClientAddress(addressId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.client.address.addressList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.address.addressList + "🚀 ~ useUpdateClientAddress ~ error:", error);
        },
    });
};

export const useChangeClientAddressDefault = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (addressId: string) => changeClientAddressDefault(addressId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.client.address.addressList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.client.address.addressList + "🚀 ~ useChangeClientAddressDefault ~ error:", error);
        },
    });
};


export const useGetClientAddressesByClientId = (
    clientId: string,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getClientAddressesResponseByClientId>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.address.addressList, clientId],
        queryFn: () => getClientAddressesResponseByClientId(clientId),
        ...options,
    });
};