import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { Ref } from "vue";
import {getListNotification, deleteNotification, deleteAllNotification } from "../api/notification.api.ts";


export const useGetListNotification = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListNotification>>, Error> => {
    return useQuery({
        queryKey: [queryKey.websocket.notification.notification],
        queryFn: () => getListNotification(),
        ...options,
    });
};

export const useDeleteNotification = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: string) => deleteNotification(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.websocket.notification.notification], });
        },
        onError: (error: any) => {
            console.log(queryKey.websocket.notification.notification + "🚀 ~ order detail ~ error:", error);
        },
    });
};

export const useDeleteAllNotification = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: () => deleteAllNotification(),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.websocket.notification.notification], });
        },
        onError: (error: any) => {
            console.log(queryKey.websocket.notification.notification + "🚀 ~ order detail ~ error:", error);
        },
    });
};