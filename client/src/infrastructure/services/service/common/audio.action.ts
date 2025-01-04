import {useMutation, useQueryClient} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {AudioUploadRequest, uploadAudio} from "@/infrastructure/services/api/common/audio.api.ts";

export const useUploadAudio = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: AudioUploadRequest) => uploadAudio(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.common.upload.audio],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.common.upload.audio, "🚀 ~ fileUpload ~ error:", error);
        },
    });
};