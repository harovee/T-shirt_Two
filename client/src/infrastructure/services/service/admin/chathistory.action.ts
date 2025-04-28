import {  useQuery, UseQueryReturnType } from "@tanstack/vue-query";
import { getChatHistories } from "../../api/admin/chathistory.api";
import { queryKey } from "@/infrastructure/constants/queryKey";
import { Ref } from "vue";

export const useGetChatHistory = (
    roomId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getChatHistories>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.livechat.chatHistory, roomId,],
        queryFn: () => getChatHistories(roomId.value),
        ...options,
    })
}

// export const useMarkMessageAsRead = () => {
//     const queryClient = useQueryClient();
//     return useMutation({
//         mutationFn: ({roomId}: { roomId: string}) => markMessageAsRead(roomId),
//         onSuccess: () => {
//             queryClient.invalidateQueries({queryKey: [queryKey.admin.livechat.chatHistory],});
//         },
//         onError: (error: any) => {
//             console.log(queryKey.admin.livechat.chatHistory + "🚀 ~ messUpdate ~ error:", error);
//         },
//     });
// };