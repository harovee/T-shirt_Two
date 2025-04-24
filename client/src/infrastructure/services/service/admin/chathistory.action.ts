import { useQuery, UseQueryReturnType } from "@tanstack/vue-query";
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